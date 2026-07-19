package com.teacherbooking.controller;

import com.teacherbooking.common.result.Result;
import com.teacherbooking.dto.LoginDTO;
import com.teacherbooking.dto.RegisterDTO;
import com.teacherbooking.service.AuthService;
import com.teacherbooking.vo.LoginVO;
import com.teacherbooking.vo.UserInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "用户注册、登录、认证相关接口")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        return Result.success(authService.login(loginDTO));
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public Result<Void> register(@Valid @RequestBody RegisterDTO registerDTO) {
        authService.register(registerDTO);
        return Result.success("注册成功", null);
    }

    @GetMapping("/userInfo")
    @Operation(summary = "获取当前用户信息")
    public Result<UserInfoVO> getUserInfo() {
        return Result.success(authService.getCurrentUserInfo());
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出")
    public Result<Void> logout() {
        authService.logout();
        return Result.success("登出成功", null);
    }
}
