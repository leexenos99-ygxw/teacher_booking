package com.teacherbooking.service;

import com.teacherbooking.dto.LoginDTO;
import com.teacherbooking.dto.RegisterDTO;
import com.teacherbooking.vo.LoginVO;
import com.teacherbooking.vo.UserInfoVO;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 登录
     */
    LoginVO login(LoginDTO loginDTO);

    /**
     * 注册
     */
    void register(RegisterDTO registerDTO);

    /**
     * 获取当前用户信息
     */
    UserInfoVO getCurrentUserInfo();

    /**
     * 登出
     */
    void logout();
}
