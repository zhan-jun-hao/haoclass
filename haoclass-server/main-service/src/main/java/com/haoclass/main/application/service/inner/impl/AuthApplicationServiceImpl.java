package com.haoclass.main.application.service.inner.impl;

import com.haoclass.common.exception.BusinessException;
import com.haoclass.common.exception.ErrorCode;
import com.haoclass.main.application.service.inner.AuthApplicationService;
import com.haoclass.main.domain.service.UserService;
import com.haoclass.main.infrastructure.security.JwtTokenProvider;
import com.haoclass.main.infrastructure.security.LoginUser;
import com.haoclass.main.infrastructure.security.UserAuthStateService;
import com.haoclass.main.infrastructure.persistence.po.User;
import com.haoclass.main.interfaces.vo.auth.admin.request.AdminLoginReqVo;
import com.haoclass.main.interfaces.vo.auth.client.request.ClientLoginReqVo;
import com.haoclass.main.interfaces.vo.auth.admin.response.AdminLoginRespVo;
import com.haoclass.main.interfaces.vo.auth.client.response.ClientLoginRespVo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthApplicationServiceImpl implements AuthApplicationService {

    private static final int ADMIN_ROLE = 1;

    private static final String TOKEN_TYPE_BEARER = "Bearer";

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

    private final UserService userService;

    private final UserAuthStateService userAuthStateService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdminLoginRespVo adminLogin(AdminLoginReqVo reqVo) {
        LoginUser loginUser = refreshLoginUser(authenticate(reqVo.getPhone(), reqVo.getPassword()));
        if (!Objects.equals(loginUser.getRole(), ADMIN_ROLE)) {
            throw new BusinessException(ErrorCode.FORBIDDEN.getCode(), "账号无后台权限");
        }

        userService.updateLastLoginTime(loginUser.getId());
        Long authVersion = userAuthStateService.refreshAtLogin(loginUser);
        String token = jwtTokenProvider.generateToken(loginUser, authVersion);

        return buildAdminLoginRespVo(loginUser, token);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ClientLoginRespVo clientLogin(ClientLoginReqVo reqVo) {
        LoginUser loginUser = refreshLoginUser(authenticate(reqVo.getPhone(), reqVo.getPassword()));
        userService.updateLastLoginTime(loginUser.getId());
        Long authVersion = userAuthStateService.refreshAtLogin(loginUser);
        String token = jwtTokenProvider.generateToken(loginUser, authVersion);

        return buildClientLoginRespVo(loginUser, token);
    }

    private LoginUser authenticate(String phone, String password) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(phone, password));
        } catch (DisabledException e) {
            throw BusinessException.badRequest("账号已禁用");
        } catch (AuthenticationException e) {
            throw BusinessException.badRequest("手机号或密码错误");
        }

        return (LoginUser) authentication.getPrincipal();
    }

    private LoginUser refreshLoginUser(LoginUser authenticatedUser) {
        User user = userService.getUserById(authenticatedUser.getId());
        LoginUser latestLoginUser = new LoginUser(user);
        if (!latestLoginUser.isEnabled()) {
            throw BusinessException.badRequest("Account is disabled");
        }
        return latestLoginUser;
    }

    private AdminLoginRespVo buildAdminLoginRespVo(LoginUser loginUser, String token) {
        return new AdminLoginRespVo(
                TOKEN_TYPE_BEARER,
                token,
                jwtTokenProvider.getExpireSeconds(),
                loginUser.getId(),
                loginUser.getPhone(),
                loginUser.getNickname(),
                loginUser.getRole()
        );
    }

    private ClientLoginRespVo buildClientLoginRespVo(LoginUser loginUser, String token) {
        return new ClientLoginRespVo(
                TOKEN_TYPE_BEARER,
                token,
                jwtTokenProvider.getExpireSeconds(),
                loginUser.getId(),
                loginUser.getPhone(),
                loginUser.getNickname(),
                loginUser.getRole()
        );
    }
}
