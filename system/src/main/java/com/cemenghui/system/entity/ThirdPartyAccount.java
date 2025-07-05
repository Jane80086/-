package com.cemenghui.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("system_third_party_account")
public class ThirdPartyAccount {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("third_party_id")
    private String thirdPartyId;

    @TableField("open_id")
    private String openId;

    @TableField("platform")
    private String platform;

    @TableField("account")
    private String account;

    @TableField("user_id")
    private Long userId; // 关联main-app users表

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

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
