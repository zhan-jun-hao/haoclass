package com.haoclass.main.application.converter.command;

import com.haoclass.main.infrastructure.persistence.po.User;
import com.haoclass.main.interfaces.vo.user.admin.request.AdminUserReqVo;
import com.haoclass.main.interfaces.vo.user.admin.request.AdminUserUpdateReqVo;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-09T23:14:45+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.15 (Oracle Corporation)"
)
public class UserCommandConverterImpl implements UserCommandConverter {

    @Override
    public User reqVoToPo(AdminUserReqVo reqVo) {
        if ( reqVo == null ) {
            return null;
        }

        User user = new User();

        user.setPhone( reqVo.getPhone() );
        user.setNickname( reqVo.getNickname() );
        user.setAvatarUrl( reqVo.getAvatarUrl() );
        user.setPassword( reqVo.getPassword() );
        user.setRole( reqVo.getRole() );
        user.setStatus( reqVo.getStatus() );
        user.setVipExpireTime( reqVo.getVipExpireTime() );

        return user;
    }

    @Override
    public User updateVoToPo(AdminUserUpdateReqVo reqVo) {
        if ( reqVo == null ) {
            return null;
        }

        User user = new User();

        user.setNickname( reqVo.getNickname() );
        user.setAvatarUrl( reqVo.getAvatarUrl() );
        user.setRole( reqVo.getRole() );
        user.setStatus( reqVo.getStatus() );
        user.setVipExpireTime( reqVo.getVipExpireTime() );

        return user;
    }
}
