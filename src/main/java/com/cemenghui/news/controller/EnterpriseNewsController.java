package com.cemenghui.news.controller;

import com.cemenghui.common.Result;
import com.cemenghui.news.dto.NewsRequest;
import com.cemenghui.news.dto.PageRequest;
import com.cemenghui.news.dto.NewsVO;
import com.cemenghui.news.dto.PageResult;
import com.cemenghui.news.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/enterprise/news")
@PreAuthorize("hasRole('ENTERPRISE') or hasRole('ADMIN')")
@RequiredArgsConstructor
public class EnterpriseNewsController {

    private final NewsService newsService;

    /**
     * 发布新闻
     */
    @PostMapping
    public Result publishNews(@Valid @RequestBody NewsRequest newsRequest, Authentication authentication) {
        Long newsId = newsService.publishNews(newsRequest);
        return Result.success("发布成功", newsId);
    }

    /**
     * 获取我的新闻列表
     */
    @GetMapping("/my")
    public Result getMyNewsList(PageRequest pageRequest, Authentication authentication) {
        PageResult<NewsVO> result = newsService.getMyNewsList(pageRequest);
        return Result.success("查询成功", result);
    }

    /**
     * 编辑新闻
     */
    @PutMapping("/{newsId}")
    public Result editNews(@PathVariable Long newsId,
                           @Valid @RequestBody NewsRequest newsRequest,
                           Authentication authentication) {
        boolean success = newsService.editNews(newsId, newsRequest);
        return success ? Result.success("编辑成功") : Result.fail("编辑失败");
    }

    /**
     * 删除新闻
     */
    @DeleteMapping("/{newsId}")
    public Result deleteNews(@PathVariable Long newsId, Authentication authentication) {
        boolean success = newsService.deleteNews(newsId);
        return success ? Result.success("删除成功") : Result.fail("删除失败");
    }
}
