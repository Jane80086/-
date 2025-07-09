package com.cemenghui.news.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cemenghui.news.constants.NewsConstants;
import com.cemenghui.news.dto.*;
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
import com.google.gson.Gson;
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
    private final Gson gson = new Gson();

    @Override
    public PageResult<NewsVO> searchNews(SearchRequest searchRequest) {
        Integer statusToSearch = NewsConstants.NEWS_STATUS_PUBLISHED;
        Page<News> page = new Page<>(searchRequest.getPage(), searchRequest.getPageSize());
        Page<News> result = newsMapper.findByCondition(page, searchRequest, statusToSearch, null);
        List<NewsVO> voList = result.getRecords().stream()
                .map(news -> convertToVO(news, authorizationService.getCurrentUserId()))
                .collect(Collectors.toList());
        return new PageResult<>(voList, result.getTotal(), searchRequest.getPage(), searchRequest.getPageSize());
    }

    @Override
    @Cacheable(key = "'detail:' + #newsId", unless = "#result == null")
    public NewsVO getNewsDetail(Long newsId) {
        News news = newsMapper.selectById(newsId);
        if (news == null || news.getIsDeleted() == 1) {
            throw new NewsNotFoundException(newsId);
        }
        Long currentUserId = authorizationService.getCurrentUserId();
        // 这里的 isAdmin 检查是业务逻辑的一部分，因为普通用户也可以查看已发布新闻，
        // 而管理员和所有者可以查看未发布的新闻。所以这个检查不是多余的。
        boolean isAdmin = authorizationService.isAdmin(currentUserId);
        boolean isOwner = news.isOwner(currentUserId);

        if (news.isPublished() || isAdmin || isOwner) {
            recordViewCount(news);
            newsMapper.updateViewCount(newsId);
            evictNewsDetailCache(newsId);
            return convertToVO(news, currentUserId);
        } else {
            throw new UnauthorizedException("无权查看此新闻详情");
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "news", allEntries = true)
    public Long publishNews(NewsRequest newsRequest) {
        Long currentUserId = authorizationService.getCurrentUserId();
        // 此方法对应 @PreAuthorize("hasAnyRole('ADMIN', 'ENTERPRISE')")
        // 因此 checkEnterprisePermission 依然是必要的，除非控制器严格区分
        authorizationService.checkEnterprisePermission(currentUserId);
        News news = new News();
        BeanUtils.copyProperties(newsRequest, news);
        news.setUserId(currentUserId);
        news.setStatus(NewsConstants.NEWS_STATUS_PENDING); // 默认待审核
        newsMapper.insert(news);
        recordOperation(currentUserId, OperationType.PUBLISH, news.getId(), "发布新闻", null, news);
        return news.getId();
    }

    @Override
    @Transactional
    @CacheEvict(value = "news", allEntries = true)
    public Long adminPublishNews(NewsRequest newsRequest) {
        Long currentUserId = authorizationService.getCurrentUserId();
        // ==== 核心修改点：此方法在 AdminNewsController 中已有 @PreAuthorize("hasRole('ADMIN')")，所以可以移除此行 ====
        // authorizationService.checkAdminPermission(currentUserId); // 删除或注释掉此行

        News news = new News();
        BeanUtils.copyProperties(newsRequest, news);
        news.setUserId(currentUserId);
        news.setStatus(NewsConstants.NEWS_STATUS_PUBLISHED); // 管理员直接发布
        newsMapper.insert(news);
        recordOperation(currentUserId, OperationType.PUBLISH, news.getId(), "管理员发布新闻", null, news);
        return news.getId();
    }

    @Override
    public PageResult<NewsVO> getMyNewsList(PageRequest pageRequest) {
        Long currentUserId = authorizationService.getCurrentUserId();
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
            @CacheEvict(cacheNames = "news", key = "'detail:' + #newsId"),
            @CacheEvict(cacheNames = "news", allEntries = true)
    })
    public boolean editNews(Long newsId, NewsRequest newsRequest) {
        Long currentUserId = authorizationService.getCurrentUserId();
        News existingNews = newsMapper.selectById(newsId);
        if (existingNews == null || existingNews.getIsDeleted() == 1) {
            throw new NewsNotFoundException(newsId);
        }
        // canEditNews 是业务逻辑的一部分，因为需要判断是否是所有者，或者是否是管理员
        if (!authorizationService.canEditNews(currentUserId, newsId)) {
            throw new UnauthorizedException("无权编辑此新闻");
        }
        News oldNews = new News();
        BeanUtils.copyProperties(existingNews, oldNews);

        BeanUtils.copyProperties(newsRequest, existingNews);
        existingNews.setUpdateTime(LocalDateTime.now());

        // 这部分逻辑是判断企业用户编辑后是否变为待审核状态，属于业务规则，不是纯粹的权限检查
        if (authorizationService.isEnterprise(currentUserId) && !authorizationService.isAdmin(currentUserId)) {
            if (!existingNews.canEdit()) {
                throw new BusinessException("当前状态的新闻不允许编辑");
            }
            existingNews.setStatus(NewsConstants.NEWS_STATUS_PENDING);
        }

        int result = newsMapper.updateById(existingNews);
        if(result > 0) recordOperation(currentUserId, OperationType.EDIT, newsId, "编辑新闻", oldNews, existingNews);
        return result > 0;
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = "news", key = "'detail:' + #newsId"),
            @CacheEvict(cacheNames = "news", allEntries = true)
    })
    public boolean deleteNews(Long newsId) {
        Long currentUserId = authorizationService.getCurrentUserId();
        // canDeleteNews 是业务逻辑的一部分，因为需要判断是否是所有者，或者是否是管理员
        if (!authorizationService.canDeleteNews(currentUserId, newsId)) {
            throw new UnauthorizedException("无权删除此新闻");
        }
        News news = newsMapper.selectById(newsId);
        if (news == null) throw new NewsNotFoundException(newsId);

        int result = newsMapper.softDelete(newsId, currentUserId);
        if(result > 0) recordOperation(currentUserId, OperationType.DELETE, newsId, "删除新闻", news, null);
        return result > 0;
    }

    @Override
    public PageResult<NewsVO> getPendingNewsList(PageRequest pageRequest) {
        // ==== 核心修改点：此方法在 AdminNewsController 中已有 @PreAuthorize("hasRole('ADMIN')")，所以可以移除此行 ====
        // authorizationService.checkAdminPermission(authorizationService.getCurrentUserId()); // 删除或注释掉此行

        Page<News> page = new Page<>(pageRequest.getPage(), pageRequest.getValidPageSize());
        Page<News> result = newsMapper.findPendingNews(page);
        List<NewsVO> voList = result.getRecords().stream()
                .map(news -> convertToVO(news, authorizationService.getCurrentUserId()))
                .collect(Collectors.toList());
        return new PageResult<>(voList, result.getTotal(), pageRequest.getPage(), pageRequest.getValidPageSize());
    }

    @Override
    @Transactional
    @CacheEvict(value = "news", allEntries = true)
    public boolean auditNews(Long newsId, AuditRequest auditRequest) {
        Long currentUserId = authorizationService.getCurrentUserId();
        // ==== 核心修改点：此方法在 AdminNewsController 中已有 @PreAuthorize("hasRole('ADMIN')")，所以可以移除此行 ====
        // authorizationService.checkAdminPermission(currentUserId); // 删除或注释掉此行

        News news = newsMapper.selectById(newsId);
        if (news == null || news.getIsDeleted() == 1) {
            throw new NewsNotFoundException(newsId);
        }
        if (!news.isPending()) { // 这是业务逻辑：只有待审核新闻才能被审核
            throw new BusinessException("只能审核待审核状态的新闻");
        }
        News oldNews = new News();
        BeanUtils.copyProperties(news, oldNews);

        news.setStatus(auditRequest.getStatus());
        news.setAuditUserId(currentUserId);
        news.setAuditComment(auditRequest.getComment());
        news.setAuditTime(LocalDateTime.now());

        int result = newsMapper.updateById(news);
        if(result > 0) {
            String statusText = NewsStatus.getByCode(auditRequest.getStatus()).getDesc();
            recordOperation(currentUserId, OperationType.AUDIT, newsId, "审核新闻: " + statusText, oldNews, news);
        }
        return result > 0;
    }

    @Override
    @Transactional
    @CacheEvict(value = "news", allEntries = true)
    public boolean adminEditNews(Long newsId, NewsRequest newsRequest) {
        Long currentUserId = authorizationService.getCurrentUserId();
        // ==== 核心修改点：此方法在 AdminNewsController 中已有 @PreAuthorize("hasRole('ADMIN')")，所以可以移除此行 ====
        // authorizationService.checkAdminPermission(currentUserId); // 删除或注释掉此行

        News existingNews = newsMapper.selectById(newsId);
        if (existingNews == null || existingNews.getIsDeleted() == 1) {
            throw new NewsNotFoundException(newsId);
        }
        News oldNews = new News();
        BeanUtils.copyProperties(existingNews, oldNews);
        BeanUtils.copyProperties(newsRequest, existingNews);
        existingNews.setUpdateTime(LocalDateTime.now());
        int result = newsMapper.updateById(existingNews);
        if(result > 0) recordOperation(currentUserId, OperationType.EDIT, newsId, "管理员编辑新闻", oldNews, existingNews);
        return result > 0;
    }

    @Override
    @Cacheable(key = "'allNews:' + #searchRequest.page + ':' + #searchRequest.pageSize + ':' + #searchRequest.status")
    public PageResult<NewsVO> getAllNewsList(SearchRequest searchRequest) {
        Long currentUserId = authorizationService.getCurrentUserId();
        // ==== 核心修改点：此方法在 AdminNewsController 中已有 @PreAuthorize("hasRole('ADMIN')")，所以可以移除此行 ====
        // authorizationService.checkAdminPermission(currentUserId); // 删除或注释掉此行

        Page<News> page = new Page<>(searchRequest.getPage(), searchRequest.getPageSize());
        Page<News> result = newsMapper.findByCondition(page, searchRequest, searchRequest.getStatus(), null);
        List<NewsVO> voList = result.getRecords().stream()
                .map(news -> convertToVO(news, currentUserId))
                .collect(Collectors.toList());
        return new PageResult<>(voList, result.getTotal(), searchRequest.getPage(), searchRequest.getPageSize());
    }

    @Override
    @Transactional
    @CacheEvict(value = "news", allEntries = true)
    public boolean adminDeleteNews(Long newsId) {
        Long currentUserId = authorizationService.getCurrentUserId();
        // ==== 核心修改点：此方法在 AdminNewsController 中已有 @PreAuthorize("hasRole('ADMIN')")，所以可以移除此行 ====
        // authorizationService.checkAdminPermission(currentUserId); // 删除或注释掉此行

        News news = newsMapper.selectById(newsId);
        if (news == null) throw new NewsNotFoundException(newsId);

        News oldNews = new News();
        BeanUtils.copyProperties(news, oldNews);
        news.setIsDeleted(1);
        int result = newsMapper.updateById(news);
        if(result > 0) recordOperation(currentUserId, OperationType.DELETE, newsId, "管理员删除新闻", oldNews, null);
        return result > 0;
    }

    @Override
    @Cacheable(key = "'popular:' + #limit", unless = "#result.isEmpty()")
    public List<NewsVO> getPopularNews(Integer limit) {
        Integer finalLimit = (limit == null || limit <= 0) ? 10 : limit;
        List<News> newsList = newsMapper.findPopularNews(finalLimit, NewsConstants.NEWS_STATUS_PUBLISHED);
        return newsList.stream()
                .map(news -> convertToVO(news, authorizationService.getCurrentUserId()))
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(cacheNames = "news", key = "'statistics'", unless = "#result.isEmpty()")
    public Map<String, Object> getBasicStatistics() {
        Long currentUserId = authorizationService.getCurrentUserId();
        // ==== 核心修改点：此方法在 AdminNewsController 中已有 @PreAuthorize("hasRole('ADMIN')")，所以可以移除此行 ====
        // authorizationService.checkAdminPermission(currentUserId); // 删除或注释掉此行

        return newsMapper.getAdminBasicStatistics(
                NewsConstants.NEWS_STATUS_PENDING,
                NewsConstants.NEWS_STATUS_PUBLISHED,
                NewsConstants.NEWS_STATUS_REJECTED
        );
    }

    @Override
    @Cacheable(key = "'viewTrend:' + #startDate + ':' + #endDate")
    public List<Map<String, Object>> getViewTrend(LocalDate startDate, LocalDate endDate) {
        Long currentUserId = authorizationService.getCurrentUserId();
        // ==== 核心修改点：此方法在 AdminNewsController 中已有 @PreAuthorize("hasRole('ADMIN')")，所以可以移除此行 ====
        // authorizationService.checkAdminPermission(currentUserId); // 删除或注释掉此行

        return newsMapper.getViewTrend(startDate, endDate);
    }

    @Override
    @Cacheable(key = "'hotNews:' + #limit + ':' + #status", unless = "#result.isEmpty()")
    public List<NewsVO> getHotNews(Integer limit, Integer status) {
        Long currentUserId = authorizationService.getCurrentUserId();
        // ==== 核心修改点：此方法在 AdminNewsController 中已有 @PreAuthorize("hasRole('ADMIN')")，所以可以移除此行 ====
        // authorizationService.checkAdminPermission(currentUserId); // 删除或注释掉此行

        Integer finalLimit = (limit == null || limit <= 0) ? 10 : limit;
        List<News> hotNews = newsMapper.getHotNews(finalLimit, status);
        return hotNews.stream()
                .map(news -> convertToVO(news, currentUserId))
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
            // 确保 canEdit 和 canDelete 的逻辑正确，这里是结合了业务逻辑和权限的判断
            vo.setCanEdit((isOwner && news.canEdit()) || isAdmin);
            vo.setCanDelete(isOwner || isAdmin);
        } else {
            vo.setIsOwner(false);
            vo.setCanEdit(false);
            vo.setCanDelete(false);
        }
        return vo;
    }

    private void recordViewCount(News news) {
        try {
            UserViewLog viewLog = new UserViewLog();
            viewLog.setUserId(authorizationService.getCurrentUserId());
            viewLog.setResourceType("NEWS");
            viewLog.setNewsId(news.getId());
            viewLog.setResourceTitle(news.getTitle());
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
            operationLog.setResourceType("NEWS");
            operationLog.setResourceId(resourceId);
            if (newValue instanceof News) {
                operationLog.setResourceTitle(((News) newValue).getTitle());
            } else if (oldValue instanceof News) {
                operationLog.setResourceTitle(((News) oldValue).getTitle());
            }
            operationLog.setOperationDesc(description);
            operationLog.setOperationTime(LocalDateTime.now());
            operationLog.setOperationResult(1); // 1 for success
            operationLog.setOldValue(oldValue != null ? gson.toJson(oldValue) : null);
            operationLog.setNewValue(newValue != null ? gson.toJson(newValue) : null);
            logService.recordOperation(operationLog);
        } catch (Exception e) {
            log.warn("Failed to record operation log: {}", e.getMessage());
        }
    }

    @CacheEvict(cacheNames = "news", key = "'detail:' + #newsId")
    public void evictNewsDetailCache(Long newsId) {
        log.debug("Evicted news detail cache for newsId: {}", newsId);
    }
}