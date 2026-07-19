package com.teacherbooking;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 教师交流预约管理系统启动类
 */
@SpringBootApplication(exclude = {
        RedisAutoConfiguration.class,
        RedisRepositoriesAutoConfiguration.class
})
@MapperScan("com.teacherbooking.mapper")
@EnableAsync
@EnableScheduling
public class TeacherBookingApplication {

    public static void main(String[] args) {
        SpringApplication.run(TeacherBookingApplication.class, args);
        System.out.println("===============================================");
        System.out.println("  教师交流预约管理系统 启动成功!");
        System.out.println("  API文档: http://localhost:8080/doc.html");
        System.out.println("===============================================");
    }
}
