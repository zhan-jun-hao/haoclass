package com.haoclass.main.interfaces.vo.auth.client.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientLoginRespVo {

    /**
     * Token类型
     */
    private String tokenType;

    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * 过期时间，单位：秒
     */
    private Long expiresIn;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户角色：0普通用户 1管理员
     */
    private Integer role;
}