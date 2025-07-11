package com.cemenghui.news.service;

/**
 * 内容润色服务接口
 */
public interface ContentRefinementService {

    /**
     * 调用 AI 模型对文本内容进行润色。
     * @param originalContent 待润色的原始文本
     * @return 润色后的文本，如果失败则返回 null
     */
    String refineContent(String originalContent);
}