package com.haoclass.main.application.converter.command;

import com.haoclass.main.infrastructure.persistence.po.User;
import com.haoclass.main.interfaces.vo.user.admin.request.AdminUserReqVo;
import com.haoclass.main.interfaces.vo.user.admin.request.AdminUserUpdateReqVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserCommandConverter {

    UserCommandConverter INSTANCE = Mappers.getMapper(UserCommandConverter.class);

    User reqVoToPo(AdminUserReqVo reqVo);

    User updateVoToPo(AdminUserUpdateReqVo reqVo);
}
