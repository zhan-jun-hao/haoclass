# haoclass-server

轻量版好课后端骨架。

## 环境

- JDK 17
- Maven 3.9+

## 模块

```text
gateway-service  网关服务
main-service     主业务服务：用户、课程、订单、评论等
pay-service      支付服务
coupon-service   优惠券服务
```

## 启动顺序

1. 启动 Nacos
2. 启动 Redis、MySQL 等基础组件
3. 启动业务服务
4. 启动 gateway-service

## 常用命令

```bash
mvn clean package -DskipTests
mvn -pl main-service spring-boot:run
mvn -pl gateway-service spring-boot:run
```
