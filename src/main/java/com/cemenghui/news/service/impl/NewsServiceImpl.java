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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsServiceImpl implements NewsService {

    private final NewsMapper newsMapper;
    private final AuthorizationService authorizationService;
    private final LogService logService;

    @Override
    public PageResult<NewsVO> searchNews(SearchRequest searchRequest) {
        Page<News> page = new Page<>(searchRequest.getPage(), searchRequest.getPageSize());
        Page<News> result = newsMapper.findByCondition(page, searchRequest);

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

        // 记录浏览日志
        recordViewCount(newsId);

        // 增加浏览次数
        newsMapper.updateViewCount(newsId);
        news.incrementViewCount();

        return convertToVO(news, authorizationService.getCurrentUserId());
    }

    @Override
    @Transactional
    public Long publishNews(NewsRequest newsRequest) {
        Long currentUserId = authorizationService.getCurrentUserId();
        authorizationService.checkEnterprisePermission(currentUserId);

        News news = new News();
        BeanUtils.copyProperties(newsRequest, news);
        news.setUserId(currentUserId);
        news.setStatus(NewsStatus.PENDING.getCode());
        news.setCreateTime(LocalDateTime.now());
        news.setUpdateTime(LocalDateTime.now());

        int result = newsMapper.insert(news);
        if (result > 0) {
            // 记录操作日志
            recordOperation(currentUserId, OperationType.PUBLISH, news.getId(), "发布新闻: " + news.getTitle(), null, news);
            return news.getId();
        }
        throw new BusinessException("发布新闻失败");
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
    public boolean editNews(Long newsId, NewsRequest newsRequest) {
        Long currentUserId = authorizationService.getCurrentUserId();

        if (!authorizationService.canEditNews(currentUserId, newsId)) {
            throw new UnauthorizedException("编辑新闻");
        }

        News existingNews = newsMapper.selectById(newsId);
        if (existingNews == null || existingNews.getIsDeleted() == 1) {
            throw new NewsNotFoundException(newsId);
        }

        if (!existingNews.canEdit()) {
            throw new BusinessException("当前状态的新闻不允许编辑");
        }

        News oldNews = new News();
        BeanUtils.copyProperties(existingNews, oldNews);

        BeanUtils.copyProperties(newsRequest, existingNews);
        existingNews.setUpdateTime(LocalDateTime.now());
        existingNews.setStatus(NewsStatus.PENDING.getCode()); // 编辑后重新审核

        int result = newsMapper.updateById(existingNews);
        if (result > 0) {
            recordOperation(currentUserId, OperationType.EDIT, newsId, "编辑新闻: " + existingNews.getTitle(), oldNews, existingNews);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean deleteNews(Long newsId) {
        Long currentUserId = authorizationService.getCurrentUserId();

        if (!authorizationService.canDeleteNews(currentUserId, newsId)) {
            throw new UnauthorizedException("删除新闻");
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

        Page<News> page = new Page<>(pageRequest.getPage(), pageRequest.getValidPageSize());
        Page<News> result = newsMapper.findPendingNews(page);

        List<NewsVO> voList = result.getRecords().stream()
                .map(news -> convertToVO(news, authorizationService.getCurrentUserId()))
                .collect(Collectors.toList());

        return new PageResult<>(voList, result.getTotal(), pageRequest.getPage(), pageRequest.getValidPageSize());
    }

    @Override
    @Transactional
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
            String statusText = auditRequest.getStatus() == 1 ? "通过" : "拒绝";
            recordOperation(currentUserId, OperationType.AUDIT, newsId,
                    "审核新闻: " + statusText, news, null);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean adminEditNews(Long newsId, NewsRequest newsRequest) {
        Long currentUserId = authorizationService.getCurrentUserId();
        authorizationService.checkAdminPermission(currentUserId);

        News existingNews = newsMapper.selectById(newsId);
        if (existingNews == null || existingNews.getIsDeleted() == 1) {
            throw new NewsNotFoundException(newsId);
        }

        News oldNews = new News();
        BeanUtils.copyProperties(existingNews, oldNews);

        BeanUtils.copyProperties(newsRequest, existingNews);
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
    public PageResult<NewsVO> getAllNewsList(SearchRequest searchRequest) {
        authorizationService.checkAdminPermission(authorizationService.getCurrentUserId());
        return searchNews(searchRequest);
    }

    @Override
    @Transactional
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
    public List<NewsVO> getPopularNews(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        List<News> newsList = newsMapper.findPopularNews(limit);
        return newsList.stream()
                .map(news -> convertToVO(news, authorizationService.getCurrentUserId()))
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getBasicStatistics() {
        authorizationService.checkAdminPermission(authorizationService.getCurrentUserId());

        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalNews", newsMapper.countByStatus(null));
        statistics.put("pendingNews", newsMapper.countByStatus(NewsStatus.PENDING.getCode()));
        statistics.put("approvedNews", newsMapper.countByStatus(NewsStatus.APPROVED.getCode()));
        statistics.put("rejectedNews", newsMapper.countByStatus(NewsStatus.REJECTED.getCode()));

        // 可以添加更多统计信息
        List<Map<String, Object>> detailStats = newsMapper.getBasicStatistics();
        statistics.put("detailStats", detailStats);

        return statistics;
    }

    // 私有方法
    private NewsVO convertToVO(News news, Long currentUserId) {
        NewsVO vo = new NewsVO();
        BeanUtils.copyProperties(news, vo);
        vo.setStatusText(NewsStatus.getByCode(news.getStatus()).getDesc());

        if (currentUserId != null) {
            vo.setIsOwner(news.isOwner(currentUserId));
            vo.setCanEdit(news.canEdit() && (news.isOwner(currentUserId) || isAdmin(currentUserId)));
            vo.setCanDelete(news.canDelete() && (news.isOwner(currentUserId) || isAdmin(currentUserId)));
        } else {
            vo.setIsOwner(false);
            vo.setCanEdit(false);
            vo.setCanDelete(false);
        }

        return vo;
    }

    private boolean isAdmin(Long userId) {
        // 这里需要根据实际的用户角色判断逻辑来实现
        // 可以通过AuthorizationService来判断
        try {
            authorizationService.checkAdminPermission(userId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void recordViewCount(Long newsId) {
        try {
            UserViewLog viewLog = new UserViewLog();
            viewLog.setUserId(authorizationService.getCurrentUserId());
            viewLog.setNewsId(newsId);
            viewLog.setViewTime(LocalDateTime.now());
            // 可以添加IP地址、用户代理等信息
            logService.recordView(viewLog);
        } catch (Exception e) {
            log.warn("记录浏览日志失败: {}", e.getMessage());
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
            // 可以将oldValue和newValue序列化为JSON存储
            operationLog.setOperationTime(LocalDateTime.now());
            logService.recordOperation(operationLog);
        } catch (Exception e) {
            log.warn("记录操作日志失败: {}", e.getMessage());
        }
    }
}