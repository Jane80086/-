package com.cemenghui.system.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("正在检查数据库连接...");
        
        try {
            // 测试数据库连接
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            System.out.println("数据库连接成功！");
            
            // 检查表是否存在
            boolean usersTableExists = checkTableExists("users");
            if (!usersTableExists) {
                System.out.println("检测到数据库表不存在，正在初始化数据库...");
                initializeDatabase();
            } else {
                System.out.println("数据库表已存在，检查表结构...");
                checkAndFixTableStructure();
            }
            
        } catch (Exception e) {
            System.err.println("数据库连接失败: " + e.getMessage());
            System.err.println("请确保：");
            System.err.println("1. KingBase8 数据库服务正在运行");
            System.err.println("2. 数据库 'course_manager' 已创建");
            System.err.println("3. 用户名和密码正确");
            System.err.println("4. 端口 54321 可访问");
            
            // 尝试创建数据库
            tryCreateDatabase();
        }
    }
    
    private boolean checkTableExists(String tableName) {
        try {
            jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = ?", 
                Integer.class, 
                tableName
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    private void checkAndFixTableStructure() {
        try {
            // 检查enterprise表是否存在credit_code字段
            boolean creditCodeExists = checkColumnExists("enterprise", "credit_code");
            if (!creditCodeExists) {
                System.out.println("检测到enterprise表缺少credit_code字段，正在修复...");
                fixEnterpriseTable();
            } else {
                System.out.println("enterprise表结构正常");
            }
        } catch (Exception e) {
            System.err.println("检查表结构失败: " + e.getMessage());
        }
    }
    
    private boolean checkColumnExists(String tableName, String columnName) {
        try {
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.columns WHERE table_name = ? AND column_name = ?", 
                Integer.class, 
                tableName, columnName
            );
            return count != null && count > 0;
        } catch (Exception e) {
            System.err.println("检查字段存在性失败: " + e.getMessage());
            return false;
        }
    }
    
    private void fixEnterpriseTable() {
        try {
            System.out.println("开始修复enterprise表结构...");
            
            // 添加缺失的字段
            jdbcTemplate.execute("ALTER TABLE enterprise ADD COLUMN IF NOT EXISTS credit_code VARCHAR(30)");
            System.out.println("已添加 credit_code 字段");
            
            jdbcTemplate.execute("ALTER TABLE enterprise ADD COLUMN IF NOT EXISTS register_address VARCHAR(200)");
            System.out.println("已添加 register_address 字段");
            
            jdbcTemplate.execute("ALTER TABLE enterprise ADD COLUMN IF NOT EXISTS legal_representative VARCHAR(50)");
            System.out.println("已添加 legal_representative 字段");
            
            jdbcTemplate.execute("ALTER TABLE enterprise ADD COLUMN IF NOT EXISTS registration_date DATE");
            System.out.println("已添加 registration_date 字段");
            
            jdbcTemplate.execute("ALTER TABLE enterprise ADD COLUMN IF NOT EXISTS enterprise_type VARCHAR(50)");
            System.out.println("已添加 enterprise_type 字段");
            
            jdbcTemplate.execute("ALTER TABLE enterprise ADD COLUMN IF NOT EXISTS registered_capital VARCHAR(50)");
            System.out.println("已添加 registered_capital 字段");
            
            jdbcTemplate.execute("ALTER TABLE enterprise ADD COLUMN IF NOT EXISTS business_scope TEXT");
            System.out.println("已添加 business_scope 字段");
            
            jdbcTemplate.execute("ALTER TABLE enterprise ADD COLUMN IF NOT EXISTS establishment_date DATE");
            System.out.println("已添加 establishment_date 字段");
            
            jdbcTemplate.execute("ALTER TABLE enterprise ADD COLUMN IF NOT EXISTS business_term VARCHAR(100)");
            System.out.println("已添加 business_term 字段");
            
            jdbcTemplate.execute("ALTER TABLE enterprise ADD COLUMN IF NOT EXISTS registration_authority VARCHAR(100)");
            System.out.println("已添加 registration_authority 字段");
            
            jdbcTemplate.execute("ALTER TABLE enterprise ADD COLUMN IF NOT EXISTS approval_date DATE");
            System.out.println("已添加 approval_date 字段");
            
            jdbcTemplate.execute("ALTER TABLE enterprise ADD COLUMN IF NOT EXISTS enterprise_status VARCHAR(30)");
            System.out.println("已添加 enterprise_status 字段");
            
            jdbcTemplate.execute("ALTER TABLE enterprise ADD COLUMN IF NOT EXISTS create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP");
            System.out.println("已添加 create_time 字段");
            
            jdbcTemplate.execute("ALTER TABLE enterprise ADD COLUMN IF NOT EXISTS update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP");
            System.out.println("已添加 update_time 字段");
            
            // 尝试添加唯一约束（如果不存在）
            try {
                jdbcTemplate.execute("ALTER TABLE enterprise ADD CONSTRAINT enterprise_social_credit_code_key UNIQUE (credit_code)");
                System.out.println("已添加 credit_code 唯一约束");
            } catch (Exception e) {
                System.out.println("credit_code 唯一约束可能已存在: " + e.getMessage());
            }
            
            System.out.println("enterprise表结构修复完成");
        } catch (Exception e) {
            System.err.println("修复enterprise表结构失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void initializeDatabase() {
        try {
            System.out.println("正在创建数据库表...");
            
            // 创建基本的表结构
            createBasicTables();
            
            System.out.println("数据库初始化完成！");
        } catch (Exception e) {
            System.err.println("数据库初始化失败: " + e.getMessage());
        }
    }
    
    private void createBasicTables() {
        // 创建用户表
        jdbcTemplate.execute(
            "CREATE TABLE IF NOT EXISTS users (" +
            "id SERIAL PRIMARY KEY," +
            "username VARCHAR(100) NOT NULL UNIQUE," +
            "password VARCHAR(255) NOT NULL," +
            "real_name VARCHAR(100)," +
            "email VARCHAR(255)," +
            "phone VARCHAR(20)," +
            "user_type VARCHAR(20) NOT NULL," +
            "status INTEGER DEFAULT 1," +
            "department VARCHAR(100)," +
            "nickname VARCHAR(100)," +
            "avatar VARCHAR(500)," +
            "is_remembered BOOLEAN DEFAULT FALSE," +
            "enterprise_id VARCHAR(64)," +
            "dynamic_code VARCHAR(20)," +
            "role VARCHAR(50)," +
            "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "deleted INTEGER DEFAULT 0" +
            ")"
        );
        // 创建企业表
        jdbcTemplate.execute(
            "CREATE TABLE IF NOT EXISTS enterprise (" +
            "enterprise_id VARCHAR(30) PRIMARY KEY," +
            "enterprise_name VARCHAR(100) NOT NULL," +
            "credit_code VARCHAR(30) UNIQUE," +
            "register_address VARCHAR(200)," +
            "legal_representative VARCHAR(50)," +
            "registration_date DATE," +
            "enterprise_type VARCHAR(50)," +
            "registered_capital VARCHAR(50)," +
            "business_scope TEXT," +
            "establishment_date DATE," +
            "business_term VARCHAR(100)," +
            "registration_authority VARCHAR(100)," +
            "approval_date DATE," +
            "enterprise_status VARCHAR(30)," +
            "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ")"
        );
        // 创建用户修改历史表
        jdbcTemplate.execute(
            "CREATE TABLE IF NOT EXISTS system_user_modify_history (" +
            "id SERIAL PRIMARY KEY," +
            "user_id BIGINT," +
            "modify_type VARCHAR(50)," +
            "field_name VARCHAR(100)," +
            "old_value TEXT," +
            "new_value TEXT," +
            "modify_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "operator_id BIGINT" +
            ")"
        );
        // 创建系统日志表
        jdbcTemplate.execute(
            "CREATE TABLE IF NOT EXISTS system_log (" +
            "id SERIAL PRIMARY KEY," +
            "user_id BIGINT," +
            "operation_type VARCHAR(50)," +
            "operation_desc VARCHAR(500)," +
            "ip_address VARCHAR(50)," +
            "operation_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ")"
        );
        // 插入默认管理员用户
        try {
            jdbcTemplate.execute(
                "INSERT INTO users (username, password, real_name, user_type, status, role) " +
                "VALUES ('0000admin', 'admin123', '系统管理员', 'ADMIN', 1, 'SUPER_ADMIN') " +
                "ON CONFLICT (username) DO NOTHING"
            );
        } catch (Exception e) {
            System.out.println("默认管理员用户可能已存在");
        }
        System.out.println("基本表结构创建完成");
    }
    
    private void tryCreateDatabase() {
        try {
            System.out.println("尝试创建数据库...");
            
            // 连接到默认数据库
            String url = "jdbc:kingbase8://localhost:54321/SYSTEM";
            String username = "SYSTEM";
            String password = "123456";
            
            try (Connection conn = DriverManager.getConnection(url, username, password)) {
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("CREATE DATABASE course_manager");
                    System.out.println("数据库 'course_manager' 创建成功！");
                    System.out.println("请重启应用程序以完成初始化。");
                }
            }
        } catch (Exception e) {
            System.err.println("创建数据库失败: " + e.getMessage());
            System.err.println("请手动创建数据库 'course_manager'");
        }
    }
} 