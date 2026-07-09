package com.haoclass.main.interfaces.vo.user.admin.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserReqVo {

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    private String phone;

    /**
     * 用户昵称
     */
    @NotBlank(message = "用户昵称不能为空")
    private String nickname;

    /**
     * 用户头像URL
     */
    private String avatarUrl;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 用户角色：0普通用户 1管理员
     */
    @NotNull(message = "用户角色不能为空")
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