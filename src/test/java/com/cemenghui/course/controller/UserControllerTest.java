package com.cemenghui.course.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.HashMap;
import java.util.Map;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testAddUser() throws Exception {
        Map<String, Object> user = new HashMap<>();
        user.put("username", "testuser");
        user.put("password", "123456");
        user.put("userType", "normal");
        user.put("email", "testuser@example.com");
        String json = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/user/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateUser() throws Exception {
        Map<String, Object> user = new HashMap<>();
        user.put("username", "testuser");
        user.put("password", "654321");
        user.put("userType", "normal");
        user.put("email", "testuser@example.com");
        String json = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/user/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
    }

    @Test
    public void testAddUserWithEmptyUsername() throws Exception {
        Map<String, Object> user = new HashMap<>();
        user.put("username", "");
        user.put("password", "123456");
        user.put("userType", "normal");
        String json = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/user/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("失败")));
    }

    @Test
    public void testAddUserWithIllegalUserType() throws Exception {
        Map<String, Object> user = new HashMap<>();
        user.put("username", "testuser2");
        user.put("password", "123456");
        user.put("userType", "illegal");
        user.put("email", "testuser2@example.com");
        String json = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/user/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("失败")));
    }

    @Test
    public void testAddUserWithShortPassword() throws Exception {
        Map<String, Object> user = new HashMap<>();
        user.put("username", "testuser3");
        user.put("password", "1");
        user.put("userType", "normal");
        user.put("email", "testuser3@example.com");
        String json = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/user/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("失败")));
    }

    @Test
    public void testAddDuplicateUser() throws Exception {
        // 使用时间戳确保用户名唯一
        String timestamp = String.valueOf(System.currentTimeMillis());
        String username = "dupuser" + timestamp;
        String email = "dupuser" + timestamp + "@example.com";
        
        Map<String, Object> user = new HashMap<>();
        user.put("username", username);
        user.put("password", "123456");
        user.put("userType", "normal");
        user.put("email", email);
        String json = objectMapper.writeValueAsString(user);
        
        // 第一次添加应该成功
        mockMvc.perform(post("/user/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("成功")));
        
        // 再次添加应该失败
        mockMvc.perform(post("/user/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("失败")));
    }
} 