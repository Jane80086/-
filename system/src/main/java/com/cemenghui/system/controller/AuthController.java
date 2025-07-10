package com.cemenghui.system.controller;

import com.cemenghui.system.dto.LoginRequestDTO;
import com.cemenghui.system.dto.LoginResponseDTO;
import com.cemenghui.system.util.CaptchaUtil;
import com.cemenghui.system.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private CaptchaUtil captchaUtil;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private com.cemenghui.system.service.LoginService loginService;

    @GetMapping("/captcha")
    public void getCaptcha(HttpServletResponse response, @RequestParam("uuid") String uuid) throws IOException {
        String captcha = captchaUtil.generateCaptcha(6);
        // 存入 Redis，key为uuid，5分钟有效
        redisUtil.set("captcha:" + uuid, captcha, 300);

        int width = 120, height = 40;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 28));
        g.drawString(captcha, 20, 30);
        g.dispose();

        response.setContentType("image/png");
        ImageIO.write(image, "png", response.getOutputStream());
    }

}
