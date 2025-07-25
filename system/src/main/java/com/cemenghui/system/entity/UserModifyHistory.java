package com.cemenghui.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户修改历史实体类，对应 system_user_modify_history 表
 */
@Data
@TableName("system_user_modify_history")
public class UserModifyHistory {
    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField("user_id")
    private Long userId;
    @TableField("modify_type")
    private String modifyType;
    @TableField("field_name")
    private String fieldName;
    @TableField("old_value")
    private String oldValue;
    @TableField("new_value")
    private String newValue;
    @TableField("modify_time")
    private LocalDateTime modifyTime;
    @TableField("operator_id")
    private Long operatorId;
    
    // 添加兼容方法
    public String getHistoryId() {
        return this.id != null ? this.id.toString() : null;
    }
    
    public void setHistoryId(String historyId) {
        if (historyId == null) {
            this.id = null;
            return;
        }
        
        try {
            // 尝试直接解析为Long
            this.id = Long.valueOf(historyId);
        } catch (NumberFormatException e) {
            // 如果是UUID字符串，生成一个Long ID
            this.id = Long.valueOf(Math.abs(historyId.hashCode()));
        }
    }
}
