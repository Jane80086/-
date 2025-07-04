package com.cemenghui.course.service;

public interface UserService {
    boolean existsByUsername(String username);
    void deleteByUsername(String username);
    // ... 其他方法声明 ...
} 