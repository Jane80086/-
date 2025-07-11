package com.cemenghui.system.service;

import java.util.Map;
import com.cemenghui.system.entity.Enterprise;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface EnterpriseService {
    default Map<String, Object> getEnterpriseList(int page, int size, String enterpriseName, String creditCode, String status, String sortField) {
        return Map.of();
    }

    Map<String, Object> getEnterpriseList(int page, int size, String enterpriseName, String creditCode, String status, String sortField, String sortOrder);

    Map<String, Object> createEnterprise(Enterprise enterprise);
    Map<String, Object> deleteEnterprise(String enterpriseId);
    Map<String, Object> updateEnterprise(Enterprise enterprise);
    void exportEnterpriseList(String enterpriseName, String creditCode, String status, HttpServletResponse response) throws IOException;
} 
