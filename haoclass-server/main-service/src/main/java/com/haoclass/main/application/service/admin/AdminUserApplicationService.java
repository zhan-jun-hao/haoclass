package com.haoclass.main.application.service.admin;

import com.haoclass.common.result.PageResult;
import com.haoclass.main.interfaces.vo.user.admin.request.*;
import com.haoclass.main.interfaces.vo.user.admin.response.AdminUserBasicVo;
import com.haoclass.main.interfaces.vo.user.admin.response.AdminUserDetailVo;

public interface AdminUserApplicationService {

    /**
     * 分页查询用户
     */
    PageResult<AdminUserBasicVo> getUserPageList(AdminUserPageQueryReqVo queryReqVo);

    /**
     * 查询用户详情
     * @param id
     * @return
     */
    AdminUserDetailVo getUserDetail(Long id);

    /**
     * 添加新用户
     * @param reqVo
     */
    void addNewUser(AdminUserReqVo reqVo);

    /**
     * 修改用户
     * @param id
     * @param reqVo
     */
    void modifyUser(Long id, AdminUserUpdateReqVo reqVo);

    /**
     * 删除用户
     * @param id
     */
    void removeUser(Long id);

    /**
     * 恢复用户
     * @param id
     */
    void restoreUser(Long id);

    /**
     * 启用用户
     * @param id
     */
    void enableUser(Long id);

    /**
     * 禁用用户
     * @param id
     */
    void disableUser(Long id);

    /**
     * 修改用户密码
     * @param id
     * @param reqVo
     */
    void modifyUserPassword(Long id, AdminUserPasswordReqVo reqVo);
}
