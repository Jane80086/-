package com.cemenghui.system.entity;

import lombok.Data;

public class ThirdPartyAccount {

    // 第三方平台标识
    private String thirdPartyId;

    // 开放平台标识（如微信 openid 等 ）
    private String openId;

    // 所属平台（如 "wechat"、"qq" 等 ）
    private String platform;

    // 绑定方法
    public void bind() {
        // 编写绑定第三方账号的逻辑，比如关联系统用户等
        System.out.println("第三方账号已绑定");
    }

    // 根据第三方信息登录方法
    public void loginByThirdParty() {
        // 编写通过第三方账号登录的逻辑，比如校验第三方凭证等
        System.out.println("通过第三方账号登录中");
    }

    private String account;

    public String getThirdPartyId() {
        return thirdPartyId;
    }

    public void setThirdPartyId(String thirdPartyId) {
        this.thirdPartyId = thirdPartyId;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}
