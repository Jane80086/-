package com.cemenghui.course.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class RecommendationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testAnalyzeCourseHeat() throws Exception {
        mockMvc.perform(get("/api/recommendations/heat-analysis"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGenerateRecommendations() throws Exception {
        mockMvc.perform(get("/api/recommendations/generate"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetHotCourses() throws Exception {
        mockMvc.perform(get("/api/recommendations/hot"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetPersonalizedRecommendations() throws Exception {
        mockMvc.perform(get("/api/recommendations/personalized")
                .param("userId", "1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetTrendingCourses() throws Exception {
        mockMvc.perform(get("/api/recommendations/trending"))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateFeaturedCourses() throws Exception {
        mockMvc.perform(post("/api/recommendations/update-featured")
                .param("autoUpdate", "true"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetMcpServerStatus() throws Exception {
        mockMvc.perform(get("/api/recommendations/mcp-status"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetRecommendationStats() throws Exception {
        mockMvc.perform(get("/api/recommendations/stats"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetHomepageRecommendations() throws Exception {
        mockMvc.perform(get("/api/recommendations/homepage"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetRecommendationsByCategory() throws Exception {
        mockMvc.perform(get("/api/recommendations/by-category/test"))
                .andExpect(status().isOk());
    }
} 