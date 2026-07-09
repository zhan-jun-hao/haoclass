package com.haoclass.main.interfaces.controller.admin;


import com.alibaba.fastjson.JSONObject;
import com.haoclass.common.result.PageResult;
import com.haoclass.common.result.Result;
import com.haoclass.main.application.service.admin.AdminUserApplicationService;
import com.haoclass.main.interfaces.vo.user.admin.request.*;
import com.haoclass.main.interfaces.vo.user.admin.response.AdminUserBasicVo;
import com.haoclass.main.interfaces.vo.user.admin.response.AdminUserDetailVo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 后台管理领域-admin端-用户管理
 *
 * @author zhanjunhao
 * @since 2026/5/27
 */
@Slf4j
@RequestMapping("/api/main/admin/users")
@RestController
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserApplicationService adminUserApplicationService;

    /**
     * 分页查询User基础信息
     * @param query
     * @return
     */
    @GetMapping
    public Result<PageResult<AdminUserBasicVo>> getCoursePageList(AdminUserPageQueryReqVo query) {
        log.info("分页查询, UserController -> /api/main/admin/users, query: {}", JSONObject.toJSONString(query));
        return Result.success(adminUserApplicationService.getUserPageList(query));
    }

    @GetMapping("{id}")
    public Result<AdminUserDetailVo> getUserDetail(@PathVariable("id") Long id) {
        log.info("查询用户详情, UserController -> /api/main/admin/users/{}", id);
        return Result.success(adminUserApplicationService.getUserDetail(id));
    }

    /**
     * 新增User
     * @param reqVo
     * @return
     */
    @PostMapping
    public Result<Void> addUser(@RequestBody @Valid AdminUserReqVo reqVo) {
        adminUserApplicationService.addNewUser(reqVo);
        return Result.success();
    }

    /**
     * 修改User
     * @param id
     * @param reqVo
     * @return
     */
    @PutMapping("{id}")
    public Result<Void> updateUser(@PathVariable("id") Long id, @RequestBody @Valid AdminUserUpdateReqVo reqVo) {
        log.info("修改用户, UserController -> /api/main/admin/users/{}, reqVo: {}", id, JSONObject.toJSONString(reqVo));
        adminUserApplicationService.modifyUser(id, reqVo);
        return Result.success();
    }

    /**
     * 修改User密码
     * @param id
     * @param reqVo
     * @return
     */
    @PutMapping("{id}/password")
    public Result<Void> updateUserPassword(@PathVariable("id") Long id, @RequestBody @Valid AdminUserPasswordReqVo reqVo) {
        adminUserApplicationService.modifyUserPassword(id, reqVo);
        return Result.success();
    }

    /**
     * 逻辑删除User
     * @param id
     * @return
     */
    @DeleteMapping("{id}")
    public Result<Void> deleteUser(@PathVariable("id") Long id) {
        log.info("删除用户, UserController -> /api/main/admin/users/{}", id);
        adminUserApplicationService.removeUser(id);
        return Result.success();
    }

    /**
     * 恢复User
     * @param id
     * @return
     */
    @PutMapping("{id}/restore")
    public Result<Void> restoreUser(@PathVariable("id") Long id) {
        log.info("恢复用户, UserController -> /api/main/admin/users/{}", id);
        adminUserApplicationService.restoreUser(id);
        return Result.success();
    }

    /**
     * 开启用户
     * @param id
     * @return
     */
    @PutMapping("{id}/enable")
    public Result<Void> enableUser(@PathVariable("id") Long id) {
        log.info("开启用户, UserController -> /api/main/admin/users/{}/enable", id);
        adminUserApplicationService.enableUser(id);
        return Result.success();
    }

    /**
     * 禁用用户
     * @param id
     * @return
     */
    @PutMapping("{id}/disable")
    public Result<Void> disableUser(@PathVariable("id") Long id) {
        log.info("禁用用户, UseController -> /api/main/admin/users/{}/disable", id);
        adminUserApplicationService.disableUser(id);
        return Result.success();
    }

}

