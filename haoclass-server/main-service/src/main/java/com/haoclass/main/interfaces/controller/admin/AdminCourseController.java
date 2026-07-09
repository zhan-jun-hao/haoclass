package com.haoclass.main.interfaces.controller.admin;

import com.alibaba.fastjson.JSONObject;
import com.haoclass.common.result.PageResult;
import com.haoclass.common.result.Result;
import com.haoclass.main.application.service.admin.AdminCourseApplicationService;
import com.haoclass.main.interfaces.vo.course.admin.request.AdminCoursePageQueryReqVo;
import com.haoclass.main.interfaces.vo.course.admin.request.AdminCourseReqVo;
import com.haoclass.main.interfaces.vo.course.admin.request.AdminCourseUpdateReqVo;
import com.haoclass.main.interfaces.vo.course.admin.response.AdminCourseBasicVo;
import com.haoclass.main.interfaces.vo.course.admin.response.AdminCourseDetailVo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 后台管理领域-admin端-课程管理
 *
 * @author zhanjunhao
 * @since 2026/5/25
 */
@Slf4j
@RequestMapping("/api/main/admin/courses")
@RestController
@RequiredArgsConstructor
public class AdminCourseController {

    private final AdminCourseApplicationService adminCourseApplicationService;

    /**
     * 分页查询课程基础信息
     * @param query
     * @return
     */
    @GetMapping
    public Result<PageResult<AdminCourseBasicVo>> getCoursePageList(AdminCoursePageQueryReqVo query) {
        log.info("分页查询, CourseController -> /api/main/admin/courses, query: {}", JSONObject.toJSONString(query));
        return Result.success(adminCourseApplicationService.getCoursePageList(query));
    }

    @GetMapping("{id}")
    public Result<AdminCourseDetailVo> getCourseDetail(@PathVariable("id") Long id) {
        log.info("查询课程详情, CourseController -> /api/main/admin/courses/{}", id);
        return Result.success(adminCourseApplicationService.getCourseDetail(id));
    }

    /**
     * 新增课程
     * @param reqVo
     * @return
     */
    @PostMapping
    public Result<Void> addCourse(@RequestBody @Valid AdminCourseReqVo reqVo) {
        log.info("新增课程, CourseController -> /api/main/admin/courses, reqVo: {}", JSONObject.toJSONString(reqVo));
        adminCourseApplicationService.addNewCourse(reqVo);
        return Result.success();
    }

    /**
     * 修改课程
     * @param id
     * @param reqVo
     * @return
     */
    @PutMapping("{id}")
    public Result<Void> updateCourse(@PathVariable("id") Long id, @RequestBody @Valid AdminCourseUpdateReqVo reqVo) {
        log.info("修改课程, CourseController -> /api/main/admin/courses/{}, reqVo: {}", id, JSONObject.toJSONString(reqVo));
        adminCourseApplicationService.modifyCourse(id, reqVo);
        return Result.success();
    }

    /**
     * 逻辑删除课程
     * @param id
     * @return
     */
    @DeleteMapping("{id}")
    public Result<Void> deleteCourse(@PathVariable("id") Long id) {
        log.info("删除课程, CourseController -> /api/main/admin/courses/{}", id);
        adminCourseApplicationService.removeCourse(id);
        return Result.success();
    }

    /**
     * 上架课程
     * @param id
     * @return
     */
    @PutMapping("{id}/publish")
    public Result<Void> publishCourse(@PathVariable("id") Long id) {
        log.info("上架课程, CourseController -> /api/main/admin/courses/{}/publish", id);
        adminCourseApplicationService.publishCourse(id);
        return Result.success();
    }

    /**
     * 下架课程
     * @param id
     * @return
     */
    @PutMapping("{id}/unpublish")
    public Result<Void> unpublishCourse(@PathVariable("id") Long id) {
        log.info("下架课程, CourseController -> /api/main/admin/courses/{}/unpublish", id);
        adminCourseApplicationService.unpublishCourse(id);
        return Result.success();
    }
}
