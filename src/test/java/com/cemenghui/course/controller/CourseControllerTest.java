package com.cemenghui.course.controller;

import com.cemenghui.course.entity.Course;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CourseControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetCourseList() throws Exception {
        mockMvc.perform(get("/api/course/list"))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateCourse() throws Exception {
        Course course = new Course();
        course.setTitle("测试课程");
        course.setDescription("测试描述");
        course.setInstructorId(1L);
        course.setPrice(new BigDecimal("99.99"));
        course.setDuration(10);
        course.setCategory("编程");
        course.setStatus("DRAFT");
        course.setCoverImage("cover.jpg");
        course.setVideoUrl("video.mp4");

        mockMvc.perform(post("/api/course/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(course)))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetCourseDetailNotFound() throws Exception {
        mockMvc.perform(get("/api/course/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateCourseNotFound() throws Exception {
        Course course = new Course();
        course.setTitle("新标题");
        course.setDescription("新描述");
        mockMvc.perform(put("/api/course/999999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(course)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteCourseNotFound() throws Exception {
        mockMvc.perform(delete("/api/course/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testSearchCourses() throws Exception {
        mockMvc.perform(get("/api/course/search?keyword=测试"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetHotSearchTrends() throws Exception {
        mockMvc.perform(get("/api/course/trends"))
                .andExpect(status().isOk());
    }

    @Test
    public void testUnpublishCourseNotFound() throws Exception {
        mockMvc.perform(put("/api/course/999999/unpublish"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testSubmitForReviewNotFound() throws Exception {
        mockMvc.perform(post("/api/course/999999/submit"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetMyCourses() throws Exception {
        mockMvc.perform(get("/api/course/my"))
                .andExpect(status().isOk());
    }
} 