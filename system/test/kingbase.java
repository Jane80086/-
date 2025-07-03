package com.system.test;

import java.sql.Connection;
import java.sql.DriverManager;

public class kingbase {
    public static void main(String[] args) {
        String url = "jdbc:kingbase8://localhost:54321/cemenghui";
        String user = "system";
        String password = "123456";
        try {
            Class.forName("com.kingbase8.Driver");
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("连接成功！");
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("连接失败！");
        }
    }
}