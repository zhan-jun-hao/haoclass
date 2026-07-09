package com.haoclass.common.result;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BaseEntity {

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 创建时间
     */
    @TableField(value = "createTime", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "updateTime", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    @TableField(value = "createdUser", fill = FieldFill.INSERT)
    private Long createdUser;

    /**
     * 更新人
     */
    @TableField(value = "updatedUser", fill = FieldFill.INSERT_UPDATE)
    private Long updatedUser;

    /**
     * 逻辑删除
     * 0=未删除 1=已删除
     */
    @TableLogic(value = "0", delval = "1")
    private Integer deleted;
}
