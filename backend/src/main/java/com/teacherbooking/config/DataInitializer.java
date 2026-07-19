package com.teacherbooking.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.teacherbooking.entity.User;
import com.teacherbooking.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile({"dev", "local"})
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.username:admin}")
    private String adminUsername;

    @Value("${admin.password:}")
    private String adminPassword;

    public DataInitializer(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        initAdmin();
    }

    private void initAdmin() {
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>()
                        .eq(User::getRole, "ADMIN")
        );

        if (count != null && count > 0) {
            log.info("管理员账号已存在，跳过初始化");
            return;
        }

        if (adminPassword == null || adminPassword.trim().isEmpty()) {
            log.warn("未配置 admin.password，跳过管理员账号初始化");
            log.warn("请配置环境变量 ADMIN_PASSWORD 或在配置文件中设置 admin.password");
            return;
        }

        User admin = new User();
        admin.setUsername(adminUsername);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setRole("ADMIN");
        admin.setStatus("NORMAL");
        admin.setNickname("系统管理员");
        admin.setPhone("13800000000");

        userMapper.insert(admin);
        log.info("管理员账号初始化成功：用户名 = {}", adminUsername);
    }
}
