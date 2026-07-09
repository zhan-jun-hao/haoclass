package com.haoclass.main.domain.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haoclass.main.infrastructure.persistence.po.User;
import com.haoclass.main.interfaces.vo.user.admin.request.AdminUserPageQueryReqVo;

/**
 * 用户管理接口
 */
public interface UserService extends IService<User> {

    /**
     * 分页查询用户
     * @param pageQuery
     * @return
     */
    IPage<User> pageQuery(AdminUserPageQueryReqVo pageQuery);

    /**
     * 通过Id查询正常用户
     * @param id
     * @return
     */
    User getUserById(Long id);

    /**
     * 通过电话查询未删除用户
     * @param phone
     * @return
     */
    User findExistUserByPhone(String phone);

    /**
     * 通过电话查询所有用户记录
     * @param phone
     * @return
     */
    User findUnExistUserByPhone(String phone);

    /**
     * 新增用户
     */
    void saveUser(User user);

    /**
     * 修改用户
     */
    void updateUserById(Long id, User user);

    /**
     * 删除用户
     */
    void deleteUserById(Long id);

    /**
     * 恢复用户
     * @param id
     */
    void restoreUserById(Long id);

    /**
     * 修改用户状态
     * @param id
     * @param status
     */
    void updateStatusById(Long id, Integer status);

    /**
     * 修改用户密码
     * @param id
     * @param password
     */
    void updatePassword(Long id, String password);

    /**
     * 更新最后登录时间
     * @param id
     */
    void updateLastLoginTime(Long id);
}
