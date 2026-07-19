package com.teacherbooking.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户信息VO
 */
@Data
@Schema(description = "用户信息")
public class UserInfoVO {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "角色")
    private String role;

    @Schema(description = "角色名称")
    private String roleName;
}
