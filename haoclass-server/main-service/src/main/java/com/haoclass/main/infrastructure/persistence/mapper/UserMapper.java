package com.haoclass.main.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.haoclass.main.infrastructure.persistence.po.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    int restoreUserById(@Param("id") Long id,
                        @Param("updateTime") LocalDateTime updateTime,
                        @Param("updatedUser") Long updatedUser);

    User findUnExistUserByPhone(@Param("phone") String phone);
}
