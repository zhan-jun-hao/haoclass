package com.haoclass.main.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.haoclass.common.result.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 用户持久化对象。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user")
public class User extends BaseEntity {

    /**
     * 手机号
     */
    private String phone;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像url
     */
    @TableField("avatarUrl")
    private String avatarUrl;

    /**
     * 密文密码
     */
    private String password;

    /**
     * 角色：0普通用户 1管理用户
     */
    private Integer role;

    /**
     * 状态： 0禁用 1正常
     */
    private Integer status;

    /**
     * VIP到期时间
     */
    @TableField("vipExpireTime")
    private LocalDateTime vipExpireTime;

    /**
     * 最后登录时间
     */
    @TableField("lastLoginTime")
    private LocalDateTime lastLoginTime;

}
