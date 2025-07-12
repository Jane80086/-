package com.cemenghui.news.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cemenghui.news.constants.NewsConstants;
import com.cemenghui.news.dto.*;
import com.cemenghui.news.entity.News;
import com.cemenghui.news.entity.UserOperationLog;
import com.cemenghui.news.entity.UserViewLog;
import com.cemenghui.news.enums.OperationType;
import com.cemenghui.news.exception.BusinessException;
import com.cemenghui.news.exception.NewsNotFoundException;
import com.cemenghui.news.exception.UnauthorizedException;
import com.cemenghui.news.mapper.NewsMapper;
import com.cemenghui.news.service.AuthorizationService;
import com.cemenghui.news.service.LogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

@ExtendWith(MockitoExtension.class)
class NewsServiceImplTest {

    @Mock
    private NewsMapper newsMapper;

    @Mock
    private AuthorizationService authorizationService;

    @Mock
    private LogService logService;

    @InjectMocks
    private NewsServiceImpl newsService;

    private SearchRequest searchRequest;
    private PageRequest pageRequest;

    @BeforeEach
    void setUp() {
        searchRequest = new SearchRequest();
        searchRequest.setPage(1);
        searchRequest.setPageSize(10);

        pageRequest = new PageRequest();
        pageRequest.setPage(1);
        pageRequest.setPageSize(10);
    }

    @Test
    void searchNews_ShouldReturnPageResult_WhenInputIsValid() {
        // 准备测试数据
        News news1 = new News();
        news1.setId(1L);
        news1.setTitle("Test News 1");
        news1.setStatus(NewsConstants.NEWS_STATUS_PUBLISHED);

        News news2 = new News();
        news2.setId(2L);
        news2.setTitle("Test News 2");
        news2.setStatus(NewsConstants.NEWS_STATUS_PUBLISHED);

        Page<News> mockPage = new Page<>(1, 10, 2);
        mockPage.setRecords(Arrays.asList(news1, news2));

        // 模拟行为
        when(newsMapper.countByCondition(eq(searchRequest), eq(NewsConstants.NEWS_STATUS_PUBLISHED)))
                .thenReturn(2L);
        when(newsMapper.findByCondition(any(Page.class), eq(searchRequest),
                eq(NewsConstants.NEWS_STATUS_PUBLISHED), isNull()))
                .thenReturn(mockPage);
        when(authorizationService.getCurrentUserId()).thenReturn(1L);

        // 执行测试
        PageResult<NewsVO> result = newsService.searchNews(searchRequest);

        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.getList().size());
        assertEquals(2, result.getTotal());
        assertEquals(1, result.getPage());
        assertEquals(10, result.getPageSize());

        // 验证转换逻辑
        NewsVO vo1 = result.getList().get(0);
        assertEquals("Test News 1", vo1.getTitle());
        assertEquals("已发布", vo1.getStatusText());

