package com.cemenghui.news.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cemenghui.news.constants.NewsConstants;
import com.cemenghui.news.dto.*;
import com.cemenghui.news.dto.NewsVO;
import com.cemenghui.news.dto.PageResult;
import com.cemenghui.news.entity.News;
import com.cemenghui.news.entity.UserOperationLog;
import com.cemenghui.news.entity.UserViewLog;
import com.cemenghui.news.enums.NewsStatus;
import com.cemenghui.news.enums.OperationType;
import com.cemenghui.news.exception.BusinessException;
import com.cemenghui.news.exception.NewsNotFoundException;
import com.cemenghui.news.exception.UnauthorizedException;
import com.cemenghui.news.mapper.NewsMapper;
import com.cemenghui.news.service.AuthorizationService;
import com.cemenghui.news.service.LogService;
import com.cemenghui.news.service.NewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@CacheConfig(cacheNames = "news")
public class NewsServiceImpl implements NewsService {

    private final NewsMapper newsMapper;
    private final AuthorizationService authorizationService;
    private final LogService logService;
    // If you have a UserService to get total and active users, inject it here
    // private final UserService userService;

    @Override
    @Cacheable(key = "'search:' + #searchRequest.page + ':' + #searchRequest.pageSize + ':' + #searchRequest.keyword + ':' + #searchRequest.category",
            unless = "#result.records.isEmpty()")
    public PageResult<NewsVO> searchNews(SearchRequest searchRequest) {
        Integer statusToSearch = NewsStatus.APPROVED.getCode();
        // Use getPageSize() for SearchRequest as it doesn't have getValidPageSize()
        Page<News> page = new Page<>(searchRequest.getPage(), searchRequest.getPageSize());
        Page<News> result = newsMapper.findByCondition(page, searchRequest, statusToSearch, null);

        List<NewsVO> voList = result.getRecords().stream()
                .map(news -> convertToVO(news, authorizationService.getCurrentUserId()))
                .collect(Collectors.toList());

        return new PageResult<>(voList, result.getTotal(), searchRequest.getPage(), searchRequest.getPageSize());
    }

    @Override
    public NewsVO getNewsDetail(Long newsId) {
        News news = newsMapper.selectById(newsId);
        if (news == null || news.getIsDeleted() == 1) {
            throw new NewsNotFoundException(newsId);
        }

        Long currentUserId = authorizationService.getCurrentUserId();
        boolean isAdmin = authorizationService.isAdmin(currentUserId);
        boolean isOwner = news.isOwner(currentUserId);

        if (news.isApproved() || isAdmin || isOwner) {
            recordViewCount(newsId);
            newsMapper.updateViewCount(newsId);
            news.incrementViewCount();
            // 浏览量更新后清除该新闻的详情缓存
            evictNewsDetailCache(newsId);
            return convertToVO(news, currentUserId);
        } else {
            throw new UnauthorizedException("无权查看此新闻详情");
        }
    }

    @Override
    @Transactional
    @CacheEvict(allEntries = true)
    public Long publishNews(NewsRequest newsRequest) {
        Long currentUserId = authorizationService.getCurrentUserId();
        authorizationService.checkEnterprisePermission(currentUserId);

        News news = new News();
        BeanUtils.copyProperties(newsRequest, news);
        news.setUserId(currentUserId);
        news.setStatus(NewsStatus.PENDING.getCode());
        news.setCreateTime(LocalDateTime.now());
        news.setUpdateTime(LocalDateTime.now());
        news.setViewCount(0);
        news.setIsDeleted(0);

        int result = newsMapper.insert(news);
        if (result > 0) {
            recordOperation(currentUserId, OperationType.PUBLISH, news.getId(), "发布新闻: " + news.getTitle(), null, news);
            return news.getId();
        }
        throw new BusinessException("发布新闻失败");
    }

