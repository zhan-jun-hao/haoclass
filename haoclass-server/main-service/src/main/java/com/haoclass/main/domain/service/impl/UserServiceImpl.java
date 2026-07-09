package com.haoclass.main.domain.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoclass.common.exception.BusinessException;
import com.haoclass.main.domain.service.UserService;
import com.haoclass.main.infrastructure.persistence.mapper.UserMapper;
import com.haoclass.main.infrastructure.persistence.po.User;
import com.haoclass.main.infrastructure.security.SecurityUserHolder;
import com.haoclass.main.interfaces.vo.user.admin.request.AdminUserPageQueryReqVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public IPage<User> pageQuery(AdminUserPageQueryReqVo pageQuery) {
        if(Objects.isNull(pageQuery)) {
            pageQuery = new AdminUserPageQueryReqVo();
        }

        if(pageQuery.getCurrent() == null || pageQuery.getCurrent() < 1) {
            pageQuery.setCurrent(1L);
        }

        if(pageQuery.getSize() == null || pageQuery.getSize() < 1) {
            pageQuery.setSize(10L);
        }

        if(pageQuery.getSize() > 100) {
            pageQuery.setSize(100L);
        }

        LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery(User.class)
                .eq(User::getDeleted, 0)
                .like(StringUtils.hasText(pageQuery.getPhone()), User::getPhone, pageQuery.getPhone())
                .like(StringUtils.hasText(pageQuery.getNickname()), User::getNickname, pageQuery.getNickname())
                .eq(Objects.nonNull(pageQuery.getRole()), User::getRole, pageQuery.getRole())
                .eq(Objects.nonNull(pageQuery.getStatus()), User::getStatus, pageQuery.getStatus())
                .orderByDesc(User::getCreateTime);

        return this.page(new Page<>(pageQuery.getCurrent(), pageQuery.getSize()), wrapper);
    }

    @Override
    public User getUserById(Long id) {
        User user = this.getById(id);
        if(Objects.isNull(user)) {
            throw BusinessException.notFound("用户不存在");
        }
        return user;
    }

    @Override
    public User findExistUserByPhone(String phone) {
        if(!StringUtils.hasText(phone)) {
            return null;
        }

        return this.getOne(Wrappers.lambdaQuery(User.class)
                .eq(User::getDeleted, 0)
                .eq(User::getPhone, phone), false);
    }

    @Override
    public User findUnExistUserByPhone(String phone) {
        if(!StringUtils.hasText(phone)) {
            return null;
        }
        return userMapper.findUnExistUserByPhone(phone);
    }

    @Override
    public void saveUser(User user) {
        String phone = user.getPhone();
        if(Objects.nonNull(findExistUserByPhone(phone))) {
            throw BusinessException.badRequest("已存在该手机号的用户且没逻辑删除");
        }

        if(Objects.nonNull(findUnExistUserByPhone(phone))) {
            throw BusinessException.badRequest("该用户已被删除，请先恢复");
        }

        if(Objects.isNull(user.getStatus())) {
            user.setStatus(1);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setId(IdUtil.getSnowflakeNextId());
        user.setCreateTime(LocalDateTime.now());
        user.setCreatedUser(SecurityUserHolder.getUserId());
        user.setUpdateTime(LocalDateTime.now());
        user.setUpdatedUser(SecurityUserHolder.getUserId());

        this.save(user);
    }

    @Override
    public void updateUserById(Long id, User user) {
        User originUser = this.getById(id);
        if(Objects.isNull(originUser)) {
            throw BusinessException.notFound("用户不存在");
        }
        user.setId(id);
        user.setUpdateTime(LocalDateTime.now());
        user.setUpdatedUser(SecurityUserHolder.getUserId());

        this.updateById(user);
    }

    @Override
    public void deleteUserById(Long id) {
        User user = this.getById(id);
        if(Objects.isNull(user)) {
            throw BusinessException.notFound("用户不存在");
        }
        user.setUpdateTime(LocalDateTime.now());
        user.setUpdatedUser(SecurityUserHolder.getUserId());

        this.removeById(user);
    }

    @Override
    public void restoreUserById(Long id) {
        int row = userMapper.restoreUserById(id, LocalDateTime.now(), SecurityUserHolder.getUserId());
        log.info("更新了: {} 行", row);
        if(row == 0) {
            throw BusinessException.badRequest("用户不存在或无需恢复");
        }
    }

    @Override
    public void updateStatusById(Long id, Integer status) {
        if(!Objects.equals(status, 0) && !Objects.equals(status, 1)) {
            throw BusinessException.badRequest("用户状态只能为禁用或启用");
        }

        User user = this.getById(id);
        if(Objects.isNull(user)) {
            throw BusinessException.notFound("用户不存在");
        }

        user.setStatus(status);
        user.setUpdateTime(LocalDateTime.now());
        user.setUpdatedUser(SecurityUserHolder.getUserId());

        this.updateById(user);
    }

    @Override
    public void updatePassword(Long id, String password) {
        User user = this.getById(id);
        if(Objects.isNull(user)) {
            throw BusinessException.notFound("用户不存在");
        }

        user.setPassword(passwordEncoder.encode(password));
        user.setUpdateTime(LocalDateTime.now());
        user.setUpdatedUser(SecurityUserHolder.getUserId());

        this.updateById(user);
    }

    @Override
    public void updateLastLoginTime(Long id) {
        LambdaUpdateWrapper<User> wrapper = Wrappers.lambdaUpdate(User.class)
                .eq(User::getId, id)
                .eq(User::getDeleted, 0)
                .set(User::getLastLoginTime, LocalDateTime.now())
                .set(User::getUpdateTime, LocalDateTime.now());

        this.update(wrapper);
    }
}
