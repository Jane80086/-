package com.cemenghui.news.controller;

import com.cemenghui.common.Result;
import com.cemenghui.news.dto.*;
import com.cemenghui.news.service.ContentRefinementService;
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
    private final ContentRefinementService contentRefinementService;// 注入 ContentRefinementService 接口

    /**
     * 发布新闻
     */
    @PostMapping
    public Result publishNews(@Valid @RequestBody NewsRequest newsRequest, Authentication authentication) {
        Long newsId = newsService.publishNews(newsRequest);
        return Result.success( newsId);
    }

    /**
     * 获取我的新闻列表
     */
    @GetMapping("/my")
    public Result getMyNewsList(PageRequest pageRequest, Authentication authentication) {
        PageResult<NewsVO> result = newsService.getMyNewsList(pageRequest);
        return Result.success(result);
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

    /**
     * 对新闻内容进行智能润色（由前端按钮触发）
     * 只负责润色并返回结果，不涉及新闻的保存，保存由前端调用 publishNews/editNews 接口完成
     */
    @PostMapping("/refine-content")
    // 移除泛型参数 <RefineResponse>
    public Result refineContent(@RequestBody RefineRequest request) {
        String originalContent = request.getContent();
        if (originalContent == null || originalContent.trim().isEmpty()) {
            return Result.fail("待润色的内容不能为空");
        }

        String refinedContent = contentRefinementService.refineContent(originalContent);

        if (refinedContent != null) {
            // 这里仍然将 RefineResponse 对象作为 Object 传递给 Result.success()
            return Result.success(new RefineResponse(true, "内容润色成功", refinedContent));
        } else {
            return Result.fail("内容润色失败，请稍后再试或检查Ollama服务");
        }
    }
}
