package com.haoclass.main.interfaces.controller.client;

import com.haoclass.common.result.Result;
import com.haoclass.main.application.service.inner.AuthApplicationService;
import com.haoclass.main.interfaces.vo.auth.client.request.ClientLoginReqVo;
import com.haoclass.main.interfaces.vo.auth.client.response.ClientLoginRespVo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/main/client/auth")
@RequiredArgsConstructor
public class ClientAuthController {

    private final AuthApplicationService authApplicationService;

    @PostMapping("/login")
    public Result<ClientLoginRespVo> login(@RequestBody @Valid ClientLoginReqVo reqVo) {
        log.info("用户登录, phone: {}", reqVo.getPhone());
        return Result.success(authApplicationService.clientLogin(reqVo));
    }
}
