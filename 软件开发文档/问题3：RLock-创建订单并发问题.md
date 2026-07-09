# 1.背景

在创建订单的时候，我们经过层层查询，然后才进行创建订单的操作，所以，先查再更新的这种逻辑，形成条件反射，想到高并发问题，有可能用户同时创建两个订单，这个时候就适合用RLock锁了

## 2.引入依赖

```xml
<dependency>
    <groupId>org.redisson</groupId>
    <artifactId>redisson-spring-boot-starter</artifactId>
    <version>3.52.0</version>
</dependency>
```

# 3.代码

```java
    @Override
    public CourseOrderBasicVo createCourseOrder(CourseOrderReqVo reqVo) {
        Long userId = SecurityUserHolder.getUserId();
        Long courseId = reqVo.getCourseId();
        String lockKey = "main-service:order:create:" + userId + ":" + courseId;
        RLock lock = redissonClient.getLock(lockKey);
        try {
            boolean isLocked = lock.tryLock(3, TimeUnit.SECONDS);
            if(!isLocked) {
                throw new BusinessException("操作过于频繁");
            }

            return transactionTemplate.execute(status ->
                doCreateCourseOrder(userId, courseId)
            );

        } catch (InterruptedException e) {
            // 标记该线程已经中断
            Thread.currentThread().interrupt();
            throw BusinessException.badRequest("操作已中断，请稍后重试");
        } finally {
            if(lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
```

```java
 // 无返回值
transactionTemplate.executeWithoutResult(status ->
                    doCloseExpiredPendingOrders()
            );
```



# 4.事务问题

当这个方法执行完毕后，才会提交事务，所以事务没有提交的时候就把锁释放了，其他请求就拿到了锁，然后查询记录，就会再次创建订单，所以我们要保证事务提交了以后再释放锁，所以我们将业务逻辑用方法封装起来，使用transactionTemplate让事务在锁释放前提交

# 5.确定锁粒度问题

这里的锁粒度是userId + courseId，因为一个用户对于不同的课程有不同的订单，所以一个用户在对一个课程创建订单的时候，为了避免他创建多个订单，所以粒度选择为userId + courseId

# 6.我们怎么保证创建订单幂等的

```java
userId + courseId 分布式锁
        +
锁内查询已有订单
        +
事务提交后再释放锁
```

不管接口被请求一次还是十次，最终只会生成一张有效待支付订单