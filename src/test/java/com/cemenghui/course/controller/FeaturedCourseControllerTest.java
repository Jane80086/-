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
public class FeaturedCourseControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetFeaturedCourses() throws Exception {
        mockMvc.perform(get("/api/featured/list"))
                .andExpect(status().isOk());
    }

    @Test
    public void testPromoteToFeatured() throws Exception {
        mockMvc.perform(post("/api/featured/1/promote")
                .param("priority", "1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testRemoveFromFeatured() throws Exception {
        mockMvc.perform(delete("/api/featured/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdatePriority() throws Exception {
        mockMvc.perform(put("/api/featured/1/priority")
                .param("priority", "2"))
                .andExpect(status().isOk());
    }

    @Test
    public void testPromoteNonExistentCourse() throws Exception {
        mockMvc.perform(post("/api/featured/999999/promote")
                .param("priority", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("失败")));
    }

    @Test
    public void testPromoteWithIllegalPriority() throws Exception {
        mockMvc.perform(post("/api/featured/1/promote")
                .param("priority", "-1"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("失败")));
    }

    @Test
    public void testRepeatPromote() throws Exception {
        mockMvc.perform(post("/api/featured/1/promote")
                .param("priority", "1"))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/featured/1/promote")
                .param("priority", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("失败")));
    }

    @Test
    public void testRemoveNonExistentFeatured() throws Exception {
        mockMvc.perform(delete("/api/featured/999999"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("失败")));
    }
} 