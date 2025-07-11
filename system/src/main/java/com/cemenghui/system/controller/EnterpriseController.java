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
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;

@RestController
@RequestMapping("/api/enterprise")
public class EnterpriseController {

    @Autowired
    private EnterpriseService enterpriseService;

    @Autowired
    private JWTUtil jwtUtil;

    @GetMapping("/list")
    public Object getEnterpriseList(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @RequestParam(value = "enterpriseName", required = false) String enterpriseName,
            @RequestParam(value = "creditCode", required = false) String creditCode,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "sortField", required = false) String sortField,
            @RequestParam(value = "sortOrder", required = false) String sortOrder, // 新增
            @RequestHeader(value = "Authorization", required = false) String token
    ) {
        System.out.println("EnterpriseController /list 方法被调用");
        int pageNum = page != null ? page : 1;
        int sizeNum = (pageSize != null) ? pageSize : (size != null ? size : 10);
        System.out.println("收到企业列表请求，参数：page=" + pageNum + ", size=" + sizeNum + ", enterpriseName=" + enterpriseName + ", creditCode=" + creditCode + ", status=" + status + ", sortField=" + sortField + ", sortOrder=" + sortOrder);
        try {
            if (token == null || !isSuperAdmin(token)) {
                System.out.println("无权限访问,token=" + token);
                return new ResponseEntity<>(ResultVO.unauthorized("无权限访问"), HttpStatus.UNAUTHORIZED);
            }
            // 字段名映射，兼容前端驼峰
            if ("createTime".equals(sortField)) {
                sortField = "create_time";
            } else if ("updateTime".equals(sortField)) {
                sortField = "update_time";
            }
            Map<String, Object> result = enterpriseService.getEnterpriseList(pageNum, sizeNum, enterpriseName, creditCode, status, sortField, sortOrder); // 传递sortOrder
            System.out.println("企业Service返回：" + result);
            return new ResponseEntity<>(ResultVO.success(result), HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("企业列表接口异常: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(ResultVO.error("企业列表接口异常: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
    public ResponseEntity<?> createEnterprise(@RequestBody Enterprise enterprise) {
        try {
            // 自动生成企业ID，避免非空约束违反
            if (enterprise.getEnterpriseId() == null || enterprise.getEnterpriseId().trim().isEmpty()) {
                enterprise.setEnterpriseId(String.valueOf(System.currentTimeMillis()));
            }
            if (enterprise.getCreateTime() == null) {
                enterprise.setCreateTime(java.time.LocalDateTime.now());
            }
            Map<String, Object> result = enterpriseService.createEnterprise(enterprise);
            return ResponseEntity.ok(result);
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage() != null && e.getMessage().contains("enterprise_social_credit_code_key")) {
                return ResponseEntity.badRequest().body("统一社会信用代码已存在，请更换");
            }
            return ResponseEntity.status(500).body("数据库约束冲突：" + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("服务器内部错误：" + e.getMessage());
        }
    }

    @DeleteMapping("/{enterpriseId}")
    public Map<String, Object> deleteEnterprise(@PathVariable("enterpriseId") String enterpriseId) {
        return enterpriseService.deleteEnterprise(enterpriseId);
    }

    @DeleteMapping("/delete/{enterpriseId}")
    public Map<String, Object> deleteEnterpriseCompat(@PathVariable("enterpriseId") String enterpriseId) {
        return deleteEnterprise(enterpriseId);
    }

    @PutMapping("/{enterpriseId}")
    public Map<String, Object> updateEnterprise(@PathVariable("enterpriseId") String enterpriseId, @RequestBody Enterprise enterprise) {
        enterprise.setEnterpriseId(enterpriseId);
        return enterpriseService.updateEnterprise(enterprise);
    }

    @PutMapping("/update/{enterpriseId}")
    public Map<String, Object> updateEnterpriseCompat(@PathVariable("enterpriseId") String enterpriseId, @RequestBody Enterprise enterprise) {
        return updateEnterprise(enterpriseId, enterprise);
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