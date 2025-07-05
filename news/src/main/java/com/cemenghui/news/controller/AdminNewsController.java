package com.cemenghui.news.controller;

import com.cemenghui.common.Result;
import com.cemenghui.news.dto.*;
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
@RequestMapping("/api/admin/news")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Slf4j
public class AdminNewsController {

    private final NewsService newsService;

    @PostMapping("/publish")
    public Result adminPublishNews(@Valid @RequestBody NewsRequest newsRequest) {
        Long newsId = newsService.adminPublishNews(newsRequest);
        return Result.success(newsId);
    }

    @GetMapping("/pending")
    public Result getPendingNewsList(PageRequest pageRequest) {
        PageResult<NewsVO> result = newsService.getPendingNewsList(pageRequest);
        return Result.success(result);
    }

    @PostMapping("/{newsId}/audit")
    public Result auditNews(@PathVariable Long newsId, @Valid @RequestBody AuditRequest auditRequest) {
        boolean success = newsService.auditNews(newsId, auditRequest);
        return success ? Result.success("审核成功") : Result.fail("审核失败");
    }

    @PutMapping("/{newsId}")
    public Result adminEditNews(@PathVariable Long newsId, @Valid @RequestBody NewsRequest newsRequest) {
        boolean success = newsService.adminEditNews(newsId, newsRequest);
        return success ? Result.success("编辑成功") : Result.fail("编辑失败");
    }

    @GetMapping("/all")
    public Result getAllNewsList(SearchRequest searchRequest) {
        PageResult<NewsVO> result = newsService.getAllNewsList(searchRequest);
        return Result.success(result);
    }

    @DeleteMapping("/{newsId}")
    public Result adminDeleteNews(@PathVariable Long newsId) {
        boolean success = newsService.adminDeleteNews(newsId);
        return success ? Result.success("删除成功") : Result.fail("删除失败");
    }

    @GetMapping("/statistics/basic")
    public Result getBasicStatistics() {
        Map<String, Object> statistics = newsService.getBasicStatistics();
        return Result.success(statistics);
    }

    @GetMapping("/statistics/view-trend")
    public Result getViewTrend(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Map<String, Object>> trendData = newsService.getViewTrend(startDate, endDate);
        return Result.success(trendData);
    }

    @GetMapping("/statistics/hot-news")
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
    public Result batchAuditNews(@Valid @RequestBody BatchAuditRequest batchRequest) {
        int successCount = 0;
        for (Long newsId : batchRequest.getNewsIds()) {
            try {
                if (newsService.auditNews(newsId, batchRequest.getAuditRequest())) {
                    successCount++;
                }
            } catch (Exception e) {
                log.warn("批量审核新闻ID {} 失败: {}", newsId, e.getMessage());
            }
        }
        return Result.success("批量审核完成，成功处理" + successCount + "条记录");
    }
}