package com.haoclass.main.application.service.admin.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.haoclass.common.result.PageResult;
import com.haoclass.main.application.converter.admin.AdminUserConverter;
import com.haoclass.main.application.converter.command.UserCommandConverter;
import com.haoclass.main.application.service.admin.AdminUserApplicationService;
import com.haoclass.main.domain.service.UserService;
import com.haoclass.main.infrastructure.persistence.po.User;
import com.haoclass.main.infrastructure.security.UserAuthStateService;
import com.haoclass.main.interfaces.vo.user.admin.request.*;
import com.haoclass.main.interfaces.vo.user.admin.response.AdminUserBasicVo;
import com.haoclass.main.interfaces.vo.user.admin.response.AdminUserDetailVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUserApplicationServiceImpl implements AdminUserApplicationService {

    private final UserService userService;

    private final UserAuthStateService userAuthStateService;

    @Override
    public PageResult<AdminUserBasicVo> getUserPageList(AdminUserPageQueryReqVo query) {
        IPage<User> page = userService.pageQuery(query);
        List<AdminUserBasicVo> records = AdminUserConverter.INSTANCE.poToBasicVo(page.getRecords());
        return PageResult.success(page, records);
    }

    @Override
    public AdminUserDetailVo getUserDetail(Long id) {
        User user = userService.getUserById(id);
        return AdminUserConverter.INSTANCE.poToDetailVo(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addNewUser(AdminUserReqVo reqVo) {
        User user = UserCommandConverter.INSTANCE.reqVoToPo(reqVo);
        userService.saveUser(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyUser(Long id, AdminUserUpdateReqVo reqVo) {
        User user = UserCommandConverter.INSTANCE.updateVoToPo(reqVo);
        userService.updateUserById(id, user);
        User updatedUser = userService.getUserById(id);
        userAuthStateService.updateAndInvalidate(id, updatedUser.getStatus(), updatedUser.getRole());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeUser(Long id) {
        userService.deleteUserById(id);
        userAuthStateService.updateAndInvalidate(id, 0, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void restoreUser(Long id) {
        userService.restoreUserById(id);
        userAuthStateService.updateAndInvalidate(id, 1, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enableUser(Long id) {
        userService.updateStatusById(id, 1);
        userAuthStateService.updateAndInvalidate(id, 1, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disableUser(Long id) {
        userService.updateStatusById(id, 0);
        userAuthStateService.updateAndInvalidate(id, 0, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyUserPassword(Long id, AdminUserPasswordReqVo reqVo) {
        userService.updatePassword(id, reqVo.getPassword());
        // 更新jwt版本号
        userAuthStateService.invalidate(id);
    }
}
