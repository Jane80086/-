package com.cemenghui.meeting.java.com.example.demo.controller;


import com.cemenghui.meeting.bean.User;
import com.cemenghui.meeting.bean.UserLoginRequest;
import com.cemenghui.meeting.bean.UserRegisterRequest;
import com.cemenghui.meeting.controller.AuthController;
import com.cemenghui.meeting.service.UserService;
import com.cemenghui.meeting.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;



import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController 单元测试")
public class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("用户登录-成功")
    public void testLogin_Success() throws Exception {
        UserLoginRequest request = new UserLoginRequest();
        request.setUsername("testuser");
        request.setPassword("123456");

        User user = new User();
        user.setUsername("testuser");
        user.setUserType("NORMAL");

        when(userService.login(request)).thenReturn("token123");
        when(jwtUtil.generateRefreshToken("testuser")).thenReturn("refreshToken123");
        when(userService.getUserByUsername("testuser")).thenReturn(user);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").value("token123"))
                .andExpect(jsonPath("$.data.refreshToken").value("refreshToken123"));
    }

    @Test
    @DisplayName("用户登录-失败")
    public void testLogin_Failure() throws Exception {
        UserLoginRequest request = new UserLoginRequest();
        request.setUsername("testuser");
        request.setPassword("wrongpassword");

        when(userService.login(request)).thenThrow(new IllegalArgumentException("用户名或密码错误"));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("用户名或密码错误"));
    }

    @Test
    @DisplayName("用户注册-成功")
    public void testRegister_Success() throws Exception {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername("newuser");
        request.setPassword("123456");
        request.setRealName("测试用户");

        User user = new User();
        user.setUsername("newuser");
        user.setRealName("测试用户");

        when(userService.register(request)).thenReturn(user);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("newuser"));
    }

    @Test
    @DisplayName("用户注册-失败")
    public void testRegister_Failure() throws Exception {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setUsername("existinguser");
        request.setPassword("123456");

        when(userService.register(request)).thenThrow(new IllegalArgumentException("用户名已存在"));

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("用户名已存在"));
    }

    @Test
    @DisplayName("刷新Token-成功")
    public void testRefreshToken_Success() throws Exception {
        String refreshToken = "validRefreshToken";
        when(jwtUtil.validateToken(refreshToken)).thenReturn(true);
        when(jwtUtil.getUsernameFromToken(refreshToken)).thenReturn("testuser");
        when(jwtUtil.generateToken("testuser")).thenReturn("newToken123");
        when(jwtUtil.generateRefreshToken("testuser")).thenReturn("newRefreshToken123");

        mockMvc.perform(post("/api/auth/refresh")
                .header("Authorization", "Bearer " + refreshToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").value("newToken123"))
                .andExpect(jsonPath("$.data.refreshToken").value("newRefreshToken123"));
    }

    @Test
    @DisplayName("刷新Token-失败")
    public void testRefreshToken_Failure() throws Exception {
        String refreshToken = "invalidRefreshToken";
        when(jwtUtil.validateToken(refreshToken)).thenReturn(false);

        mockMvc.perform(post("/api/auth/refresh")
                .header("Authorization", "Bearer " + refreshToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("刷新token无效"));
    }

    @Test
    @DisplayName("用户登出-成功")
    public void testLogout_Success() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value("登出成功"));
    }

    @Test
    @DisplayName("获取用户信息-成功")
    public void testGetProfile_Success() throws Exception {
        User user = new User();
        user.setUsername("testuser");
        user.setRealName("测试用户");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        when(userService.getUserByUsername("testuser")).thenReturn(user);
        SecurityContextHolder.setContext(securityContext);

        mockMvc.perform(get("/api/auth/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("testuser"));
    }

    @Test
    @DisplayName("获取用户信息-失败")
    public void testGetProfile_Failure() throws Exception {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        when(userService.getUserByUsername("testuser")).thenThrow(new RuntimeException("用户不存在"));
        SecurityContextHolder.setContext(securityContext);

        mockMvc.perform(get("/api/auth/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("获取用户信息失败"));
    }
} 