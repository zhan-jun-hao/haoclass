package com.haoclass.main;

import com.haoclass.security.feign.InternalFeignConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableFeignClients(
        basePackages = {"com.haoclass.coupon.api.client", "com.haoclass.pay.api.client"},
        defaultConfiguration = InternalFeignConfiguration.class)
@EnableScheduling
@SpringBootApplication
@MapperScan("com.haoclass.main.infrastructure.persistence.mapper")
public class MainServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MainServiceApplication.class, args);
    }
}
