package com.cemenghui.news.controller;

import com.cemenghui.common.Result;
import com.cemenghui.news.dto.SearchRequest;
import com.cemenghui.news.dto.NewsVO;
import com.cemenghui.news.dto.PageResult;
import com.cemenghui.news.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/news")
@CrossOrigin
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    /**
     * 搜索新闻
     */
    @GetMapping("/search")
    public Result searchNews(SearchRequest searchRequest) {
        PageResult<NewsVO> result = newsService.searchNews(searchRequest);
        return Result.success(result);
    }

    /**
     * 获取新闻详情
     */
    @GetMapping("/{newsId}")
    public Result getNewsDetail(@PathVariable Long newsId) {
        NewsVO newsVO = newsService.getNewsDetail(newsId);
        return Result.success(newsVO);
    }

    /**
     * 获取热门新闻
     */
    @GetMapping("/popular")
    public Result getPopularNews(@RequestParam(defaultValue = "10") Integer limit) {
        List<NewsVO> result = newsService.getPopularNews(limit);
        return Result.success(result);
    }
}