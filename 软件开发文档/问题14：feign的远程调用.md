# 1.业务

用户在购买课程时，查询可用的优惠券，main-service调用coupon-service

# 2.coupon-api

coupon-api是coupon-service提供给其他服务的client，能够进行数据交流

## 2.1 FeignClient

```java
import com.haoclass.common.result.Result;
import com.haoclass.coupon.api.dto.response.AvailableCouponResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 优惠券服务远程调用接口
 */
@FeignClient(name = "coupon-service", path = "/api/coupon/inner/user-coupons")
public interface CouponFeignClient {

    /**
     * 查询当前用户可用于指定订单金额的优惠券
     *
     * @param orderAmount 订单原始金额，单位：分
     * @return 可用优惠券列表
     */
    @GetMapping("/available")
    Result<List<AvailableCouponResponse>> getAvailableCoupons(@RequestParam("orderAmount") Integer orderAmount);
}
```

## 2.2 Dto

```java
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 订单可用优惠券响应对象
 */
@Data
public class AvailableCouponResponse {

    /**
     * 用户优惠券ID
     */
    private Long userCouponId;

    /**
     * 优惠券名称
     */
    private String couponName;

    /**
     * 使用门槛金额，单位：分
     */
    private Integer thresholdAmount;

    /**
     * 优惠金额，单位：分
     */
    private Integer discountAmount;

    /**
     * 有效期结束时间
     */
    private LocalDateTime validEndTime;
}

```

# 3.coupon-service

## 3.1 InnerUserCouponController

```java
/**
 * 用户优惠券内部调用接口
 */
@Validated // 这个注解能对@Min和@RequestParam生效
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coupon/inner/user-coupons")
public class InnerUserCouponController {

    private final InnerUserCouponApplicationService innerUserCouponApplicationService;

    /**
     * 查询当前用户的订单可用优惠券
     */
    @GetMapping("/available")
    public Result<List<AvailableCouponResponse>> getAvailableCoupons(@RequestParam("orderAmount")
            @Min(value = 0, message = "订单金额不能小于0") Integer orderAmount) {
        return Result.success(innerUserCouponApplicationService.getAvailableCouponList(orderAmount));
    }
}
```



# 4.main-service

## 4.1 ClientOrderController

```java
    /**
     * 查询课程订单能够使用的优惠券
     *
     * @param courseId
     * @return
     */
    @GetMapping("/available")
    public Result<List<CourseOrderAvailableCouponRespVo>> getAvailableList(@RequestParam("courseId") Long courseId) {
        log.info("查询订单购买课程: {} 可用优惠券", courseId);
        return clientCourseOrderApplicationService.getAvailableList(courseId);
    }
```

## 4.2 ClientOrderApplicationServiceImpl

```java
 @Override
    public Result<List<CourseOrderAvailableCouponRespVo>> getAvailableList(Long courseId) {
        // 1.课程必须存在 且 上架
        Course course = courseService.getPublishedCourseById(courseId);
        // 2.课程可用的优惠券列表
        Result<List<AvailableCouponResponse>> result =
                couponFeignClient.getAvailableCoupons(course.getPrice());

        if (result == null || result.getCode() != 200 || result.getData() == null) {
            String message = result == null ? "优惠券服务无响应" : result.getMsg();
            throw new BusinessException("查询可用优惠券失败：" + message);
        }

        List<AvailableCouponResponse> availableCouponResponseList = result.getData();
        if(availableCouponResponseList.isEmpty()) {
            return Result.success(Collections.emptyList());
        }
        List<CourseOrderAvailableCouponRespVo> resultList = new ArrayList<>();
        availableCouponResponseList.forEach(availableCouponResponse -> {
            CourseOrderAvailableCouponRespVo respVo = OrderAvailableCouponConverter.INSTANCE.innerToRespVo(availableCouponResponse);
            respVo.setPayAmount(Math.max(0, course.getPrice() - respVo.getDiscountAmount()));
            resultList.add(respVo);
        });
        return Result.success(resultList);
    }
```

# 5.总结

main-service 准备前端视图vo，访问coupon-service提供的client叫coupon-api，然后FeignClient向coupon-service请求数据，coupon-service返回client的inner视图respVo，注意配置网关gateway，把inner接口给屏蔽掉，不放行

```yaml
  cloud:
    gateway:
      routes:
        - id: coupon-service
          uri: lb://coupon-service
          predicates:
            - Path=/api/coupon/client/**,/api/coupon/admin/**

        - id: pay-service
          uri: lb://pay-service
          predicates:
            - Path=/api/pay/client/**,/api/pay/admin/**

        - id: main-service
          uri: lb://main-service
          predicates:
            - Path=/api/client/**,/api/admin/**
```

