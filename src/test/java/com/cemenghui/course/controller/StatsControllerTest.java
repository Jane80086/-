package com.cemenghui.course.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class StatsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetDashboardStats() throws Exception {
        mockMvc.perform(get("/api/stats/dashboard"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetCourseAnalytics() throws Exception {
        mockMvc.perform(get("/api/stats/course/1/analytics"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetUserHistory() throws Exception {
        mockMvc.perform(get("/api/stats/user/1/history"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetTrends() throws Exception {
        mockMvc.perform(get("/api/stats/trends"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetRevenueStats() throws Exception {
        mockMvc.perform(get("/api/stats/revenue"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetSystemHealth() throws Exception {
        mockMvc.perform(get("/api/stats/health"))
                .andExpect(status().isOk());
    }
} 