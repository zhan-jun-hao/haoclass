package com.haoclass.main.application.service.admin;

import com.haoclass.common.result.PageResult;
import com.haoclass.main.interfaces.vo.comment.admin.request.AdminCourseCommentPageQueryReqVo;
import com.haoclass.main.interfaces.vo.comment.admin.response.AdminCourseCommentBasicVo;
import com.haoclass.main.interfaces.vo.comment.admin.response.AdminCourseCommentDetailVo;

public interface AdminCourseCommentApplicationService {

    /**
     * 分页查询评论
     * @param query
     * @return
     */
    PageResult<AdminCourseCommentBasicVo> getCourseCommentPageList(AdminCourseCommentPageQueryReqVo query);

    AdminCourseCommentDetailVo getCourseCommentDetail(Long id);

    void removeCourseComment(Long id);

    void approveCourseComment(Long id);

    void rejectCourseComment(Long id);

    void hideCourseComment(Long id);
}
