package com.haoclass.main.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.haoclass.main.infrastructure.persistence.po.CourseCommentLike;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

public interface CourseCommentLikeMapper extends BaseMapper<CourseCommentLike> {

    @Insert("""
            INSERT IGNORE INTO course_comment_like
                (id, commentId, userId, status, createdUser, updatedUser, createTime, updateTime, deleted)
            VALUES
                (#{id}, #{commentId}, #{userId}, 1, #{userId}, #{userId}, #{now}, #{now}, 0)
            """)
    int insertIgnore(@Param("id") Long id,
                     @Param("commentId") Long commentId,
                     @Param("userId") Long userId,
                     @Param("now") LocalDateTime now);
}
