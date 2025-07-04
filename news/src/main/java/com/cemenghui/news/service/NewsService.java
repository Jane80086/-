package com.cemenghui.news.service;

import com.cemenghui.news.dto.*;
import com.cemenghui.news.dto.NewsVO;
import com.cemenghui.news.dto.PageResult;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface NewsService {
    PageResult<NewsVO> searchNews(SearchRequest searchRequest);
    NewsVO getNewsDetail(Long newsId);
    Long publishNews(NewsRequest newsRequest);
    Long adminPublishNews(NewsRequest newsRequest); // 新增管理员发布新闻方法
    PageResult<NewsVO> getMyNewsList(PageRequest pageRequest);
    boolean editNews(Long newsId, NewsRequest newsRequest);
    boolean deleteNews(Long newsId);
    PageResult<NewsVO> getPendingNewsList(PageRequest pageRequest);
    boolean auditNews(Long newsId, AuditRequest auditRequest);
    boolean adminEditNews(Long newsId, NewsRequest newsRequest);
    PageResult<NewsVO> getAllNewsList(SearchRequest searchRequest);
    boolean adminDeleteNews(Long newsId);
    List<NewsVO> getPopularNews(Integer limit);
    /**
     * 获取管理员仪表盘基础统计数据
     * 包括总动态数、各种状态动态数、总浏览量、今日浏览量、总用户数、活跃用户数等
     * @return 统计数据Map
     */
    Map<String, Object> getBasicStatistics();

    /**
     * 获取指定日期范围内的浏览量趋势数据
     * @param startDate 开始日期 (YYYY-MM-DD)
     * @param endDate 结束日期 (YYYY-MM-DD)
     * @return 每日浏览量列表，例如 [{date: "2023-01-01", views: 100}, ...]
     */
    List<Map<String, Object>> getViewTrend(LocalDate startDate, LocalDate endDate); // 使用 LocalDate

    /**
     * 获取热门动态列表（管理员视角，可选择性包含所有状态或指定状态）
     * @param limit 返回数量限制
     * @param status 动态状态，null表示所有状态，否则根据状态码过滤
     * @return 热门动态列表
     */
    List<NewsVO> getHotNews(Integer limit, Integer status); // 可以增加状态参数，更灵活
}
