package com.cemenghui.system.controller;

import com.cemenghui.system.entity.Enterprise;
import com.cemenghui.system.service.EnterpriseService;
import com.cemenghui.common.JWTUtil;
import com.cemenghui.system.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/enterprise")
public class EnterpriseController {

    @Autowired
    private EnterpriseService enterpriseService;

    @Autowired
    private JWTUtil jwtUtil;

    @GetMapping("/list")
    public ResponseEntity<ResultVO<Map<String, Object>>> getEnterpriseList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String enterpriseName,
            @RequestParam(required = false) String creditCode,
            @RequestParam(required = false) String status,
            @RequestHeader("Authorization") String token
    ) {
        // 验证超级管理员权限
        if (!isSuperAdmin(token)) {
            return new ResponseEntity<>(ResultVO.unauthorized("无权限访问"), HttpStatus.UNAUTHORIZED);
        }

        Map<String, Object> result = enterpriseService.getEnterpriseList(page, size, enterpriseName, creditCode, status);
        return new ResponseEntity<>(ResultVO.success(result), HttpStatus.OK);
    }

    /**
     * 导出企业列表为Excel
     */
    @GetMapping("/export")
    public void exportEnterpriseList(
            @RequestParam(required = false) String enterpriseName,
            @RequestParam(required = false) String creditCode,
            @RequestParam(required = false) String status,
            @RequestHeader("Authorization") String token,
            HttpServletResponse response) throws IOException {
        if (!isSuperAdmin(token)) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "无权限访问");
            return;
        }

        enterpriseService.exportEnterpriseList(enterpriseName, creditCode, status, response);
    }

    @PostMapping("/create")
    public Map<String, Object> createEnterprise(@RequestBody Enterprise enterprise) {
        // 自动生成企业ID，避免非空约束违反
        if (enterprise.getEnterpriseId() == null) {
            enterprise.setEnterpriseId(String.valueOf(System.currentTimeMillis())); // 使用时间戳作为临时ID
        }
        // 设置创建时间
        if (enterprise.getCreateTime() == null) {
            enterprise.setCreateTime(java.time.LocalDateTime.now());
        }
        return enterpriseService.createEnterprise(enterprise);
    }

    @DeleteMapping("/{enterpriseId}")
    public Map<String, Object> deleteEnterprise(@PathVariable("enterpriseId") String enterpriseId) {
        return enterpriseService.deleteEnterprise(enterpriseId);
    }

    @PutMapping("/{enterpriseId}")
    public Map<String, Object> updateEnterprise(@PathVariable("enterpriseId") String enterpriseId, @RequestBody Enterprise enterprise) {
        enterprise.setEnterpriseId(enterpriseId);
        return enterpriseService.updateEnterprise(enterprise);
    }

    // 辅助方法：验证是否为超级管理员
    private boolean isSuperAdmin(String token) {
        try {
            String cleanToken = jwtUtil.extractTokenFromHeader(token);
            if (cleanToken == null) {
                return false;
            }
            String account = jwtUtil.getAccountFromToken(cleanToken);
            System.out.println("isSuperAdmin校验，token: [" + cleanToken + "]，解析账号: [" + account + "]");
            return account != null && account.startsWith("0000");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}