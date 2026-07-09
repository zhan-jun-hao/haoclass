package com.haoclass.main.infrastructure.persistence.mapper;

import com.haoclass.main.interfaces.vo.course.client.response.ClientMyCourseVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ClientMyCourseMapper {

    /**
     * 连表查询课程、课程章节、课程进度
     * @param userId
     * @return
     */
    List<ClientMyCourseVo> selectMyCourses(@Param("userId") Long userId);
}
