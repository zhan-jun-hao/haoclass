package com.haoclass.main.interfaces.vo.user.admin.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserPageQueryReqVo {

    /**
     * 当前页码，默认从1开始
     */
    private Long current = 1L;

    /**
     * 每页条数
     */
    private Long size = 10L;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户状态：0禁用 1正常
     */
    private Integer status;

    /**
     * 用户角色：0普通用户 1管理员
     */
    private Integer role;
}