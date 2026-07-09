package com.haoclass.main.interfaces.vo.user.admin.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserUpdateReqVo {

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户头像URL
     */
    private String avatarUrl;

    /**
     * 用户角色：0普通用户 1管理员
     */
    private Integer role;

    /**
     * 用户状态：0禁用 1正常
     */
    private Integer status;

    /**
     * VIP到期时间
     */
    private LocalDateTime vipExpireTime;
}