package com.cemenghui.news.controller;

import com.cemenghui.common.Result;
import com.cemenghui.news.dto.AuditRequest;
import com.cemenghui.news.dto.NewsRequest;
import com.cemenghui.news.dto.PageRequest;
import com.cemenghui.news.dto.SearchRequest;
import com.cemenghui.news.dto.NewsVO;
import com.cemenghui.news.dto.PageResult;
import com.cemenghui.news.service.NewsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/news") // 基础路径保持不变
@PreAuthorize("hasRole('ADMIN')") // 确保只有管理员才能访问这些接口
@RequiredArgsConstructor
@Slf4j
public class AdminNewsController {

    private final NewsService newsService;

    /**
     * 管理员发布新闻（无需审核）
     */
    @PostMapping("/publish") // 新增管理员发布接口
    public Result adminPublishNews(@Valid @RequestBody NewsRequest newsRequest) {
        Long newsId = newsService.adminPublishNews(newsRequest); // 调用新的服务层方法
        return Result.success(newsId);
    }

    /**
     * 获取待审核新闻列表
     */
    @GetMapping("/pending")
    public Result getPendingNewsList(PageRequest pageRequest) {
        PageResult<NewsVO> result = newsService.getPendingNewsList(pageRequest);
        return Result.success(result);
    }

    /**
     * 审核新闻
     */
    @PostMapping("/{newsId}/audit")
    public Result auditNews(@PathVariable Long newsId, @Valid @RequestBody AuditRequest auditRequest) {
        boolean success = newsService.auditNews(newsId, auditRequest);
        return success ? Result.success("审核成功") : Result.fail("审核失败");
    }

    /**
     * 管理员编辑新闻
     */
    @PutMapping("/{newsId}")
    public Result adminEditNews(@PathVariable Long newsId, @Valid @RequestBody NewsRequest newsRequest) {
        boolean success = newsService.adminEditNews(newsId, newsRequest);
        return success ? Result.success("编辑成功") : Result.fail("编辑失败");
    }

    /**
     * 获取所有新闻列表 (管理员可查看所有状态)
     */
    @GetMapping("/all")
    public Result getAllNewsList(SearchRequest searchRequest) {
        PageResult<NewsVO> result = newsService.getAllNewsList(searchRequest);
        return Result.success(result);
    }

    /**
     * 管理员删除新闻
     */
    @DeleteMapping("/{newsId}")
    public Result adminDeleteNews(@PathVariable Long newsId) {
        boolean success = newsService.adminDeleteNews(newsId);
        return success ? Result.success("删除成功") : Result.fail("删除失败");
    }

    /**
     * 获取基础统计数据
     * 对应前端 /api/admin/news/statistics/basic
     */
    @GetMapping("/statistics/basic") // <-- 修改这里，使其与前端期望的路径匹配
    public Result getBasicStatistics() {
        Map<String, Object> statistics = newsService.getBasicStatistics();
        return Result.success(statistics);
    }

    /**
     * 获取浏览量趋势数据
     * 对应前端 /api/admin/news/statistics/view-trend
     */
    @GetMapping("/statistics/view-trend") // <-- 新增此方法和映射
    public Result getViewTrend(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Map<String, Object>> trendData = newsService.getViewTrend(startDate, endDate);
        return Result.success(trendData);
    }

    /**
     * 获取热门动态列表
     * 对应前端 /api/admin/news/statistics/hot-news
     */
    @GetMapping("/statistics/hot-news") // <-- 新增此方法和映射
    public Result getHotNews(
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) Integer status) {
        List<NewsVO> hotNews = newsService.getHotNews(limit, status);
        return Result.success(hotNews);
    }

    /**
     * 批量审核新闻
     */
    @PostMapping("/batch-audit")
    public Result batchAuditNews(@RequestBody List<Long> newsIds, @Valid @RequestBody AuditRequest auditRequest) {
        int successCount = 0;
        for (Long newsId : newsIds) {
            try {
                if (newsService.auditNews(newsId, auditRequest)) {
                    successCount++;
                }
            } catch (Exception e) {
                log.warn("批量审核新闻ID {} 失败: {}", newsId, e.getMessage());
            }
        }
        return Result.success("批量审核完成，成功处理" + successCount + "条记录");
    }
}
