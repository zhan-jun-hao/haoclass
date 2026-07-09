package com.haoclass.main.interfaces.controller.admin;

import com.haoclass.common.result.Result;
import com.haoclass.main.application.service.inner.AuthApplicationService;
import com.haoclass.main.interfaces.vo.auth.admin.request.AdminLoginReqVo;
import com.haoclass.main.interfaces.vo.auth.admin.response.AdminLoginRespVo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/main/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AuthApplicationService authApplicationService;

    @PostMapping("/login")
    public Result<AdminLoginRespVo> login(@RequestBody @Valid AdminLoginReqVo reqVo) {
        log.info("后台登录, phone: {}", reqVo.getPhone());
        return Result.success(authApplicationService.adminLogin(reqVo));
    }
}
