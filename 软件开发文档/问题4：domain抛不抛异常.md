如果是业务规定，domain抛，命名为get，而如果业务就是可以返回null，那就再写一个方法find，get可以调用find，get要捕获异常，这样复用性就高了

```java
    @Override
    public CourseOrder getCourseOrderById(Long id, Long userId) {
        LambdaQueryWrapper<CourseOrder> wrapper = Wrappers.lambdaQuery(CourseOrder.class)
                .eq(CourseOrder::getId, id)
                .eq(CourseOrder::getUserId, userId)
                .eq(CourseOrder::getDeleted, 0);
        CourseOrder courseOrder = this.getOne(wrapper);
        if(Objects.isNull(courseOrder)) {
            throw BusinessException.notFound("不存在该订单");
        }

        return courseOrder;
    }

    @Override
    public CourseOrder findPendingOrder(Long userId, Long courseId) {
        LambdaQueryWrapper<CourseOrder> wrapper = Wrappers.lambdaQuery(CourseOrder.class)
                .eq(CourseOrder::getUserId, userId)
                .eq(CourseOrder::getCourseId, courseId)
                .eq(CourseOrder::getStatus, CourseOrderStatusEnum.PENDING_PAYMENT)
                .eq(CourseOrder::getDeleted, 0)
                .gt(CourseOrder::getExpireTime, LocalDateTime.now())
                .orderByDesc(CourseOrder::getCreateTime)
                .last("LIMIT 1");

        return this.getOne(wrapper, false);
    }
```

