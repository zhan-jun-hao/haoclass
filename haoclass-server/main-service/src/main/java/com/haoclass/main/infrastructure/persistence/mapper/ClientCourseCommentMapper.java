package com.haoclass.main.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haoclass.main.domain.model.query.CourseCommentQuery;
import com.haoclass.main.interfaces.vo.comment.client.response.ClientCourseCommentVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ClientCourseCommentMapper {

    IPage<ClientCourseCommentVo> selectPage(Page<ClientCourseCommentVo> page,
                                            @Param("query") CourseCommentQuery query,
                                            @Param("userId") Long userId);
}