        // 验证mock调用
        verify(newsMapper).countByCondition(eq(searchRequest), eq(NewsConstants.NEWS_STATUS_PUBLISHED));
        verify(newsMapper).findByCondition(any(Page.class), eq(searchRequest),
                eq(NewsConstants.NEWS_STATUS_PUBLISHED), isNull());
        verify(authorizationService, times(2)).getCurrentUserId();
    }

    @Test
    void searchNews_ShouldReturnEmptyResult_WhenNoDataFound() {
        // 模拟行为 - 总数返回0，不会调用分页查询
        when(newsMapper.countByCondition(eq(searchRequest), eq(NewsConstants.NEWS_STATUS_PUBLISHED)))
                .thenReturn(0L);

        // 执行测试
        PageResult<NewsVO> result = newsService.searchNews(searchRequest);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.getTotal());
        assertEquals(0, result.getList().size());

        // 验证mock调用
        verify(newsMapper).countByCondition(eq(searchRequest), eq(NewsConstants.NEWS_STATUS_PUBLISHED));
        verify(newsMapper, never()).findByCondition(any(Page.class), any(), any(), any());
    }

    @Test
    void searchNews_ShouldHandleMinPageSize_WhenPageSizeIsOne() {
        // 设置最小分页
        searchRequest.setPageSize(1);

        News news = new News();
        news.setId(1L);
        news.setTitle("Single News");
        news.setStatus(NewsConstants.NEWS_STATUS_PUBLISHED);

        Page<News> mockPage = new Page<>(1, 1, 1);
        mockPage.setRecords(Collections.singletonList(news));

        // 模拟行为
        when(newsMapper.countByCondition(eq(searchRequest), eq(NewsConstants.NEWS_STATUS_PUBLISHED)))
                .thenReturn(1L);
        when(newsMapper.findByCondition(any(Page.class), eq(searchRequest),
                eq(NewsConstants.NEWS_STATUS_PUBLISHED), isNull()))
                .thenReturn(mockPage);
        when(authorizationService.getCurrentUserId()).thenReturn(1L);

        // 执行测试
        PageResult<NewsVO> result = newsService.searchNews(searchRequest);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.getList().size());
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getPage());
        assertEquals(1, result.getPageSize());

        // 验证mock调用
        verify(newsMapper).countByCondition(eq(searchRequest), eq(NewsConstants.NEWS_STATUS_PUBLISHED));
    }

    @Test
    void searchNews_ShouldHandleMaxPageSize_WhenPageSizeIsLarge() {
        // 设置大分页
        searchRequest.setPageSize(100);

        // 准备大量数据
        List<News> newsList = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            News news = new News();
            news.setId((long) i);
            news.setTitle("News " + i);
            news.setStatus(NewsConstants.NEWS_STATUS_PUBLISHED);
            newsList.add(news);
        }
        Page<News> mockPage = new Page<>(1, 100, 50);
        mockPage.setRecords(newsList);

        // 模拟行为
        when(newsMapper.countByCondition(eq(searchRequest), eq(NewsConstants.NEWS_STATUS_PUBLISHED)))
                .thenReturn(50L);
        when(newsMapper.findByCondition(any(Page.class), eq(searchRequest),
                eq(NewsConstants.NEWS_STATUS_PUBLISHED), isNull()))
                .thenReturn(mockPage);

        // 执行测试
        PageResult<NewsVO> result = newsService.searchNews(searchRequest);

        // 验证结果
        assertNotNull(result);
        assertEquals(50, result.getTotal());
        assertEquals(100, result.getPageSize());
        assertEquals(50, result.getList().size());

        // 验证mock调用
        verify(newsMapper).countByCondition(eq(searchRequest), eq(NewsConstants.NEWS_STATUS_PUBLISHED));
    }

    @Test
    void getNewsDetail_ShouldReturnNewsVO_WhenNewsIsPublished() {
        // 准备测试数据
        News news = new News();
        news.setId(1L);
        news.setTitle("Published News");
        news.setStatus(NewsConstants.NEWS_STATUS_PUBLISHED);
        news.setIsDeleted(0);

        // 模拟行为
        when(newsMapper.selectById(1L)).thenReturn(news);
        when(authorizationService.getCurrentUserId()).thenReturn(2L); // 非所有者
        when(authorizationService.isAdmin(2L)).thenReturn(false);

        // 执行测试
        NewsVO result = newsService.getNewsDetail(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals("Published News", result.getTitle());
        assertEquals("已发布", result.getStatusText());

        // 验证mock调用
        verify(newsMapper).selectById(1L);
        verify(newsMapper).updateViewCount(1L);
        verify(logService).recordView(any(UserViewLog.class));
    }

    @Test
    void getNewsDetail_ShouldThrowException_WhenNewsNotFound() {
        // 模拟行为
        when(newsMapper.selectById(1L)).thenReturn(null);

        // 执行和验证
        assertThrows(NewsNotFoundException.class, () -> {
            newsService.getNewsDetail(1L);
        });

        // 验证mock调用
        verify(newsMapper).selectById(1L);
        verify(newsMapper, never()).updateViewCount(anyLong());
        verify(logService, never()).recordView(any());
    }

    @Test
    void getNewsDetail_ShouldThrowException_WhenNoPermissionForPendingNews() {
        // 准备测试数据
        News news = new News();
        news.setId(1L);
        news.setStatus(NewsConstants.NEWS_STATUS_PENDING); // 待审核状态
        news.setIsDeleted(0);

        // 模拟行为
        when(newsMapper.selectById(1L)).thenReturn(news);
        when(authorizationService.getCurrentUserId()).thenReturn(2L); // 非所有者
        when(authorizationService.isAdmin(2L)).thenReturn(false);

        // 执行和验证
        assertThrows(UnauthorizedException.class, () -> {
            newsService.getNewsDetail(1L);
        });

        // 验证mock调用
        verify(newsMapper).selectById(1L);
        verify(newsMapper, never()).updateViewCount(anyLong());
        verify(logService, never()).recordView(any());
    }

    @Test
    void getNewsDetail_ShouldThrowException_WhenNewsIsDeleted() {
        // 准备测试数据
        News news = new News();
        news.setId(1L);
        news.setIsDeleted(1); // 已删除

        // 模拟行为
        when(newsMapper.selectById(1L)).thenReturn(news);

        // 执行和验证
        assertThrows(NewsNotFoundException.class, () -> {
            newsService.getNewsDetail(1L);
        });

        // 验证mock调用
        verify(newsMapper).selectById(1L);
        verify(newsMapper, never()).updateViewCount(anyLong());
        verify(logService, never()).recordView(any());
    }

    @Test
    @Transactional
    void publishNews_ShouldReturnNewsId_WhenInputIsValid() {
        // 准备测试数据
        NewsRequest newsRequest = new NewsRequest();
        newsRequest.setTitle("New News");
        newsRequest.setContent("Content");
        newsRequest.setAuthor("Author");

        // 模拟行为
        when(authorizationService.getCurrentUserId()).thenReturn(1L);
        doAnswer(invocation -> {
            News news = invocation.getArgument(0);
            news.setId(100L); // 模拟插入后设置ID
            return 1; // 返回影响的行数
        }).when(newsMapper).insert(any(News.class));

        // 执行测试
        Long result = newsService.publishNews(newsRequest);

        // 验证结果
        assertNotNull(result);
        assertEquals(100L, result);

        // 验证mock调用
        verify(newsMapper).insert(any(News.class));

        // 验证日志记录，使用更宽松的验证方式
        verify(logService, timeout(100).atLeastOnce())
                .recordOperation(any(UserOperationLog.class));
    }

    @Test
    void publishNews_ShouldThrowException_WhenTitleIsBlank() {
        // 准备无效的请求数据
        NewsRequest newsRequest = new NewsRequest();
        newsRequest.setTitle(""); // 空标题
        newsRequest.setContent("Content");
        newsRequest.setAuthor("Author");

        // 手动验证请求对象
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<NewsRequest>> violations = validator.validate(newsRequest);

        // 验证是否包含预期的验证错误
        assertFalse(violations.isEmpty(), "应该有验证错误");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("标题不能为空")));

        // 注意：这里不需要再调用 publishNews，因为验证已经失败
    }

    @Test
    void adminPublishNews_ShouldReturnNewsId_WhenAdminPublishes() {
        // 准备测试数据
        NewsRequest newsRequest = new NewsRequest();
        newsRequest.setTitle("Admin News");
        newsRequest.setContent("Admin Content");
        newsRequest.setAuthor("Admin");

        // 模拟行为
        when(authorizationService.getCurrentUserId()).thenReturn(1L);
        doAnswer(invocation -> {
            News news = invocation.getArgument(0);
            news.setId(101L); // 模拟插入后设置ID
            return 1;
        }).when(newsMapper).insert(any(News.class));

        // 执行测试
        Long result = newsService.adminPublishNews(newsRequest);

        // 验证结果
        assertNotNull(result);
        assertEquals(101L, result);

        // 验证新闻状态是已发布
        ArgumentCaptor<News> newsCaptor = ArgumentCaptor.forClass(News.class);
        verify(newsMapper).insert(newsCaptor.capture());
        assertEquals(NewsConstants.NEWS_STATUS_PUBLISHED, newsCaptor.getValue().getStatus());

        // 验证日志记录
        verify(logService).recordOperation(any(UserOperationLog.class));
    }

    @Test
    void adminPublishNews_ShouldThrowException_WhenContentIsBlank() {
        // 准备无效的请求数据
        NewsRequest newsRequest = new NewsRequest();
        newsRequest.setTitle("Valid Title");
        newsRequest.setContent(""); // 空内容
        newsRequest.setAuthor("Author");

        // 手动验证请求对象
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<NewsRequest>> violations = validator.validate(newsRequest);

        // 验证是否包含预期的验证错误
        assertFalse(violations.isEmpty(), "应该有验证错误");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("内容不能为空")));
    }

    @Test
    void getMyNewsList_ShouldReturnUserNews_WhenUserHasNews() {
        // 准备测试数据
        Long userId = 1L;
        News news1 = new News();
        news1.setId(1L);
        news1.setTitle("User News 1");
        news1.setUserId(userId);

        News news2 = new News();
        news2.setId(2L);
        news2.setTitle("User News 2");
        news2.setUserId(userId);

        Page<News> mockPage = new Page<>(1, 10, 2);
        mockPage.setRecords(Arrays.asList(news1, news2));

        // 模拟行为
        when(authorizationService.getCurrentUserId()).thenReturn(userId);
        when(newsMapper.countByUserId(userId)).thenReturn(2L);
        when(newsMapper.findByUserId(any(Page.class), eq(userId))).thenReturn(mockPage);

        // 执行测试
        PageResult<NewsVO> result = newsService.getMyNewsList(pageRequest);

        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.getList().size());
        assertEquals(2, result.getTotal());
        assertEquals(1, result.getPage());
        assertEquals(10, result.getPageSize());

        // 修改这里的断言 - 验证 isOwner 为 true
        assertTrue(result.getList().get(0).getIsOwner());

        // 验证返回的新闻属于当前用户
        assertEquals(userId, news1.getUserId());
        assertEquals(userId, news2.getUserId());

        // 验证mock调用
        verify(newsMapper).countByUserId(userId);
        verify(newsMapper).findByUserId(any(Page.class), eq(userId));
        verify(authorizationService, atLeastOnce()).getCurrentUserId();
    }

    @Test
    void getMyNewsList_ShouldReturnEmpty_WhenUserHasNoNews() {
        // 准备测试数据
        Long userId = 1L;

        // 模拟行为 - 总数返回0，不会调用分页查询
        when(authorizationService.getCurrentUserId()).thenReturn(userId);
        when(newsMapper.countByUserId(userId)).thenReturn(0L);

        // 执行测试
        PageResult<NewsVO> result = newsService.getMyNewsList(pageRequest);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.getTotal());
        assertEquals(0, result.getList().size());

        // 验证mock调用
        verify(newsMapper).countByUserId(userId);
        verify(newsMapper, never()).findByUserId(any(Page.class), eq(userId));
        verify(authorizationService, atLeastOnce()).getCurrentUserId();
    }

    @Test
    @Transactional
    void editNews_ShouldReturnTrue_WhenEditIsSuccessful() {
        // 准备测试数据
        Long newsId = 1L;
        Long currentUserId = 1L;

        NewsRequest newsRequest = new NewsRequest();
        newsRequest.setTitle("Updated Title");
        newsRequest.setContent("Updated Content");
        newsRequest.setAuthor("Updated Author");

        News existingNews = new News();
        existingNews.setId(newsId);
        existingNews.setUserId(currentUserId);
        existingNews.setStatus(NewsConstants.NEWS_STATUS_PENDING); // 待审核状态可编辑

        // 模拟行为
        when(authorizationService.getCurrentUserId()).thenReturn(currentUserId);
        when(newsMapper.selectById(newsId)).thenReturn(existingNews);
        when(authorizationService.canEditNews(currentUserId, newsId)).thenReturn(true);
        when(newsMapper.updateById(any(News.class))).thenReturn(1);

        // 执行测试
        boolean result = newsService.editNews(newsId, newsRequest);

        // 验证结果
        assertTrue(result);

        // 验证新闻内容已更新
        ArgumentCaptor<News> newsCaptor = ArgumentCaptor.forClass(News.class);
        verify(newsMapper).updateById(newsCaptor.capture());
        assertEquals("Updated Title", newsCaptor.getValue().getTitle());
        assertEquals("Updated Content", newsCaptor.getValue().getContent());

        // 验证日志记录
        verify(logService).recordOperation(any(UserOperationLog.class));
    }

    @Test
    void editNews_ShouldThrowException_WhenUserNotOwner() {
        Long newsId = 1L;
        Long currentUserId = 2L; // 非所有者

        News existingNews = new News();
        existingNews.setId(newsId);
        existingNews.setUserId(1L); // 所有者是另一个用户

        // 模拟行为
        when(authorizationService.getCurrentUserId()).thenReturn(currentUserId);
        when(newsMapper.selectById(newsId)).thenReturn(existingNews);
        when(authorizationService.canEditNews(currentUserId, newsId)).thenReturn(false);

        // 执行和验证
        assertThrows(UnauthorizedException.class, () -> {
            newsService.editNews(newsId, new NewsRequest());
        });
    }

    @Test
    void editNews_ShouldSetToPending_WhenEnterpriseUserEditsAnyStatusNews() {
        // 准备测试数据
        Long newsId = 1L;
        Long currentUserId = 1L; // 企业用户自己的ID
        NewsRequest newsRequest = new NewsRequest();
        newsRequest.setTitle("Updated Title");
        newsRequest.setContent("Updated Content");

        // 测试不同初始状态
        for (Integer status : Arrays.asList(
                NewsConstants.NEWS_STATUS_PENDING,
                NewsConstants.NEWS_STATUS_PUBLISHED,
                NewsConstants.NEWS_STATUS_REJECTED
        )) {
            News existingNews = new News();
            existingNews.setId(newsId);
            existingNews.setUserId(currentUserId); // 自己的新闻
            existingNews.setTitle("Original Title");
            existingNews.setContent("Original Content");
            existingNews.setStatus(status); // 不同初始状态
            existingNews.setIsDeleted(0);

            // 模拟行为 - 企业用户
            when(authorizationService.getCurrentUserId()).thenReturn(currentUserId);
            when(newsMapper.selectById(newsId)).thenReturn(existingNews);
            when(authorizationService.canEditNews(currentUserId, newsId)).thenReturn(true);
            when(authorizationService.isEnterprise(currentUserId)).thenReturn(true);
            when(authorizationService.isAdmin(currentUserId)).thenReturn(false);
            when(newsMapper.updateById(any(News.class))).thenReturn(1);

            // 执行测试
            boolean result = newsService.editNews(newsId, newsRequest);

            // 验证结果
            assertTrue(result);

            // 验证无论初始状态是什么，编辑后都变为待审核状态
            verify(newsMapper).updateById(argThat(news ->
                    news.getStatus() == NewsConstants.NEWS_STATUS_PENDING &&
                            news.getTitle().equals("Updated Title") &&
                            news.getContent().equals("Updated Content")
            ));

            // 重置mock
            reset(newsMapper);
        }
    }

    @Test
    @Transactional
    void deleteNews_ShouldReturnTrue_WhenDeleteIsSuccessful() {
        Long newsId = 1L;
        Long currentUserId = 1L;

        News news = new News();
        news.setId(newsId);
        news.setUserId(currentUserId);
        news.setIsDeleted(0); // 未删除

        // 模拟行为
        when(authorizationService.getCurrentUserId()).thenReturn(currentUserId);
        when(newsMapper.selectById(newsId)).thenReturn(news);
        when(authorizationService.canDeleteNews(currentUserId, newsId)).thenReturn(true); // 新增这行
        when(newsMapper.softDelete(newsId, currentUserId)).thenReturn(1);

        // 执行测试
        boolean result = newsService.deleteNews(newsId);

        // 验证结果
        assertTrue(result);

        // 验证软删除被调用
        verify(newsMapper).softDelete(newsId, currentUserId);

        // 验证日志记录
        verify(logService).recordOperation(any(UserOperationLog.class));
    }

    @Test
    void deleteNews_ShouldThrowException_WhenNewsNotFound() {
        Long newsId = 999L;
        Long currentUserId = 1L; // 需要添加当前用户ID

        // 模拟行为
        when(authorizationService.getCurrentUserId()).thenReturn(currentUserId);
        when(authorizationService.canDeleteNews(currentUserId, newsId)).thenReturn(true); // 新增这行
        when(newsMapper.selectById(newsId)).thenReturn(null);

        // 执行和验证
        assertThrows(NewsNotFoundException.class, () -> {
            newsService.deleteNews(newsId);
        });

        // 验证没有尝试删除
        verify(newsMapper, never()).softDelete(anyLong(), anyLong());
    }

    @Test
    void getPendingNewsList_ShouldReturnPendingNews() {
        // 准备测试数据
        News news1 = new News();
        news1.setId(1L);
        news1.setTitle("Pending News 1");
        news1.setStatus(NewsConstants.NEWS_STATUS_PENDING);

        News news2 = new News();
        news2.setId(2L);
        news2.setTitle("Pending News 2");
        news2.setStatus(NewsConstants.NEWS_STATUS_PENDING);

        Page<News> mockPage = new Page<>(1, 10, 2);
        mockPage.setRecords(Arrays.asList(news1, news2));

        // 模拟行为
        when(newsMapper.countPendingNews()).thenReturn(2L); // 新增总数模拟
        when(newsMapper.findPendingNews(any(Page.class))).thenReturn(mockPage);
        when(authorizationService.getCurrentUserId()).thenReturn(1L); // 新增当前用户模拟

        // 执行测试
        PageResult<NewsVO> result = newsService.getPendingNewsList(pageRequest);

        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.getList().size());
        assertEquals(2, result.getTotal());
        assertEquals(1, result.getPage());
        assertEquals(10, result.getPageSize());
        assertEquals("待审核", result.getList().get(0).getStatusText());

        // 验证mock调用
        verify(newsMapper).countPendingNews();
        verify(newsMapper).findPendingNews(any(Page.class));
        verify(authorizationService, atLeastOnce()).getCurrentUserId();
    }

    @Test
    void getPendingNewsList_ShouldReturnEmpty_WhenNoPendingNews() {
        // 模拟行为 - 总数为0，不会调用分页查询
        when(newsMapper.countPendingNews()).thenReturn(0L);

        // 执行测试
        PageResult<NewsVO> result = newsService.getPendingNewsList(pageRequest);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.getTotal());
        assertEquals(0, result.getList().size());

        // 验证mock调用
        verify(newsMapper).countPendingNews();
        verify(newsMapper, never()).findPendingNews(any(Page.class));
    }

    @Test
    @Transactional
    void auditNews_ShouldReturnTrue_WhenAuditIsSuccessful() {
        Long newsId = 1L;
        Long currentUserId = 1L;
        AuditRequest auditRequest = new AuditRequest();
        auditRequest.setStatus(NewsConstants.NEWS_STATUS_PUBLISHED); // 审核通过
        auditRequest.setComment("审核通过");

        News news = new News();
        news.setId(newsId);
        news.setStatus(NewsConstants.NEWS_STATUS_PENDING); // 待审核状态
        news.setIsDeleted(0);

        // 模拟行为
        when(authorizationService.getCurrentUserId()).thenReturn(currentUserId);
        when(newsMapper.selectById(newsId)).thenReturn(news);
        when(newsMapper.updateById(any(News.class))).thenReturn(1);

        // 执行测试
        boolean result = newsService.auditNews(newsId, auditRequest);

        // 验证结果
        assertTrue(result);

        // 验证更新后的新闻状态
        ArgumentCaptor<News> newsCaptor = ArgumentCaptor.forClass(News.class);
        verify(newsMapper).updateById(newsCaptor.capture());
        News updatedNews = newsCaptor.getValue();
        assertEquals(NewsConstants.NEWS_STATUS_PUBLISHED, updatedNews.getStatus());
        assertEquals(currentUserId, updatedNews.getAuditUserId());
        assertEquals(auditRequest.getComment(), updatedNews.getAuditComment());
        assertNotNull(updatedNews.getAuditTime());

        // 验证日志记录
        verify(logService).recordOperation(any(UserOperationLog.class));
    }

    @Test
    @Transactional
    void auditNews_ShouldReturnTrue_WhenRejectIsSuccessful() {
        Long newsId = 1L;
        Long currentUserId = 1L;
        AuditRequest auditRequest = new AuditRequest();
        auditRequest.setStatus(NewsConstants.NEWS_STATUS_REJECTED); // 审核拒绝
        auditRequest.setComment("内容不符合要求");

        News news = new News();
        news.setId(newsId);
        news.setStatus(NewsConstants.NEWS_STATUS_PENDING); // 待审核状态
        news.setIsDeleted(0);

        // 模拟行为
        when(authorizationService.getCurrentUserId()).thenReturn(currentUserId);
        when(newsMapper.selectById(newsId)).thenReturn(news);
        when(newsMapper.updateById(any(News.class))).thenReturn(1);

        // 执行测试
        boolean result = newsService.auditNews(newsId, auditRequest);

        // 验证结果
        assertTrue(result);

        // 验证更新后的新闻状态
        ArgumentCaptor<News> newsCaptor = ArgumentCaptor.forClass(News.class);
        verify(newsMapper).updateById(newsCaptor.capture());
        News updatedNews = newsCaptor.getValue();
        assertEquals(NewsConstants.NEWS_STATUS_REJECTED, updatedNews.getStatus());
        assertEquals(currentUserId, updatedNews.getAuditUserId());
        assertEquals(auditRequest.getComment(), updatedNews.getAuditComment());
        assertNotNull(updatedNews.getAuditTime());
    }

    @Test
    void auditNews_ShouldThrowException_WhenNewsNotFound() {
        Long newsId = 999L;
        AuditRequest auditRequest = new AuditRequest();
        auditRequest.setStatus(NewsConstants.NEWS_STATUS_PUBLISHED);

        // 模拟行为
        when(newsMapper.selectById(newsId)).thenReturn(null);

        // 执行和验证
        assertThrows(NewsNotFoundException.class, () -> {
            newsService.auditNews(newsId, auditRequest);
        });

        // 验证没有尝试更新
        verify(newsMapper, never()).updateById(any(News.class));
    }

    @Test
    void auditNews_ShouldThrowException_WhenNewsIsDeleted() {
        Long newsId = 1L;
        AuditRequest auditRequest = new AuditRequest();
        auditRequest.setStatus(NewsConstants.NEWS_STATUS_PUBLISHED);

        News news = new News();
        news.setId(newsId);
        news.setIsDeleted(1); // 已删除

        // 模拟行为
        when(newsMapper.selectById(newsId)).thenReturn(news);

        // 执行和验证
        assertThrows(NewsNotFoundException.class, () -> {
            newsService.auditNews(newsId, auditRequest);
        });

        // 验证没有尝试更新
        verify(newsMapper, never()).updateById(any(News.class));
    }

    @Test
    void auditNews_ShouldThrowException_WhenNewsIsNotPending() {
        Long newsId = 1L;
        Long currentUserId = 1L;
        AuditRequest auditRequest = new AuditRequest();
        auditRequest.setStatus(NewsConstants.NEWS_STATUS_PUBLISHED);

        News news = new News();
        news.setId(newsId);
        news.setStatus(NewsConstants.NEWS_STATUS_PUBLISHED); // 已发布状态
        news.setIsDeleted(0);

        // 模拟行为
        when(authorizationService.getCurrentUserId()).thenReturn(currentUserId);
        when(newsMapper.selectById(newsId)).thenReturn(news);

        // 执行和验证
        assertThrows(BusinessException.class, () -> {
            newsService.auditNews(newsId, auditRequest);
        });

        // 验证没有尝试更新
        verify(newsMapper, never()).updateById(any(News.class));
    }

    @Test
    void auditNews_ShouldThrowException_WhenInvalidAuditStatus() {
        Long newsId = 1L;
        Long currentUserId = 1L;
        AuditRequest auditRequest = new AuditRequest();
        auditRequest.setStatus(3); // 无效状态

        News news = new News();
        news.setId(newsId);
        news.setStatus(NewsConstants.NEWS_STATUS_PENDING); // 待审核状态
        news.setIsDeleted(0);

        // 模拟行为
        when(authorizationService.getCurrentUserId()).thenReturn(currentUserId);
        when(newsMapper.selectById(newsId)).thenReturn(news);

        // 执行和验证
        assertThrows(BusinessException.class, () -> {
            newsService.auditNews(newsId, auditRequest);
        });

        // 验证没有尝试更新
        verify(newsMapper, never()).updateById(any(News.class));
    }

    @Test
    @Transactional
    void auditNews_ShouldSucceed_WhenAuditCommentIsEmpty() {
        Long newsId = 1L;
        Long currentUserId = 1L;
        AuditRequest auditRequest = new AuditRequest();
        auditRequest.setStatus(NewsConstants.NEWS_STATUS_PUBLISHED);
        auditRequest.setComment(""); // 空审核意见

        News news = new News();
        news.setId(newsId);
        news.setStatus(NewsConstants.NEWS_STATUS_PENDING);
        news.setIsDeleted(0);

        // 模拟行为
        when(authorizationService.getCurrentUserId()).thenReturn(currentUserId);
        when(newsMapper.selectById(newsId)).thenReturn(news);
        when(newsMapper.updateById(any(News.class))).thenReturn(1);

        // 执行测试
        boolean result = newsService.auditNews(newsId, auditRequest);

        // 验证结果
        assertTrue(result);

        // 验证空审核意见被接受
        ArgumentCaptor<News> newsCaptor = ArgumentCaptor.forClass(News.class);
        verify(newsMapper).updateById(newsCaptor.capture());
        assertEquals("", newsCaptor.getValue().getAuditComment());
    }

    @Test
    void adminEditNews_ShouldUpdateNewsAndReturnTrue_WhenNewsExists() {
        // 准备测试数据
        Long newsId = 1L;
        NewsRequest request = new NewsRequest();
        request.setTitle("New Title");
        request.setContent("New Content");

        News existingNews = new News();
        existingNews.setId(newsId);
        existingNews.setIsDeleted(0);

        // 模拟行为
        when(authorizationService.getCurrentUserId()).thenReturn(1L);
        when(newsMapper.selectById(newsId)).thenReturn(existingNews);
        when(newsMapper.updateById(any(News.class))).thenReturn(1);

        // 执行测试
        boolean result = newsService.adminEditNews(newsId, request);

        // 验证结果
        assertTrue(result);
        verify(newsMapper).updateById(existingNews);
        assertEquals("New Title", existingNews.getTitle());
        assertEquals("New Content", existingNews.getContent());
    }

    @Test
    void adminEditNews_ShouldThrowNotFoundException_WhenNewsNotExist() {
        Long newsId = 999L;
        when(authorizationService.getCurrentUserId()).thenReturn(1L);
        when(newsMapper.selectById(newsId)).thenReturn(null);

        assertThrows(NewsNotFoundException.class, () -> {
            newsService.adminEditNews(newsId, new NewsRequest());
        });
    }

    @Test
    void adminEditNews_ShouldThrowNotFoundException_WhenNewsDeleted() {
        Long newsId = 1L;
        News deletedNews = new News();
        deletedNews.setIsDeleted(1);

        when(authorizationService.getCurrentUserId()).thenReturn(1L);
        when(newsMapper.selectById(newsId)).thenReturn(deletedNews);

        assertThrows(NewsNotFoundException.class, () -> {
            newsService.adminEditNews(newsId, new NewsRequest());
        });
    }

    @Test
    void adminEditNews_ShouldPartiallyUpdateFields() {
        // 准备部分更新请求(只更新title)
        NewsRequest request = new NewsRequest();
        request.setTitle("New Title");

        // 模拟现有数据
        News existingNews = new News();
        existingNews.setId(1L);
        existingNews.setTitle("Old Title");
        existingNews.setContent("Old Content");
        existingNews.setIsDeleted(0);

        when(authorizationService.getCurrentUserId()).thenReturn(1L);
        when(newsMapper.selectById(1L)).thenReturn(existingNews);
        when(newsMapper.updateById(any(News.class))).thenReturn(1);

        // 执行测试
        boolean result = newsService.adminEditNews(1L, request);

        // 验证结果
        assertTrue(result);
        assertEquals("New Title", existingNews.getTitle());  // 标题已更新
        assertEquals("Old Content", existingNews.getContent()); // 内容保持不变
        assertNotNull(existingNews.getUpdateTime()); // 更新时间被修改
    }

    @Test
    void adminEditNews_ShouldReturnFalse_WhenUpdateFails() {
        Long newsId = 1L;
        News existingNews = new News();
        existingNews.setId(newsId);
        existingNews.setIsDeleted(0);

        when(authorizationService.getCurrentUserId()).thenReturn(1L);
        when(newsMapper.selectById(newsId)).thenReturn(existingNews);
        when(newsMapper.updateById(any(News.class))).thenReturn(0); // 模拟更新失败

        boolean result = newsService.adminEditNews(newsId, new NewsRequest());

        assertFalse(result);
    }

    @Test
    void getAllNewsList_ShouldReturnPagedResults_WhenValidRequest() {
        // 准备测试数据
        SearchRequest request = new SearchRequest();
        request.setPage(1);
        request.setPageSize(10);
        request.setStatus(1); // 已发布状态

        // 准备模拟新闻数据
        List<News> mockNewsList = List.of(
                createMockNews(1L, "新闻1", 1),
                createMockNews(2L, "新闻2", 1)
        );

        Page<News> mockPage = new Page<>(request.getPage(), request.getPageSize());
        mockPage.setRecords(mockNewsList);

        // 模拟行为
        when(authorizationService.getCurrentUserId()).thenReturn(1L);
        when(newsMapper.countByCondition(eq(request), eq(request.getStatus()))).thenReturn(2L);
        when(newsMapper.findByCondition(
                any(Page.class),
                eq(request),
                eq(request.getStatus()),
                isNull()
        )).thenReturn(mockPage);

        // 执行测试
        PageResult<NewsVO> result = newsService.getAllNewsList(request);

        // 验证结果
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(2, result.getList().size());
        assertEquals(2, result.getTotal()); // 使用手动获取的总数
        assertEquals(1, result.getPage());
        assertEquals(10, result.getPageSize());

        // 验证转换后的VO内容
        NewsVO firstNews = result.getList().get(0);
        assertEquals("新闻1", firstNews.getTitle());
        assertEquals("已发布", firstNews.getStatusText());

        // 验证mock调用
        verify(newsMapper).countByCondition(eq(request), eq(request.getStatus()));
        verify(newsMapper).findByCondition(any(Page.class), eq(request), eq(request.getStatus()), isNull());
    }

    @Test
    void getAllNewsList_ShouldReturnEmpty_WhenNoMatch() {
        // 准备测试数据
        SearchRequest request = new SearchRequest();
        request.setStatus(1);

        // 模拟行为 - 总数为0，不会调用分页查询
        when(newsMapper.countByCondition(eq(request), eq(request.getStatus()))).thenReturn(0L);

        // 执行测试
        PageResult<NewsVO> result = newsService.getAllNewsList(request);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.getTotal());
        assertEquals(0, result.getList().size());

        // 验证mock调用
        verify(newsMapper).countByCondition(eq(request), eq(request.getStatus()));
        verify(newsMapper, never()).findByCondition(any(Page.class), any(), any(), any());
    }

    @Test
    void getAllNewsList_ShouldUseDefaultPaging_WhenNullRequest() {
        // 准备模拟数据
        List<News> mockNewsList = List.of(createMockNews(1L, "默认新闻", 1));
        Page<News> mockPage = new Page<>(1, 10);
        mockPage.setRecords(mockNewsList);

        // 模拟行为
        when(authorizationService.getCurrentUserId()).thenReturn(1L);
        when(newsMapper.countByCondition(
                argThat(request ->
                        request != null &&
                                request.getPage() == 1 &&
                                request.getPageSize() == 10
                ),
                isNull()
        )).thenReturn(1L);
        when(newsMapper.findByCondition(
                argThat(page -> page.getCurrent() == 1 && page.getSize() == 10),
                argThat(request ->
                        request != null &&
                                request.getPage() == 1 &&
                                request.getPageSize() == 10 &&
                                "create_time".equals(request.getSortBy()) &&
                                "desc".equals(request.getSortOrder())
                ),
                isNull(),
                isNull()
        )).thenReturn(mockPage);

        // 执行测试
        PageResult<NewsVO> result = newsService.getAllNewsList(null);

        // 验证结果
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.getList().size());
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getPage());
        assertEquals(10, result.getPageSize());

        // 验证mock调用
        verify(newsMapper).countByCondition(any(SearchRequest.class), isNull());
        verify(newsMapper).findByCondition(any(Page.class), any(SearchRequest.class), isNull(), isNull());
    }

    // 辅助方法
    private News createMockNews(Long id, String title, Integer status) {
        News news = new News();
        news.setId(id);
        news.setTitle(title);
        news.setStatus(status);
        news.setCreateTime(LocalDateTime.now());
        return news;
    }

    // 正常输入测试 - 成功删除新闻
    @Test
    void adminDeleteNews_ShouldReturnTrue_WhenNewsExistsAndNotDeleted() {
        // 准备测试数据
        Long newsId = 1L;
        Long adminUserId = 1L;
        News existingNews = new News();
        existingNews.setId(newsId);
        existingNews.setTitle("Test News");
        existingNews.setIsDeleted(0);

        // 设置Mock行为
        when(authorizationService.getCurrentUserId()).thenReturn(adminUserId);
        when(newsMapper.selectById(newsId)).thenReturn(existingNews);
        when(newsMapper.softDelete(newsId, adminUserId)).thenReturn(1);

        // 执行测试
        boolean result = newsService.adminDeleteNews(newsId);

        // 验证结果
        assertTrue(result);
        verify(newsMapper).selectById(newsId);
        verify(newsMapper).softDelete(newsId, adminUserId);
        verify(logService).recordOperation(any());
    }

    @Test
    void adminDeleteNews_ShouldThrowException_WhenNewsNotExist() {
        Long newsId = 999L;
        Long currentUserId = 1L;

        when(authorizationService.getCurrentUserId()).thenReturn(currentUserId);
        when(newsMapper.selectById(newsId)).thenReturn(null);

        assertThrows(NewsNotFoundException.class, () -> {
            newsService.adminDeleteNews(newsId);
        });

        verify(newsMapper, never()).softDelete(any(), any());
    }

    // 异常输入测试 - 新闻已删除
    @Test
    void adminDeleteNews_ShouldThrowNewsNotFoundException_WhenNewsAlreadyDeleted() {
        // 准备测试数据
        Long newsId = 2L;
        News deletedNews = new News();
        deletedNews.setId(newsId);
        deletedNews.setIsDeleted(1);

        // 设置Mock行为
        when(newsMapper.selectById(newsId)).thenReturn(deletedNews);

        // 执行测试并验证异常
        assertThrows(NewsNotFoundException.class, () -> newsService.adminDeleteNews(newsId));

        // 验证Mock调用
        verify(newsMapper).selectById(newsId);
        verify(newsMapper, never()).softDelete(any(), any());
        verify(logService, never()).recordOperation(any());
    }

    // 边界值测试 - 最小新闻ID
    @Test
    void adminDeleteNews_ShouldWork_WithMinimumNewsId() {
        // 准备测试数据
        Long minNewsId = 1L;
        Long adminUserId = 1L;
        News minNews = new News();
        minNews.setId(minNewsId);
        minNews.setIsDeleted(0);

        // 设置Mock行为
        when(authorizationService.getCurrentUserId()).thenReturn(adminUserId);
        when(newsMapper.selectById(minNewsId)).thenReturn(minNews);
        when(newsMapper.softDelete(minNewsId, adminUserId)).thenReturn(1);

        // 执行测试
        boolean result = newsService.adminDeleteNews(minNewsId);

        // 验证结果
        assertTrue(result);
        verify(newsMapper).selectById(minNewsId);
        verify(newsMapper).softDelete(minNewsId, adminUserId);
        verify(logService).recordOperation(any());
    }

    // 边界值测试 - 删除操作返回0
    @Test
    void adminDeleteNews_ShouldReturnFalse_WhenSoftDeleteReturns0() {
        // 准备测试数据
        Long newsId = 3L;
        Long adminUserId = 1L;
        News existingNews = new News();
        existingNews.setId(newsId);
        existingNews.setIsDeleted(0);

        // 设置Mock行为
        when(authorizationService.getCurrentUserId()).thenReturn(adminUserId);
        when(newsMapper.selectById(newsId)).thenReturn(existingNews);
        when(newsMapper.softDelete(newsId, adminUserId)).thenReturn(0);

        // 执行测试
        boolean result = newsService.adminDeleteNews(newsId);

        // 验证结果
        assertFalse(result);
        verify(newsMapper).selectById(newsId);
        verify(newsMapper).softDelete(newsId, adminUserId);
        verify(logService, never()).recordOperation(any());
    }

    @Test
    void getPopularNews_ShouldReturnPopularNews_WhenValidLimit() {
        // 1. 使用现有的createMockNews方法创建测试数据
        List<News> mockNewsList = List.of(
                createMockNews(1L, "新闻1", 1),
                createMockNews(2L, "新闻2", 1),
                createMockNews(3L, "新闻3", 1)
        );

        // 2. 手动设置viewCount（因为辅助方法不包含这个字段）
        mockNewsList.get(0).setViewCount(100);
        mockNewsList.get(1).setViewCount(80);
        mockNewsList.get(2).setViewCount(50);

        // 3. 模拟行为
        when(authorizationService.getCurrentUserId()).thenReturn(1L);
        when(newsMapper.findPopularNews(3, NewsConstants.NEWS_STATUS_PUBLISHED))
                .thenReturn(mockNewsList);

        // 4. 执行测试
        List<NewsVO> result = newsService.getPopularNews(3);

        // 5. 验证结果
        assertEquals(3, result.size());
        assertEquals("新闻1", result.get(0).getTitle());
        assertEquals("已发布", result.get(0).getStatusText()); // 验证状态转换
    }

    @Test
    void getPopularNews_ShouldHandleNullLimit() {
        // 1. 创建测试数据（使用现有辅助方法）
        List<News> mockNewsList = IntStream.range(1, 11)
                .mapToObj(i -> {
                    News news = createMockNews((long)i, "新闻"+i, 1);
                    news.setViewCount(100 - i); // 手动添加viewCount
                    return news;
                })
                .collect(Collectors.toList());

        // 2. 模拟行为
        when(authorizationService.getCurrentUserId()).thenReturn(1L);
        when(newsMapper.findPopularNews(10, NewsConstants.NEWS_STATUS_PUBLISHED))
                .thenReturn(mockNewsList);

        // 3. 执行测试
        List<NewsVO> result = newsService.getPopularNews(null);

        // 4. 验证
        assertEquals(10, result.size());
    }

    @Test
    void getPopularNews_ShouldReturnEmptyList_WhenNoData() {
        // 1. 只需要模拟newsMapper的行为
        when(newsMapper.findPopularNews(anyInt(), anyInt()))
                .thenReturn(Collections.emptyList());

        // 2. 执行测试
        List<NewsVO> result = newsService.getPopularNews(5);

        // 3. 验证
        assertTrue(result.isEmpty());

        // 4. 可选：验证没有调用权限检查（如果确实不需要）
        verify(authorizationService, never()).getCurrentUserId();
    }

    @Test
    void getPopularNews_ShouldOnlyReturnPublishedNews() {
        // 1. 使用现有方法创建测试数据
        News publishedNews = createMockNews(1L, "已发布新闻", 1);
        News pendingNews = createMockNews(2L, "待审核新闻", 0);

        // 手动设置viewCount
        publishedNews.setViewCount(100);
        pendingNews.setViewCount(90);

        // 2. 模拟Mapper返回混合状态数据
        when(authorizationService.getCurrentUserId()).thenReturn(1L);
        when(newsMapper.findPopularNews(anyInt(), eq(NewsConstants.NEWS_STATUS_PUBLISHED)))
                .thenReturn(List.of(publishedNews)); // 模拟只返回已发布新闻

        // 3. 执行测试
        List<NewsVO> result = newsService.getPopularNews(5);

        // 4. 验证
        assertEquals(1, result.size());
        assertEquals("已发布新闻", result.get(0).getTitle());
    }

    @Test
    void getBasicStatistics_ShouldReturnValidStats_WhenDataExists() {
        // 1. 准备模拟数据
        Map<String, Object> mockStats = new HashMap<>();
        mockStats.put("pendingCount", 5L);
        mockStats.put("publishedCount", 20L);
        mockStats.put("rejectedCount", 3L);
        mockStats.put("totalViewCount", 1500L);

        // 2. 模拟行为
        when(authorizationService.getCurrentUserId()).thenReturn(1L);
        when(newsMapper.getAdminBasicStatistics(
                NewsConstants.NEWS_STATUS_PENDING,
                NewsConstants.NEWS_STATUS_PUBLISHED,
                NewsConstants.NEWS_STATUS_REJECTED
        )).thenReturn(mockStats);

        // 3. 执行测试
        Map<String, Object> result = newsService.getBasicStatistics();

        // 4. 验证结果
        assertEquals(5L, result.get("pendingCount"));
        assertEquals(20L, result.get("publishedCount"));
        assertEquals(3L, result.get("rejectedCount"));
        assertEquals(1500L, result.get("totalViewCount"));

        // 5. 验证调用参数
        verify(newsMapper).getAdminBasicStatistics(
                NewsConstants.NEWS_STATUS_PENDING,
                NewsConstants.NEWS_STATUS_PUBLISHED,
                NewsConstants.NEWS_STATUS_REJECTED
        );
    }

    @Test
    void getBasicStatistics_ShouldReturnEmptyMap_WhenNoData() {
        // 1. 模拟空结果
        when(authorizationService.getCurrentUserId()).thenReturn(1L);
        when(newsMapper.getAdminBasicStatistics(anyInt(), anyInt(), anyInt()))
                .thenReturn(Collections.emptyMap());

        // 2. 执行测试
        Map<String, Object> result = newsService.getBasicStatistics();

        // 3. 验证
        assertTrue(result.isEmpty());
    }

    @Test
    void getBasicStatistics_ShouldHandleZeroCounts() {
        // 1. 模拟所有统计为0的情况
        Map<String, Object> zeroStats = new HashMap<>();
        zeroStats.put("pendingCount", 0L);
        zeroStats.put("publishedCount", 0L);
        zeroStats.put("rejectedCount", 0L);
        zeroStats.put("totalViewCount", 0L);

        // 2. 模拟行为
        when(authorizationService.getCurrentUserId()).thenReturn(1L);
        when(newsMapper.getAdminBasicStatistics(anyInt(), anyInt(), anyInt()))
                .thenReturn(zeroStats);

        // 3. 执行测试
        Map<String, Object> result = newsService.getBasicStatistics();

        // 4. 验证
        assertEquals(0L, result.get("pendingCount"));
        assertEquals(0L, result.get("publishedCount"));
        assertEquals(0L, result.get("rejectedCount"));
        assertEquals(0L, result.get("totalViewCount"));
    }

    @Test
    void getViewTrend_ShouldReturnTrendData_WhenValidDateRange() {
        // 1. 准备测试数据
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();

        List<Map<String, Object>> mockTrendData = List.of(
                createDailyTrend("2023-01-01", 100),
                createDailyTrend("2023-01-02", 150)
        );

        // 2. 模拟行为
        when(authorizationService.getCurrentUserId()).thenReturn(1L);
        when(newsMapper.getViewTrend(startDate, endDate)).thenReturn(mockTrendData);

        // 3. 执行测试
        List<Map<String, Object>> result = newsService.getViewTrend(startDate, endDate);

        // 4. 验证结果
        assertEquals(2, result.size());
        assertEquals("2023-01-01", result.get(0).get("date"));
        assertEquals(100, result.get(0).get("views"));
        verify(newsMapper).getViewTrend(startDate, endDate);
    }

    // 辅助方法创建每日趋势数据
    private Map<String, Object> createDailyTrend(String date, int views) {
        Map<String, Object> trend = new HashMap<>();
        trend.put("date", date);
        trend.put("views", views);
        return trend;
    }

    @Test
    void getViewTrend_ShouldHandleSingleDayRange() {
        // 1. 测试同一天的情况
        LocalDate date = LocalDate.now();
        List<Map<String, Object>> mockData = List.of(
                createDailyTrend(date.toString(), 50)
        );

        when(authorizationService.getCurrentUserId()).thenReturn(1L);
        when(newsMapper.getViewTrend(date, date)).thenReturn(mockData);

        List<Map<String, Object>> result = newsService.getViewTrend(date, date);

        assertEquals(1, result.size());
        assertEquals(date.toString(), result.get(0).get("date"));
    }

    @Test
    void getViewTrend_ShouldSwapDates_WhenStartAfterEnd() {
        LocalDate start = LocalDate.now();
        LocalDate end = start.minusDays(7);

        List<Map<String, Object>> mockData = List.of(
                createDailyTrend(end.toString(), 10),
                createDailyTrend(end.plusDays(1).toString(), 20)
        );

        when(authorizationService.getCurrentUserId()).thenReturn(1L);
        // 注意：这里期望方法内部会自动交换日期
        when(newsMapper.getViewTrend(end, start)).thenReturn(mockData);

        List<Map<String, Object>> result = newsService.getViewTrend(start, end);

        assertEquals(2, result.size());
        verify(newsMapper).getViewTrend(end, start); // 验证参数已交换
    }

    @Test
    void getViewTrend_ShouldReturnEmpty_WhenNoData() {
        LocalDate start = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now();

        when(authorizationService.getCurrentUserId()).thenReturn(1L);
        when(newsMapper.getViewTrend(start, end)).thenReturn(Collections.emptyList());

        List<Map<String, Object>> result = newsService.getViewTrend(start, end);

        assertTrue(result.isEmpty());
    }

    @Test
    void getViewTrend_ShouldThrowException_WhenNullDates() {
        assertThrows(IllegalArgumentException.class, () -> {
            newsService.getViewTrend(null, LocalDate.now());
        });

        assertThrows(IllegalArgumentException.class, () -> {
            newsService.getViewTrend(LocalDate.now(), null);
        });
    }

    @Test
    void getHotNews_ShouldReturnHotNews_WhenValidInput() {
        // 准备测试数据
        Integer limit = 5;
        Integer status = NewsConstants.NEWS_STATUS_PUBLISHED;
        List<News> mockNewsList = List.of(
                createMockNews(1L, "热门新闻1", status),
                createMockNews(2L, "热门新闻2", status)
        );

        // 模拟行为
        when(authorizationService.getCurrentUserId()).thenReturn(1L);
        when(newsMapper.getHotNews(limit, status)).thenReturn(mockNewsList);

        // 执行测试
        List<NewsVO> result = newsService.getHotNews(limit, status);

        // 验证结果
        assertEquals(2, result.size());
        assertEquals("热门新闻1", result.get(0).getTitle());
        assertEquals("热门新闻2", result.get(1).getTitle());

        // 验证交互
        verify(newsMapper).getHotNews(limit, status);
        verify(authorizationService).getCurrentUserId();
    }

    @Test
    void getHotNews_ShouldUseDefaultLimit_WhenLimitIsNullOrInvalid() {
        // 测试null值情况
        List<News> mockNewsList = List.of(createMockNews(1L, "默认热门新闻", null));

        when(authorizationService.getCurrentUserId()).thenReturn(1L);
        when(newsMapper.getHotNews(10, null)).thenReturn(mockNewsList); // 预期使用默认值10

        List<NewsVO> result = newsService.getHotNews(null, null);

        assertEquals(1, result.size());
        verify(newsMapper).getHotNews(10, null);

        // 测试无效limit值
        when(newsMapper.getHotNews(10, null)).thenReturn(mockNewsList);
        result = newsService.getHotNews(-1, null);
        verify(newsMapper, times(2)).getHotNews(10, null); // 再次验证使用默认值10
    }

    @Test
    void getHotNews_ShouldHandleEmptyResult() {
        when(authorizationService.getCurrentUserId()).thenReturn(1L);
        when(newsMapper.getHotNews(anyInt(), anyInt())).thenReturn(Collections.emptyList());

        List<NewsVO> result = newsService.getHotNews(5, NewsConstants.NEWS_STATUS_PUBLISHED);

        assertTrue(result.isEmpty());
        verify(newsMapper).getHotNews(5, NewsConstants.NEWS_STATUS_PUBLISHED);
    }

    @Test
    void getAdminNewsDetail_ShouldReturnNewsVO_WhenNewsExists() {
        // 准备测试数据
        Long newsId = 1L;
        News news = new News();
        news.setId(newsId);
        news.setTitle("Admin News");
        news.setStatus(NewsConstants.NEWS_STATUS_PUBLISHED);
        news.setIsDeleted(0);

        // 模拟行为
        when(newsMapper.selectById(newsId)).thenReturn(news);
        when(authorizationService.getCurrentUserId()).thenReturn(1L);

        // 执行测试
        NewsVO result = newsService.getAdminNewsDetail(newsId);

        // 验证结果
        assertNotNull(result);
        assertEquals("Admin News", result.getTitle());
        assertEquals("已发布", result.getStatusText());

        // 验证mock调用
        verify(newsMapper).selectById(newsId);
        verify(authorizationService).getCurrentUserId();
    }

    @Test
    void getAdminNewsDetail_ShouldThrowException_WhenNewsNotFound() {
        Long newsId = 999L;
        when(newsMapper.selectById(newsId)).thenReturn(null);

        assertThrows(NewsNotFoundException.class, () -> {
            newsService.getAdminNewsDetail(newsId);
        });
    }

    @Test
    void getAdminNewsDetail_ShouldThrowException_WhenNewsIsDeleted() {
        Long newsId = 1L;
        News news = new News();
        news.setId(newsId);
        news.setIsDeleted(1);

        when(newsMapper.selectById(newsId)).thenReturn(news);

        assertThrows(NewsNotFoundException.class, () -> {
            newsService.getAdminNewsDetail(newsId);
        });
    }


}