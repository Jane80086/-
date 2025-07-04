package com.cemenghui.system.controller;

import com.cemenghui.system.entity.Enterprise;
import com.cemenghui.system.service.EnterpriseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/enterprise")
public class EnterpriseController {

    @Autowired
    private EnterpriseService enterpriseService;

    @GetMapping("/list")
    public Map<String, Object> getEnterpriseList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String enterpriseName,
            @RequestParam(required = false) String creditCode,
            @RequestParam(required = false) String status
    ) {
        // 返回分页数据结构，实际应根据你的 service 层实现
        return enterpriseService.getEnterpriseList(page, size, enterpriseName, creditCode, status);
    }

    @PostMapping("/create")
    public Map<String, Object> createEnterprise(@RequestBody Enterprise enterprise) {
        // 自动生成企业ID，避免非空约束违反
        if (enterprise.getEnterpriseId() == null) {
            enterprise.setEnterpriseId(String.valueOf(System.currentTimeMillis())); // 使用时间戳作为临时ID
        }
        // 设置创建时间
        if (enterprise.getCreateTime() == null) {
            enterprise.setCreateTime(java.time.LocalDateTime.now().toString());
        }
        return enterpriseService.createEnterprise(enterprise);
    }

    @DeleteMapping("/{enterpriseId}")
    public Map<String, Object> deleteEnterprise(@PathVariable("enterpriseId") Long enterpriseId) {
        return enterpriseService.deleteEnterprise(enterpriseId);
    }

    @PutMapping("/{enterpriseId}")
    public Map<String, Object> updateEnterprise(@PathVariable("enterpriseId") Long enterpriseId, @RequestBody Enterprise enterprise) {
        enterprise.setEnterpriseId(String.valueOf(enterpriseId));
        return enterpriseService.updateEnterprise(enterprise);
    }
} 