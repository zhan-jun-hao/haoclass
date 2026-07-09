package com.haoclass.main.domain.service.impl;

import com.haoclass.main.domain.service.*;
import com.haoclass.main.domain.service.context.CourseAccessContext;
import com.haoclass.main.infrastructure.common.enums.CourseChargeTypeEnum;
import com.haoclass.main.infrastructure.persistence.po.Course;
import com.haoclass.main.infrastructure.persistence.po.CourseEpisode;
import com.haoclass.main.infrastructure.persistence.po.CourseUser;
import com.haoclass.main.infrastructure.persistence.po.User;
import com.haoclass.main.infrastructure.security.SecurityUserHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CourseAccessContextServiceImpl implements CourseAccessService {

    private final CourseService courseService;
    private final CourseEpisodeService courseEpisodeService;
    private final UserService userService;
    private final CourseUserService courseUserService;

    @Override
    public CourseAccessContext checkCourseAccess(Long courseId, Long episodeId) {
        User user = userService.getUserById(SecurityUserHolder.getUserId());
        // 1.查询课程是否存在且上架
        Course course = courseService.getPublishedCourseById(courseId);
        // 2.查询章节是否属于该课程且已发布
        CourseEpisode courseEpisode = courseEpisodeService.getPublishedEpisodeById(courseId, episodeId);
        // 3.课程是否免费 或 课程是否试看
        if(course.getChargeType() == CourseChargeTypeEnum.FREE || courseEpisode.getFreePreview() == 1) {
            CourseAccessContext context = new CourseAccessContext();
            context.setCourse(course);
            context.setCourseEpisode(courseEpisode);
            context.setCanWatch(true);
            context.setUser(user);
            return context;
        }
        // 4.用户是否是VIP用户 且 权益没过期 且 课程类型是VIP免费
        if(course.getChargeType() == CourseChargeTypeEnum.VIP_FREE) {
            if(user.getVipExpireTime() != null && user.getVipExpireTime().isAfter(LocalDateTime.now())) {
                CourseAccessContext context = new CourseAccessContext();
                context.setCourse(course);
                context.setCourseEpisode(courseEpisode);
                context.setUser(user);
                context.setCanWatch(true);
                return context;
            }
        }
        // 5.课程需要购买 且 用户不是VIP时 查询用户是否有对应课程权益
        CourseUser courseUser = courseUserService.findValidByUserIdAndCourseId(SecurityUserHolder.getUserId(), courseId);
        if(Objects.isNull(courseUser)) {
            CourseAccessContext context = new CourseAccessContext();
            context.setCourse(course);
            context.setCourseEpisode(courseEpisode);
            context.setUser(user);
            context.setCanWatch(false);
            context.setDenyReason("课程需要购买, 但是用户没有该权益");
            return context;
        }
        // 6.课程需要购买 但 用户不是VIP 但是他有权益
        CourseAccessContext context = new CourseAccessContext();
        context.setCourse(course);
        context.setCourseEpisode(courseEpisode);
        context.setUser(user);
        context.setCanWatch(true);
        context.setCourseUser(courseUser);

        return context;
    }
}
