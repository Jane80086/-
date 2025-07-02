package com.cemenghui.news.controller;

import com.cemenghui.common.Result;
import com.cemenghui.news.dto.AuditRequest;
import com.cemenghui.news.dto.NewsRequest;
import com.cemenghui.news.dto.PageRequest;
import com.cemenghui.news.dto.SearchRequest;
import com.cemenghui.news.dto.NewsVO;
import com.cemenghui.news.dto.PageResult;
import com.cemenghui.news.service.NewsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/news")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminNewsController {

    private final NewsService newsService;

    /**
     * 获取待审核新闻列表
     */
    @GetMapping("/pending")
    public Result getPendingNewsList(PageRequest pageRequest) {
        PageResult<NewsVO> result = newsService.getPendingNewsList(pageRequest);
        return Result.success("查询成功", result);
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
     * 获取所有新闻列表
     */
    @GetMapping("/all")
    public Result getAllNewsList(SearchRequest searchRequest) {
        PageResult<NewsVO> result = newsService.getAllNewsList(searchRequest);
        return Result.success("查询成功", result);
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
     */
    @GetMapping("/statistics")
    public Result getBasicStatistics() {
        Map<String, Object> statistics = newsService.getBasicStatistics();
        return Result.success("获取成功", statistics);
    }

    /**
     * 批量审核新闻
     */
    @PostMapping("/batch-audit")
    public Result batchAuditNews(@RequestBody List<Long> newsIds, @Valid @RequestBody AuditRequest auditRequest) {
        // 这里需要实现批量审核逻辑
        // 可以循环调用单个审核方法，或者在service层实现批量处理
        int successCount = 0;
        for (Long newsId : newsIds) {
            try {
                if (newsService.auditNews(newsId, auditRequest)) {
                    successCount++;
                }
            } catch (Exception e) {
                // 记录失败的情况，继续处理其他的
            }
        }
        return Result.success("批量审核完成，成功处理" + successCount + "条记录");
    }
}
