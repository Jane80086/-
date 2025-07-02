package com.cemenghui.course.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class QnAControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testAskQuestion() throws Exception {
        Map<String, Object> question = new HashMap<>();
        question.put("courseId", 1);
        question.put("question", "AI测试问题");
        question.put("userId", 1);
        mockMvc.perform(post("/api/qna/ask")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(question)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"));
    }

    @Test
    public void testAutoReply() throws Exception {
        // 先插入一个问题
        Map<String, Object> question = new HashMap<>();
        question.put("courseId", 1L);
        question.put("userId", 1L);
        question.put("question", "AI自动回复测试");
        String questionJson = objectMapper.writeValueAsString(question);
        String response = mockMvc.perform(post("/api/qna/ask")
                .contentType(MediaType.APPLICATION_JSON)
                .content(questionJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        // 假设返回的data里有id
        Long id = objectMapper.readTree(response).path("data").path("id").asLong();
        mockMvc.perform(post("/api/qna/" + id + "/auto-reply"))
                .andExpect(status().isOk());
    }

    @Test
    public void testManualReply() throws Exception {
        // 先插入一个问题
        Map<String, Object> question = new HashMap<>();
        question.put("courseId", 1L);
        question.put("userId", 1L);
        question.put("question", "AI人工回复测试");
        String questionJson = objectMapper.writeValueAsString(question);
        String response = mockMvc.perform(post("/api/qna/ask")
                .contentType(MediaType.APPLICATION_JSON)
                .content(questionJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Long id = objectMapper.readTree(response).path("data").path("id").asLong();
        mockMvc.perform(post("/api/qna/" + id + "/manual-reply")
                .param("replyContent", "人工答复内容")
                .param("replyUserId", "1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testLikeQuestion() throws Exception {
        Map<String, Object> question = new HashMap<>();
        question.put("courseId", 1L);
        question.put("userId", 1L);
        question.put("question", "AI点赞测试");
        String questionJson = objectMapper.writeValueAsString(question);
        String response = mockMvc.perform(post("/api/qna/ask")
                .contentType(MediaType.APPLICATION_JSON)
                .content(questionJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Long id = objectMapper.readTree(response).path("data").path("id").asLong();
        mockMvc.perform(post("/api/qna/" + id + "/like")
                .param("userId", "1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testReportQuestion() throws Exception {
        Map<String, Object> question = new HashMap<>();
        question.put("courseId", 1L);
        question.put("userId", 1L);
        question.put("question", "AI举报测试");
        String questionJson = objectMapper.writeValueAsString(question);
        String response = mockMvc.perform(post("/api/qna/ask")
                .contentType(MediaType.APPLICATION_JSON)
                .content(questionJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Long id = objectMapper.readTree(response).path("data").path("id").asLong();
        mockMvc.perform(post("/api/qna/" + id + "/report")
                .param("userId", "1")
                .param("reason", "测试举报"))
                .andExpect(status().isOk());
    }

    @Test
    public void testAcceptAnswer() throws Exception {
        Map<String, Object> question = new HashMap<>();
        question.put("courseId", 1L);
        question.put("userId", 1L);
        question.put("question", "AI采纳测试");
        String questionJson = objectMapper.writeValueAsString(question);
        String response = mockMvc.perform(post("/api/qna/ask")
                .contentType(MediaType.APPLICATION_JSON)
                .content(questionJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Long id = objectMapper.readTree(response).path("data").path("id").asLong();
        mockMvc.perform(post("/api/qna/" + id + "/accept")
                .param("answerType", "AI")
                .param("answerContent", "AI采纳内容"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetQuestionDetail() throws Exception {
        Map<String, Object> question = new HashMap<>();
        question.put("courseId", 1L);
        question.put("userId", 1L);
        question.put("question", "AI详情测试");
        String questionJson = objectMapper.writeValueAsString(question);
        String response = mockMvc.perform(post("/api/qna/ask")
                .contentType(MediaType.APPLICATION_JSON)
                .content(questionJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Long id = objectMapper.readTree(response).path("data").path("id").asLong();
        mockMvc.perform(get("/api/qna/" + id))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetQuestionsByCoursePaged() throws Exception {
        mockMvc.perform(get("/api/qna/course/1/page")
                .param("current", "1")
                .param("size", "5"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetCourseQnA() throws Exception {
        mockMvc.perform(get("/api/qna/course/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("0"));
    }
} 