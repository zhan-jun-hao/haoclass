package com.haoclass.main.application.converter.admin;

import com.haoclass.main.infrastructure.persistence.po.User;
import com.haoclass.main.interfaces.vo.user.admin.response.AdminUserBasicVo;
import com.haoclass.main.interfaces.vo.user.admin.response.AdminUserDetailVo;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-09T23:14:44+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.15 (Oracle Corporation)"
)
public class AdminUserConverterImpl implements AdminUserConverter {

    @Override
    public List<AdminUserBasicVo> poToBasicVo(List<User> users) {
        if ( users == null ) {
            return null;
        }

        List<AdminUserBasicVo> list = new ArrayList<AdminUserBasicVo>( users.size() );
        for ( User user : users ) {
            list.add( userToAdminUserBasicVo( user ) );
        }

        return list;
    }

    @Override
    public AdminUserDetailVo poToDetailVo(User user) {
        if ( user == null ) {
            return null;
        }

        AdminUserDetailVo adminUserDetailVo = new AdminUserDetailVo();

        adminUserDetailVo.setId( user.getId() );
        adminUserDetailVo.setPhone( user.getPhone() );
        adminUserDetailVo.setNickname( user.getNickname() );
        adminUserDetailVo.setAvatarUrl( user.getAvatarUrl() );
        adminUserDetailVo.setRole( user.getRole() );
        adminUserDetailVo.setStatus( user.getStatus() );
        adminUserDetailVo.setVipExpireTime( user.getVipExpireTime() );
        adminUserDetailVo.setLastLoginTime( user.getLastLoginTime() );

        return adminUserDetailVo;
    }

    protected AdminUserBasicVo userToAdminUserBasicVo(User user) {
        if ( user == null ) {
            return null;
        }

        AdminUserBasicVo adminUserBasicVo = new AdminUserBasicVo();

        adminUserBasicVo.setId( user.getId() );
        adminUserBasicVo.setPhone( user.getPhone() );
        adminUserBasicVo.setNickname( user.getNickname() );
        adminUserBasicVo.setAvatarUrl( user.getAvatarUrl() );
        adminUserBasicVo.setRole( user.getRole() );
        adminUserBasicVo.setStatus( user.getStatus() );
        adminUserBasicVo.setVipExpireTime( user.getVipExpireTime() );
        adminUserBasicVo.setLastLoginTime( user.getLastLoginTime() );

        return adminUserBasicVo;
    }
}
