package com.teacherbooking.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.teacherbooking.common.exception.BusinessException;
import com.teacherbooking.common.result.ResultCode;
import com.teacherbooking.common.utils.SecurityUtils;
import com.teacherbooking.dto.LoginDTO;
import com.teacherbooking.dto.RegisterDTO;
import com.teacherbooking.entity.Parent;
import com.teacherbooking.entity.Teacher;
import com.teacherbooking.entity.User;
import com.teacherbooking.enums.RoleEnum;
import com.teacherbooking.mapper.ParentMapper;
import com.teacherbooking.mapper.TeacherMapper;
import com.teacherbooking.mapper.UserMapper;
import com.teacherbooking.security.CustomUserDetails;
import com.teacherbooking.security.JwtUtil;
import com.teacherbooking.service.AuthService;
import com.teacherbooking.vo.LoginVO;
import com.teacherbooking.vo.UserInfoVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 认证服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final TeacherMapper teacherMapper;
    private final ParentMapper parentMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.prefix}")
    private String tokenPrefix;

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getUsername(),
                        loginDTO.getPassword()
                )
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        if (!"NORMAL".equals(userDetails.getStatus())) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }

        String token = jwtUtil.generateToken(
                userDetails.getUserId(),
                userDetails.getUsername(),
                userDetails.getRole()
        );

        UserInfoVO userInfoVO = convertToUserInfoVO(userDetails);

        return new LoginVO(token, tokenPrefix, expiration, userInfoVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterDTO registerDTO) {
        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            throw new BusinessException("两次密码输入不一致");
        }

        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, registerDTO.getUsername())
                        .eq(User::getDeleted, 0)
        );
        if (count > 0) {
            throw new BusinessException(ResultCode.USERNAME_EXIST);
        }

        if (registerDTO.getPhone() != null) {
            Long phoneCount = userMapper.selectCount(
                    new LambdaQueryWrapper<User>()
                            .eq(User::getPhone, registerDTO.getPhone())
                            .eq(User::getDeleted, 0)
            );
            if (phoneCount > 0) {
                throw new BusinessException(ResultCode.PHONE_EXIST);
            }
        }

        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setPhone(registerDTO.getPhone());
        user.setRole(registerDTO.getRole());
        user.setNickname(registerDTO.getNickname() != null ? registerDTO.getNickname() : registerDTO.getUsername());
        user.setStatus("NORMAL");
        userMapper.insert(user);

        if (RoleEnum.TEACHER.getCode().equals(registerDTO.getRole())) {
            Teacher teacher = new Teacher();
            teacher.setUserId(user.getId());
            teacher.setRealName(registerDTO.getRealName());
            teacher.setAuditStatus("PENDING");
            teacherMapper.insert(teacher);
        } else if (RoleEnum.PARENT.getCode().equals(registerDTO.getRole())) {
            Parent parent = new Parent();
            parent.setUserId(user.getId());
            parent.setRealName(registerDTO.getRealName());
            parentMapper.insert(parent);
        }

        log.info("用户注册成功: userId={}, username={}, role={}",
                user.getId(), user.getUsername(), user.getRole());
    }

    @Override
    public UserInfoVO getCurrentUserInfo() {
        CustomUserDetails currentUser = SecurityUtils.getCurrentUser();
        if (currentUser == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        return convertToUserInfoVO(currentUser);
    }

    @Override
    public void logout() {
        // JWT是无状态的，前端删除token即可
        // 如需服务端控制，可加入Redis黑名单
        log.info("用户登出: userId={}", SecurityUtils.getCurrentUserId());
    }

    private UserInfoVO convertToUserInfoVO(CustomUserDetails userDetails) {
        UserInfoVO vo = new UserInfoVO();
        vo.setUserId(userDetails.getUserId());
        vo.setUsername(userDetails.getUsername());
        vo.setRole(userDetails.getRole());

        RoleEnum roleEnum = RoleEnum.getByCode(userDetails.getRole());
        if (roleEnum != null) {
            vo.setRoleName(roleEnum.getDesc());
        }

        User user = userMapper.selectById(userDetails.getUserId());
        if (user != null) {
            vo.setNickname(user.getNickname());
            vo.setAvatar(user.getAvatar());
            vo.setPhone(user.getPhone());
        }

        return vo;
    }
}
