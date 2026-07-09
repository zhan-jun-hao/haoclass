package com.haoclass.main.interfaces.vo.auth.client.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ClientLoginReqVo {

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    private String phone;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
}