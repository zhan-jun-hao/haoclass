package com.haoclass.main.domain.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoclass.common.exception.BusinessException;
import com.haoclass.main.domain.service.CourseService;
import com.haoclass.main.infrastructure.persistence.mapper.CourseMapper;
import com.haoclass.main.infrastructure.persistence.po.Course;
import com.haoclass.main.infrastructure.security.SecurityUserHolder;
import com.haoclass.main.interfaces.vo.course.client.request.ClientCoursePageQueryReqVo;
import com.haoclass.main.interfaces.vo.course.admin.request.AdminCoursePageQueryReqVo;
import com.haoclass.security.context.UserContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {

    @Override
    public IPage<Course> pageQuery(AdminCoursePageQueryReqVo query) {
        if (Objects.isNull(query)) {
            query = new AdminCoursePageQueryReqVo();
        }

        if (query.getCurrent() == null || query.getCurrent() < 1) {
            query.setCurrent(1L);
        }

        if (query.getSize() == null || query.getSize() < 1) {
            query.setSize(10L);
        }

        if (query.getSize() > 100) {
            query.setSize(100L);
        }

        LambdaQueryWrapper<Course> wrapper = Wrappers.lambdaQuery(Course.class)
                .like(StringUtils.hasText(query.getCategoryName()),
                        Course::getCategoryName,
                        query.getCategoryName())
                .like(StringUtils.hasText(query.getTitle()),
                        Course::getTitle,
                        query.getTitle())
                .eq(query.getStatus() != null,
                        Course::getStatus,
                        query.getStatus())
                .eq(Course::getDeleted, 0)
                .orderByDesc(Course::getSort)
                .orderByDesc(Course::getCreateTime);

        return this.page(new Page<>(query.getCurrent(), query.getSize()), wrapper);
    }

    @Override
    public IPage<Course> pagePublishedQuery(ClientCoursePageQueryReqVo query) {
        if (Objects.isNull(query)) {
            query = new ClientCoursePageQueryReqVo();
        }

        if (query.getCurrent() == null || query.getCurrent() < 1) {
            query.setCurrent(1L);
        }

        if (query.getSize() == null || query.getSize() < 1) {
            query.setSize(10L);
        }

        if (query.getSize() > 100) {
            query.setSize(100L);
        }

        LambdaQueryWrapper<Course> wrapper = Wrappers.lambdaQuery(Course.class)
                .eq(Course::getDeleted, 0)
                .eq(Course::getStatus, 1)
                .like(StringUtils.hasText(query.getCategoryName()),
                        Course::getCategoryName,
                        query.getCategoryName())
                .like(StringUtils.hasText(query.getTitle()),
                        Course::getTitle,
                        query.getTitle())
                .orderByDesc(Course::getSort)
                .orderByDesc(Course::getCreateTime);

        return this.page(new Page<>(query.getCurrent(), query.getSize()), wrapper);
    }

    @Override
    public Course getCourseById(Long id) {
        Course course = this.getById(id);
        if (Objects.isNull(course)) {
            throw BusinessException.notFound("课程不存在");
        }

        return course;
    }

    @Override
    public Course getPublishedCourseById(Long id) {
        Course course = this.getOne(Wrappers.lambdaQuery(Course.class)
                .eq(Course::getId, id)
                .eq(Course::getDeleted, 0)
                .eq(Course::getStatus, 1));
        if (Objects.isNull(course)) {
            throw BusinessException.notFound("课程不存在或未上架");
        }

        return course;
    }

    @Override
    public void saveCourse(Course course) {
        course.setId(IdUtil.getSnowflakeNextId());
        course.setCreateTime(LocalDateTime.now());
        course.setUpdateTime(LocalDateTime.now());
        course.setCreatedUser(SecurityUserHolder.getUserId());
        course.setUpdatedUser(SecurityUserHolder.getUserId());

        this.save(course);
    }

    @Override
    public void updateCourseById(Long id, Course course) {
        Course originCourse = this.getById(id);
        if(Objects.isNull(originCourse)) {
            throw BusinessException.notFound("课程不存在");
        }
        course.setId(originCourse.getId());
        course.setUpdateTime(LocalDateTime.now());
        course.setUpdatedUser(SecurityUserHolder.getUserId());

        this.updateById(course);
    }

    @Override
    public void deleteCourseById(Long id) {
        Course course = getCourseById(id);
        course.setUpdateTime(LocalDateTime.now());
        course.setUpdatedUser(SecurityUserHolder.getUserId());

        this.removeById(course);
    }

    @Override
    public void updateStatusById(Long id, Integer status) {
        LambdaUpdateWrapper<Course> wrapper = Wrappers.lambdaUpdate(Course.class)
                .eq(Course::getId, id)
                .set(Course::getStatus, status)
                .set(Course::getUpdateTime, LocalDateTime.now())
                .set(Course::getUpdatedUser, SecurityUserHolder.getUserId());
        boolean updated = this.update(wrapper);

        if(!updated) {
            throw BusinessException.badRequest("更新课程失败");
        }
    }

    @Override
    public void updateEpisodeCount(Long id, Integer episodeCount) {
        boolean updated = this.update(Wrappers.lambdaUpdate(Course.class)
                .eq(Course::getId, id)
                .eq(Course::getDeleted, 0)
                .set(Course::getEpisodeCount, episodeCount)
                .set(Course::getUpdateTime, LocalDateTime.now())
                .set(Course::getUpdatedUser, SecurityUserHolder.getUserId()));

        if(!updated) {
            throw BusinessException.badRequest("不存在这个课程");
        }
    }

    @Override
    public void updateBuyCountById(Long id) {
        LambdaUpdateWrapper<Course> wrapper = Wrappers.lambdaUpdate(Course.class)
                .eq(Course::getId, id)
                .eq(Course::getDeleted, 0)
                .setIncrBy(Course::getBuyCount, 1)
                .set(Course::getUpdateTime, LocalDateTime.now())
                .set(Course::getUpdatedUser, 0);

        boolean updated = this.update(wrapper);
        if(!updated) {
            throw BusinessException.notFound("不存在该课程");
        }
    }

    @Override
    public void decreaseBuyCountById(Long id) {
        LambdaUpdateWrapper<Course> wrapper = Wrappers.lambdaUpdate(Course.class)
                .eq(Course::getId, id)
                .eq(Course::getDeleted, 0)
                .gt(Course::getBuyCount, 0)
                .setDecrBy(Course::getBuyCount, 1)
                .set(Course::getUpdateTime, LocalDateTime.now())
                .set(Course::getUpdatedUser, 0);

        boolean updated = this.update(wrapper);
        if (!updated) {
            throw BusinessException.notFound("不存在该课程或购买数量已经为0");
        }
    }

    @Override
    public List<Course> findCourseListById(List<Long> ids) {
        return this.listByIds(ids);
    }
}
