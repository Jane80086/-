package com.cemenghui.system.controller;

import com.cemenghui.system.dto.LoginRequestDTO;
import com.cemenghui.system.dto.LoginResponseDTO;
import com.cemenghui.system.util.CaptchaUtil;
import com.cemenghui.system.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private CaptchaUtil captchaUtil;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private com.cemenghui.system.service.LoginService loginService;

    /**
     * 获取验证码图片，uuid参数可选，若缺失自动生成。redis异常时降级为session存储，所有异常都catch避免500。
     */
    @GetMapping("/captcha")
    public void getCaptcha(HttpServletResponse response, @RequestParam(value = "uuid", required = false) String uuid, HttpSession session) {
        try {
            if (uuid == null || uuid.isEmpty()) {
                uuid = UUID.randomUUID().toString();
                response.setHeader("X-Captcha-UUID", uuid);
            }
            String captcha = captchaUtil.generateCaptcha(6);
            // 优先存入 Redis，异常时降级为 session
            boolean redisOk = true;
            try {
                redisUtil.set("captcha:" + uuid, captcha, 300);
            } catch (Exception e) {
                redisOk = false;
            }
            if (!redisOk) {
                session.setAttribute("captcha:" + uuid, captcha);
            }
            
            int width = 120, height = 40;
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();
            try {
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, width, height);
                // 干扰线
                for (int i = 0; i < 5; i++) {
                    g.setColor(new Color((int)(Math.random()*150), (int)(Math.random()*150), (int)(Math.random()*150)));
                    int x1 = (int)(Math.random()*width);
                    int y1 = (int)(Math.random()*height);
                    int x2 = (int)(Math.random()*width);
                    int y2 = (int)(Math.random()*height);
                    g.drawLine(x1, y1, x2, y2);
                }
                // 设置字体为 SansSerif
                Font font = new Font("SansSerif", Font.BOLD, 28);
                g.setFont(font);
                // 随机颜色绘制验证码
                for (int i = 0; i < captcha.length(); i++) {
                    g.setColor(new Color((int)(Math.random()*100), (int)(Math.random()*100), (int)(Math.random()*100)));
                    g.drawString(String.valueOf(captcha.charAt(i)), 18*i+10, 30);
                }
            } finally {
                g.dispose();
            }
            response.setContentType("image/png");
            ImageIO.write(image, "png", response.getOutputStream());
        } catch (Exception e) {
            try {
                response.setStatus(200);
                response.setContentType("text/plain;charset=UTF-8");
                response.getWriter().write("验证码生成失败，请联系管理员");
            } catch (IOException ignored) {}
        }
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO loginRequest) {
        String username = loginRequest.getAccount();
        String password = loginRequest.getPassword();
        String userType = loginRequest.getUserType();
        boolean rememberMe = loginRequest.isRememberMe();
        String dynamicCode = loginRequest.getDynamicCode();
        return loginService.login(username, password, userType, rememberMe, dynamicCode);
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Map<String, Object> logout() {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "登出成功");
        return result;
    }
}
