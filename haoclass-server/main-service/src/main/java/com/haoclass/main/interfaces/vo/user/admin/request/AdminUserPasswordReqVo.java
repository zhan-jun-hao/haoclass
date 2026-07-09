package com.haoclass.main.interfaces.vo.user.admin.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserPasswordReqVo {

    /**
     * 新密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;
}