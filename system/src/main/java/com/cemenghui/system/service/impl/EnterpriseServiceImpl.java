package com.cemenghui.system.service.impl;

import com.cemenghui.system.service.EnterpriseService;
import com.cemenghui.system.util.ExcelUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import com.cemenghui.system.entity.Enterprise;
import com.cemenghui.system.repository.EnterpriseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;

@Service
public class EnterpriseServiceImpl implements EnterpriseService {
    @Autowired
    private EnterpriseMapper enterpriseMapper;

    @Override
    public Map<String, Object> getEnterpriseList(int page, int size, String enterpriseName, String creditCode, String status) {
        int offset = (page - 1) * size;
        List<Enterprise> records = enterpriseMapper.selectEnterpriseListPaged(enterpriseName, creditCode, status, offset, size);
        int total = enterpriseMapper.countEnterpriseListPaged(enterpriseName, creditCode, status);
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
    public Map<String, Object> deleteEnterprise(String enterpriseId) {
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

    @Override
    public void exportEnterpriseList(String enterpriseName, String creditCode, String status, HttpServletResponse response) throws IOException {
        List<Enterprise> enterpriseList = enterpriseMapper.selectEnterpriseListForExport(enterpriseName, creditCode, status);
        if (CollectionUtils.isEmpty(enterpriseList)) {
            return;
        }

        // 定义Excel表头
        List<String> headers = Arrays.asList(
                "企业ID", "企业名称", "统一社会信用代码", "法定代表人", "注册资本",
                "成立日期", "企业类型", "注册地址", "经营范围", "企业状态",
                "注册机关", "注册日期", "批准日期", "营业期限", "创建时间", "更新时间"
        );

        // 转换数据格式
        List<List<String>> data = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (Enterprise enterprise : enterpriseList) {
            List<String> row = Arrays.asList(
                    enterprise.getEnterpriseId() != null ? enterprise.getEnterpriseId() : "",
                    enterprise.getEnterpriseName(),
                    enterprise.getCreditCode(),
                    enterprise.getLegalRepresentative(),
                    enterprise.getRegisteredCapital(),
                    enterprise.getEstablishmentDate() != null ? enterprise.getEstablishmentDate().toString() : "",
                    enterprise.getEnterpriseType(),
                    enterprise.getRegisterAddress(),
                    enterprise.getBusinessScope(),
                    enterprise.getEnterpriseStatus(),
                    enterprise.getRegistrationAuthority(),
                    enterprise.getRegistrationDate() != null ? enterprise.getRegistrationDate().toString() : "",
                    enterprise.getApprovalDate() != null ? enterprise.getApprovalDate().toString() : "",
                    enterprise.getBusinessTerm(),
                    enterprise.getCreateTime() != null ? enterprise.getCreateTime().toString() : "",
                    enterprise.getUpdateTime() != null ? enterprise.getUpdateTime().toString() : ""
            );
            data.add(row);
        }

        // 导出Excel
        new ExcelUtil().exportExcel(headers, data, "企业列表", response);
    }
} 