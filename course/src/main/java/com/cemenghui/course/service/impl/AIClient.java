package com.cemenghui.course.service.impl;

/**
 * AI服务客户端
 */
public class AIClient {
    public AIClient() {
        // 初始化AI客户端
    }

    // 示例方法：调用AI优化课程信息
    public String optimizeCourseTitle(String title) {
        // 实际应调用AI接口
        return "优化后的" + title;
    }

    // 示例方法：获取热门关键词
    public String[] getHotKeywords() {
        // 实际应调用AI接口
        return new String[]{"Java", "Spring", "AI"};
    }

    // 示例方法：AI自动问答
    public String answerQuestion(String question) {
        // 实际应调用AI接口
        return "AI回答: " + question;
    }

    // 示例方法：课程评分
    public double scoreCourse(Long courseId) {
        // 实际应调用AI接口
        return 4.5;
    }
} 