package com.haoclass.main.application.converter.admin;

import com.haoclass.main.infrastructure.persistence.po.User;
import com.haoclass.main.interfaces.vo.user.admin.response.AdminUserBasicVo;
import com.haoclass.main.interfaces.vo.user.admin.response.AdminUserDetailVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface AdminUserConverter {

    AdminUserConverter INSTANCE = Mappers.getMapper(AdminUserConverter.class);

    List<AdminUserBasicVo> poToBasicVo(List<User> users);

    AdminUserDetailVo poToDetailVo(User user);
}
