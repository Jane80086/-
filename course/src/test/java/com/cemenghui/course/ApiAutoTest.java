package com.cemenghui.course;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import com.cemenghui.course.config.JwtFilter;
import com.cemenghui.course.config.JwtInterceptor;

@SpringBootTest
@AutoConfigureMockMvc
@ComponentScan(basePackages = "com.cemenghui.course", 
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {JwtFilter.class, JwtInterceptor.class})
    })
public class ApiAutoTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/user/info")
    void testGetUserInfo() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/info"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("POST /api/user/login")
    void testUserLogin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/login"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("POST /api/user/register")
    void testUserRegister() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/register"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("PUT /api/user/info")
    void testUpdateUserInfo() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/user/info")
                .contentType("application/json")
                .content("{\"name\":\"test\",\"email\":\"test@test.com\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("POST /api/user/add")
    void testUserAdd() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/add"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("POST /api/user/update")
    void testUserUpdate() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/update"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("GET /api/test/hello")
    void testHello() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/test/hello"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("GET /api/test/courses")
    void testTestCourses() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/test/courses"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("GET /api/stats/overview")
    void testStatsOverview() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/stats/overview"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("GET /api/stats/chart")
    void testStatsChart() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/stats/chart"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("GET /api/stats/dashboard")
    void testStatsDashboard() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/stats/dashboard"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("GET /api/stats/course/{id}/analytics")
    void testStatsCourseAnalytics() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/stats/course/1/analytics"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
} 