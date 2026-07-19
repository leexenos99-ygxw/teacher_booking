package com.teacherbooking.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "登录响应")
public class LoginVO {

    @Schema(description = "Token")
    private String token;

    @Schema(description = "Token前缀")
    private String tokenPrefix;

    @Schema(description = "过期时间(毫秒)")
    private Long expiresIn;

    @Schema(description = "用户信息")
    private UserInfoVO userInfo;
}
