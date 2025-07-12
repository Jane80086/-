package com.cemenghui.course.service.impl;

import com.cemenghui.course.entity.Course;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class MCPServiceImplTest {
    @InjectMocks
    private MCPServiceImpl mcpServiceImpl;
    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testOptimizeCourseContent_Success() {
        Course course = new Course();
        course.setTitle("Java");
        course.setDescription("desc");
        MCPServiceImpl.CourseOptimizationResult result = new MCPServiceImpl.CourseOptimizationResult();
        result.setOptimizedTitle("优化后标题");
        result.setOptimizedDescription("优化后描述");
        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(MCPServiceImpl.CourseOptimizationResult.class))).thenReturn(result);
        MCPServiceImpl.CourseOptimizationResult r = mcpServiceImpl.optimizeCourseContent(course, "初学者", "技术");
        assertEquals("优化后标题", r.getOptimizedTitle());
    }

    @Test
    void testOptimizeCourseContent_NullResult() {
        Course course = new Course();
        course.setTitle("Java");
        course.setDescription("desc");
        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(MCPServiceImpl.CourseOptimizationResult.class))).thenReturn(null);
        assertNull(mcpServiceImpl.optimizeCourseContent(course, "初学者", "技术"));
    }

    @Test
    void testOptimizeCourseContent_DefaultParamsAndAuthToken() {
        // 设置authToken
        org.springframework.test.util.ReflectionTestUtils.setField(mcpServiceImpl, "authToken", "token123");
        Course course = new Course();
        course.setTitle(null); // 测试null title
        course.setDescription(null); // 测试null desc
        MCPServiceImpl.CourseOptimizationResult result = new MCPServiceImpl.CourseOptimizationResult();
        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(MCPServiceImpl.CourseOptimizationResult.class))).thenReturn(result);
        // targetAudience/courseType传null，走默认分支
        assertNotNull(mcpServiceImpl.optimizeCourseContent(course, null, null));
    }

    @Test
    void testAnalyzeCourseSEO() {
        MCPServiceImpl.SEOAnalysisResult result = new MCPServiceImpl.SEOAnalysisResult();
        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(MCPServiceImpl.SEOAnalysisResult.class))).thenReturn(result);
        assertNotNull(mcpServiceImpl.analyzeCourseSEO("title", "desc"));
    }

    @Test
    void testAnalyzeCourseSEO_WithAuthToken() {
        org.springframework.test.util.ReflectionTestUtils.setField(mcpServiceImpl, "authToken", "token456");
        MCPServiceImpl.SEOAnalysisResult result = new MCPServiceImpl.SEOAnalysisResult();
        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(MCPServiceImpl.SEOAnalysisResult.class))).thenReturn(result);
        assertNotNull(mcpServiceImpl.analyzeCourseSEO("", ""));
    }

    @Test
    void testGenerateCourseTemplates() {
        MCPServiceImpl.CourseTemplateResult result = new MCPServiceImpl.CourseTemplateResult();
        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(MCPServiceImpl.CourseTemplateResult.class))).thenReturn(result);
        assertNotNull(mcpServiceImpl.generateCourseTemplates("type", "audience"));
    }

    @Test
    void testGenerateCourseTemplates_DefaultAudienceAndAuthToken() {
        org.springframework.test.util.ReflectionTestUtils.setField(mcpServiceImpl, "authToken", "token789");
        MCPServiceImpl.CourseTemplateResult result = new MCPServiceImpl.CourseTemplateResult();
        when(restTemplate.postForObject(anyString(), any(HttpEntity.class), eq(MCPServiceImpl.CourseTemplateResult.class))).thenReturn(result);
        // targetAudience传null，走默认分支
        assertNotNull(mcpServiceImpl.generateCourseTemplates("type", null));
    }

    @Test
    void testCourseOptimizationResultGettersSetters() {
        MCPServiceImpl.CourseOptimizationResult result = new MCPServiceImpl.CourseOptimizationResult();
        result.setOptimizedTitle("t");
        result.setOptimizedDescription("d");
        result.setImprovementSuggestions(new String[]{"a", "b"});
        result.setSeoKeywords(new String[]{"k1", "k2"});
        assertEquals("t", result.getOptimizedTitle());
        assertEquals("d", result.getOptimizedDescription());
        assertArrayEquals(new String[]{"a", "b"}, result.getImprovementSuggestions());
        assertArrayEquals(new String[]{"k1", "k2"}, result.getSeoKeywords());
    }

    @Test
    void testSEOAnalysisResultGettersSetters() {
        MCPServiceImpl.SEOAnalysisResult result = new MCPServiceImpl.SEOAnalysisResult();
        result.setSeoScore(100);
        result.setTitleLength(10);
        result.setDescriptionLength(20);
        result.setSuggestions(new String[]{"s1", "s2"});
        result.setGrade("A");
        assertEquals(100, result.getSeoScore());
        assertEquals(10, result.getTitleLength());
        assertEquals(20, result.getDescriptionLength());
        assertArrayEquals(new String[]{"s1", "s2"}, result.getSuggestions());
        assertEquals("A", result.getGrade());
    }

    @Test
    void testCourseTemplateResultGettersSetters() {
        MCPServiceImpl.CourseTemplateResult result = new MCPServiceImpl.CourseTemplateResult();
        result.setTitles(new String[]{"t1", "t2"});
        result.setDescriptions(new String[]{"d1", "d2"});
        assertArrayEquals(new String[]{"t1", "t2"}, result.getTitles());
        assertArrayEquals(new String[]{"d1", "d2"}, result.getDescriptions());
    }
} 