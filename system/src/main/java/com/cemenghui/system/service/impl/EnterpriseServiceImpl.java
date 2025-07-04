package com.cemenghui.system.service.impl;

import com.cemenghui.system.service.EnterpriseService;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import com.cemenghui.system.entity.Enterprise;
import com.cemenghui.system.repository.EnterpriseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Service
public class EnterpriseServiceImpl implements EnterpriseService {
    @Autowired
    private EnterpriseMapper enterpriseMapper;

    @Override
    public Map<String, Object> getEnterpriseList(int page, int size, String enterpriseName, String creditCode, String status) {
        int offset = (page - 1) * size;
        List<Enterprise> records = enterpriseMapper.selectEnterpriseList(offset, size);
        int total = enterpriseMapper.countEnterprises();
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("records", records);
        return result;
    }

    @Override
    public Map<String, Object> createEnterprise(Enterprise enterprise) {
        int result = enterpriseMapper.insertEnterprise(enterprise);
        System.out.println("插入企业返回行数: " + result + ", 企业实体: " + enterprise);
        Map<String, Object> map = new HashMap<>();
        if (result > 0) {
            map.put("success", true);
            map.put("message", "企业创建成功");
        } else {
            map.put("success", false);
            map.put("message", "企业创建失败");
        }
        return map;
    }

    @Override
    public Map<String, Object> deleteEnterprise(Long enterpriseId) {
        Map<String, Object> result = new HashMap<>();
        int rows = enterpriseMapper.deleteByEnterpriseId(enterpriseId);
        if (rows > 0) {
            result.put("success", true);
            result.put("message", "删除成功");
        } else {
            result.put("success", false);
            result.put("message", "删除失败，未找到该企业");
        }
        return result;
    }

    @Override
    public Map<String, Object> updateEnterprise(Enterprise enterprise) {
        Map<String, Object> result = new HashMap<>();
        int rows = enterpriseMapper.updateEnterprise(enterprise);
        if (rows > 0) {
            result.put("success", true);
            result.put("message", "更新成功");
        } else {
            result.put("success", false);
            result.put("message", "更新失败，未找到该企业");
        }
        return result;
    }
} 