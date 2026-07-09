package com.haoclass.main.application.service.client;

import com.haoclass.common.result.PageResult;
import com.haoclass.main.interfaces.vo.comment.client.request.ClientCourseCommentPageQueryReqVo;
import com.haoclass.main.interfaces.vo.comment.client.request.ClientCourseCommentReqVo;
import com.haoclass.main.interfaces.vo.comment.client.request.ClientCourseMyCommentPageQueryReqVo;
import com.haoclass.main.interfaces.vo.comment.client.response.ClientCourseCommentVo;
import com.haoclass.main.interfaces.vo.comment.client.response.ClientCourseMyCommentRespVo;

public interface ClientCourseCommentApplicationService {

    /**
     * 分页查询评论
     * @param query
     * @return
     */
    PageResult<ClientCourseCommentVo> getCourseCommentPageList(ClientCourseCommentPageQueryReqVo query);

    /**
     * 用户发表评论
     * @param reqVo
     * @return
     */
    ClientCourseCommentVo addCourseComment(ClientCourseCommentReqVo reqVo);

    /**
     * 用户删除评论
     * @param id
     */
    void removeMyCourseComment(Long id);

    /**
     * 用户点赞评论
     * @param id
     */
    void likeCourseComment(Long id);

    /**
     * 用户取消点赞评论
     * @param id
     */
    void unlikeCourseComment(Long id);

    /**
     * 分页查询我的评论
     * @param reqVo
     * @return
     */
    PageResult<ClientCourseMyCommentRespVo> getMyCommentPageList(ClientCourseMyCommentPageQueryReqVo reqVo);
}
