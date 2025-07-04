package com.cemenghui.system.service;

import java.util.Map;

import com.cemenghui.common.Enterprise;
import com.cemenghui.common.Enterprise;

public interface EnterpriseService {
    Map<String, Object> getEnterpriseList(int page, int size, String enterpriseName, String creditCode, String status);
    Map<String, Object> createEnterprise(Enterprise enterprise);
    Map<String, Object> deleteEnterprise(Long enterpriseId);
    Map<String, Object> updateEnterprise(Enterprise enterprise);
} 