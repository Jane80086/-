package com.cemenghui.common;

public class AdminUser extends User {
    private String role;
    private String department;

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
} 