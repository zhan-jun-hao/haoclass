package com.haoclass.main.application.service.inner;

import com.haoclass.main.interfaces.vo.auth.admin.request.AdminLoginReqVo;
import com.haoclass.main.interfaces.vo.auth.client.request.ClientLoginReqVo;
import com.haoclass.main.interfaces.vo.auth.admin.response.AdminLoginRespVo;
import com.haoclass.main.interfaces.vo.auth.client.response.ClientLoginRespVo;

public interface AuthApplicationService {

    AdminLoginRespVo adminLogin(AdminLoginReqVo reqVo);

    ClientLoginRespVo clientLogin(ClientLoginReqVo reqVo);
}
