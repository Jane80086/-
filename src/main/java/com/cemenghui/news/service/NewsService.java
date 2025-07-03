package com.cemenghui.news.service;

import com.cemenghui.news.dto.*;
import com.cemenghui.news.dto.NewsVO;
import com.cemenghui.news.dto.PageResult;
import java.util.List;
import java.util.Map;

public interface NewsService {

    /**
     * 搜索新闻
     */
    PageResult<NewsVO> searchNews(SearchRequest searchRequest);

    /**
     * 获取新闻详情
     */
    NewsVO getNewsDetail(Long newsId);

    /**
     * 发布新闻
     */
    Long publishNews(NewsRequest newsRequest);

    /**
     * 获取我的新闻列表
     */
    PageResult<NewsVO> getMyNewsList(PageRequest pageRequest);

    /**
     * 编辑新闻
     */
    boolean editNews(Long newsId, NewsRequest newsRequest);

    /**
     * 删除新闻
     */
    boolean deleteNews(Long newsId);

    /**
     * 获取待审核新闻列表
     */
    PageResult<NewsVO> getPendingNewsList(PageRequest pageRequest);

    /**
     * 审核新闻
     */
    boolean auditNews(Long newsId, AuditRequest auditRequest);

    /**
     * 管理员编辑新闻
     */
    boolean adminEditNews(Long newsId, NewsRequest newsRequest);

    /**
     * 获取所有新闻列表
     */
    PageResult<NewsVO> getAllNewsList(SearchRequest searchRequest);

    /**
     * 管理员删除新闻
     */
    boolean adminDeleteNews(Long newsId);

    /**
     * 获取热门新闻
     */
    List<NewsVO> getPopularNews(Integer limit);

    /**
     * 获取基础统计数据
     */
    Map<String, Object> getBasicStatistics();
}