    @Override
    @Transactional
    @CacheEvict(allEntries = true)
    public Long adminPublishNews(NewsRequest newsRequest) {
        Long currentUserId = authorizationService.getCurrentUserId();
        authorizationService.checkAdminPermission(currentUserId);

        News news = new News();
        BeanUtils.copyProperties(newsRequest, news);
        news.setUserId(currentUserId);
        news.setStatus(NewsStatus.APPROVED.getCode());
        news.setCreateTime(LocalDateTime.now());
        news.setUpdateTime(LocalDateTime.now());
        news.setViewCount(0);
        news.setIsDeleted(0);

        int result = newsMapper.insert(news);
        if (result > 0) {
            recordOperation(currentUserId, OperationType.PUBLISH, news.getId(), "管理员发布新闻: " + news.getTitle(), null, news);
            return news.getId();
        }
        throw new BusinessException("管理员发布新闻失败");
    }

    @Override
    public PageResult<NewsVO> getMyNewsList(PageRequest pageRequest) {
        Long currentUserId = authorizationService.getCurrentUserId();
        // Use getValidPageSize() for PageRequest
        Page<News> page = new Page<>(pageRequest.getPage(), pageRequest.getValidPageSize());
        Page<News> result = newsMapper.findByUserId(page, currentUserId);

        List<NewsVO> voList = result.getRecords().stream()
                .map(news -> convertToVO(news, currentUserId))
                .collect(Collectors.toList());

        return new PageResult<>(voList, result.getTotal(), pageRequest.getPage(), pageRequest.getValidPageSize());
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(key = "'detail:' + #newsId"),
            @CacheEvict(key = "'search:*'", allEntries = true)
    })
    public boolean editNews(Long newsId, NewsRequest newsRequest) {
        Long currentUserId = authorizationService.getCurrentUserId();

        if (!authorizationService.canEditNews(currentUserId, newsId)) {
            throw new UnauthorizedException("无权编辑此新闻");
        }

        News existingNews = newsMapper.selectById(newsId);
        if (existingNews == null || existingNews.getIsDeleted() == 1) {
            throw new NewsNotFoundException(newsId);
        }

        if (authorizationService.isEnterprise(currentUserId) && !authorizationService.isAdmin(currentUserId)) {
            if (!existingNews.canEdit()) {
                throw new BusinessException("当前状态的新闻不允许编辑");
            }
            existingNews.setStatus(NewsStatus.PENDING.getCode());
        }

        News oldNews = new News();
        BeanUtils.copyProperties(existingNews, oldNews);

        BeanUtils.copyProperties(newsRequest, existingNews, "id", "userId", "createTime", "viewCount", "status");
        existingNews.setUpdateTime(LocalDateTime.now());

        int result = newsMapper.updateById(existingNews);
        if (result > 0) {
            recordOperation(currentUserId, OperationType.EDIT, newsId, "编辑新闻: " + existingNews.getTitle(), oldNews, existingNews);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(key = "'detail:' + #newsId"),
            @CacheEvict(key = "'search:*'", allEntries = true),
            @CacheEvict(key = "'popular:*'", allEntries = true)
    })
    public boolean deleteNews(Long newsId) {
        Long currentUserId = authorizationService.getCurrentUserId();

        if (!authorizationService.canDeleteNews(currentUserId, newsId)) {
            throw new UnauthorizedException("无权删除此新闻");
        }

        News news = newsMapper.selectById(newsId);
        if (news == null || news.getIsDeleted() == 1) {
            throw new NewsNotFoundException(newsId);
        }

        int result = newsMapper.softDelete(newsId, currentUserId);
        if (result > 0) {
            recordOperation(currentUserId, OperationType.DELETE, newsId, "删除新闻: " + news.getTitle(), news, null);
            return true;
        }
        return false;
    }

    @Override
    public PageResult<NewsVO> getPendingNewsList(PageRequest pageRequest) {
        authorizationService.checkAdminPermission(authorizationService.getCurrentUserId());

        // Use getValidPageSize() for PageRequest
        Page<News> page = new Page<>(pageRequest.getPage(), pageRequest.getValidPageSize());
        Page<News> result = newsMapper.findPendingNews(page);

        List<NewsVO> voList = result.getRecords().stream()
                .map(news -> convertToVO(news, authorizationService.getCurrentUserId()))
                .collect(Collectors.toList());

        return new PageResult<>(voList, result.getTotal(), pageRequest.getPage(), pageRequest.getValidPageSize());
    }

    @Override
    @Transactional
    @CacheEvict(allEntries = true)
    public boolean auditNews(Long newsId, AuditRequest auditRequest) {
        Long currentUserId = authorizationService.getCurrentUserId();
        authorizationService.checkAdminPermission(currentUserId);

        News news = newsMapper.selectById(newsId);
        if (news == null || news.getIsDeleted() == 1) {
            throw new NewsNotFoundException(newsId);
        }

        if (news.getStatus() != NewsStatus.PENDING.getCode()) {
            throw new BusinessException("只能审核待审核状态的新闻");
        }

        int result = newsMapper.updateAuditStatus(newsId, auditRequest.getStatus(),
                auditRequest.getComment(), currentUserId);

        if (result > 0) {
            String statusText = NewsStatus.getByCode(auditRequest.getStatus()).getDesc();
            recordOperation(currentUserId, OperationType.AUDIT, newsId,
                    "审核新闻: " + statusText, news, null);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    @CacheEvict(allEntries = true)
    public boolean adminEditNews(Long newsId, NewsRequest newsRequest) {
        Long currentUserId = authorizationService.getCurrentUserId();
        authorizationService.checkAdminPermission(currentUserId);

        News existingNews = newsMapper.selectById(newsId);
        if (existingNews == null || existingNews.getIsDeleted() == 1) {
            throw new NewsNotFoundException(newsId);
        }

        News oldNews = new News();
        BeanUtils.copyProperties(existingNews, oldNews);

        BeanUtils.copyProperties(newsRequest, existingNews, "id", "userId", "createTime", "viewCount");
        existingNews.setUpdateTime(LocalDateTime.now());

        int result = newsMapper.updateById(existingNews);
        if (result > 0) {
            recordOperation(currentUserId, OperationType.EDIT, newsId,
                    "管理员编辑新闻: " + existingNews.getTitle(), oldNews, existingNews);
            return true;
        }
        return false;
    }

    @Override
    @Cacheable(key = "'allNews:' + #searchRequest.page + ':' + #searchRequest.pageSize + ':' + #searchRequest.status")
    public PageResult<NewsVO> getAllNewsList(SearchRequest searchRequest) {
        Long currentUserId = authorizationService.getCurrentUserId();
        authorizationService.checkAdminPermission(currentUserId);

        // Use getPageSize() for SearchRequest
        Page<News> page = new Page<>(searchRequest.getPage(), searchRequest.getPageSize());
        Page<News> result = newsMapper.findByCondition(page, searchRequest, searchRequest.getStatus(), null);

        List<NewsVO> voList = result.getRecords().stream()
                .map(news -> convertToVO(news, currentUserId))
                .collect(Collectors.toList());

        return new PageResult<>(voList, result.getTotal(), searchRequest.getPage(), searchRequest.getPageSize());
    }

    @Override
    @Transactional
    @CacheEvict(allEntries = true)
    public boolean adminDeleteNews(Long newsId) {
        Long currentUserId = authorizationService.getCurrentUserId();
        authorizationService.checkAdminPermission(currentUserId);

        News news = newsMapper.selectById(newsId);
        if (news == null || news.getIsDeleted() == 1) {
            throw new NewsNotFoundException(newsId);
        }

        news.setIsDeleted(1);
        news.setDeletedTime(LocalDateTime.now());
        int result = newsMapper.updateById(news);

        if (result > 0) {
            recordOperation(currentUserId, OperationType.DELETE, newsId,
                    "管理员删除新闻: " + news.getTitle(), news, null);
            return true;
        }
        return false;
    }

    @Override
    @Cacheable(key = "'popular:' + #limit", unless = "#result.isEmpty()")
    public List<NewsVO> getPopularNews(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        List<News> newsList = newsMapper.findPopularNews(limit, NewsStatus.APPROVED.getCode());
        return newsList.stream()
                .map(news -> convertToVO(news, authorizationService.getCurrentUserId()))
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(key = "'statistics'", unless = "#result.isEmpty()")
    public Map<String, Object> getBasicStatistics() {
        authorizationService.checkAdminPermission(authorizationService.getCurrentUserId());

        Map<String, Object> statistics = newsMapper.getAdminBasicStatistics(
                NewsStatus.PENDING.getCode(),
                NewsStatus.APPROVED.getCode(),
                NewsStatus.REJECTED.getCode(),
                NewsStatus.DRAFT.getCode()
        );

        // If you have a UserService to get total and active users, inject and call it here
        // For example:
        // if (userService != null) {
        //     statistics.put("totalUsers", userService.countTotalUsers());
        //     statistics.put("activeUsers", userService.countActiveUsersLast7Days());
        // }

        return statistics;
    }

    @Override
    @Cacheable(key = "'viewTrend:' + #startDate + ':' + #endDate")
    public List<Map<String, Object>> getViewTrend(LocalDate startDate, LocalDate endDate) {
        authorizationService.checkAdminPermission(authorizationService.getCurrentUserId());
        return newsMapper.getViewTrend(startDate, endDate);
    }

    @Override
    @Cacheable(key = "'hotNews:' + #limit + ':' + #status", unless = "#result.isEmpty()")
    public List<NewsVO> getHotNews(Integer limit, Integer status) {
        authorizationService.checkAdminPermission(authorizationService.getCurrentUserId());
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        List<News> hotNews = newsMapper.getHotNews(limit, status);
        return hotNews.stream()
                .map(news -> convertToVO(news, authorizationService.getCurrentUserId()))
                .collect(Collectors.toList());
    }

    // Private helper methods
    private NewsVO convertToVO(News news, Long currentUserId) {
        NewsVO vo = new NewsVO();
        BeanUtils.copyProperties(news, vo);
        NewsStatus newsStatus = NewsStatus.getByCode(news.getStatus());
        vo.setStatusText(newsStatus != null ? newsStatus.getDesc() : "未知状态");

        if (currentUserId != null) {
            boolean isAdmin = authorizationService.isAdmin(currentUserId);
            boolean isOwner = news.isOwner(currentUserId);

            vo.setIsOwner(isOwner);
            vo.setCanEdit((isOwner && news.canEdit()) || isAdmin);
            vo.setCanDelete(isOwner || isAdmin);
        } else {
            vo.setIsOwner(false);
            vo.setCanEdit(false);
            vo.setCanDelete(false);
        }
        return vo;
    }

    private void recordViewCount(Long newsId) {
        try {
            UserViewLog viewLog = new UserViewLog();
            viewLog.setUserId(authorizationService.getCurrentUserId());
            viewLog.setNewsId(newsId);
            viewLog.setViewTime(LocalDateTime.now());
            logService.recordView(viewLog);
        } catch (Exception e) {
            log.warn("Failed to record view log: {}", e.getMessage());
        }
    }

    private void recordOperation(Long userId, OperationType operationType, Long resourceId,
                                 String description, Object oldValue, Object newValue) {
        try {
            UserOperationLog operationLog = new UserOperationLog();
            operationLog.setUserId(userId);
            operationLog.setOperationType(operationType.getCode());
            operationLog.setResourceId(resourceId);
            operationLog.setOperationDesc(description);
            operationLog.setOperationTime(LocalDateTime.now());
            logService.recordOperation(operationLog);
        } catch (Exception e) {
            log.warn("Failed to record operation log: {}", e.getMessage());
        }
    }

    /**
     * 清除新闻详情缓存的辅助方法
     */
    @CacheEvict(key = "'detail:' + #newsId")
    public void evictNewsDetailCache(Long newsId) {
        // 仅用于清除缓存，方法体为空
        log.debug("Evicted news detail cache for newsId: {}", newsId);
    }
}