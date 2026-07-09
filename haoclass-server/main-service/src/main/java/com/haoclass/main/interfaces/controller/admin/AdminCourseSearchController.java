package com.haoclass.main.interfaces.controller.admin;

import com.haoclass.common.result.Result;
import com.haoclass.main.application.service.admin.AdminCourseSearchApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端课程搜索索引接口
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/main/admin/search/courses")
public class AdminCourseSearchController {

    private final AdminCourseSearchApplicationService adminCourseSearchApplicationService;

    /**
     * 全量重建已上架课程索引
     *
     * @return 重建课程数量
     */
    @PostMapping("/rebuild")
    public Result<Integer> rebuildCourseIndex() {
        return Result.success(adminCourseSearchApplicationService.rebuildCourseIndex());
    }

    /**
     * 同步单个课程索引
     *
     * @param id 课程ID
     * @return 无
     */
    @PutMapping("{id}/sync")
    public Result<Void> syncCourseIndex(@PathVariable("id") Long id) {
        adminCourseSearchApplicationService.syncCourseIndex(id);
        return Result.success();
    }

    /**
     * 删除单个课程索引
     *
     * @param id 课程ID
     * @return 无
     */
    @DeleteMapping("{id}")
    public Result<Void> deleteCourseIndex(@PathVariable("id") Long id) {
        adminCourseSearchApplicationService.deleteCourseIndex(id);
        return Result.success();
    }
}
