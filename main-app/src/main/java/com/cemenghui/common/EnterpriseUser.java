package com.cemenghui.common;

public class EnterpriseUser extends User {
    private String companyName;
    private String companyCode;
 
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getCompanyCode() { return companyCode; }
    public void setCompanyCode(String companyCode) { this.companyCode = companyCode; }
} 