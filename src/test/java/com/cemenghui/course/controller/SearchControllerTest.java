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
public class SearchControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testSearchCourses() throws Exception {
        mockMvc.perform(get("/api/search/courses")
                .param("keyword", "AI"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetHotKeywords() throws Exception {
        mockMvc.perform(get("/api/search/hot-keywords"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetSearchSuggestions() throws Exception {
        mockMvc.perform(get("/api/search/suggestions")
                .param("query", "AI"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetRelatedCourses() throws Exception {
        mockMvc.perform(get("/api/search/related/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testAdvancedSearch() throws Exception {
        mockMvc.perform(get("/api/search/advanced")
                .param("keyword", "AI"))
                .andExpect(status().isOk());
    }
} 