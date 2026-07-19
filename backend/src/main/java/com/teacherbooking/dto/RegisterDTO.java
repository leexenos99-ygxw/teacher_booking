package com.teacherbooking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 注册DTO
 */
@Data
@Schema(description = "注册请求")
public class RegisterDTO {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 20, message = "用户名长度4-20位")
    @Schema(description = "用户名", required = true)
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度6-20位")
    @Schema(description = "密码", required = true)
    private String password;

    @NotBlank(message = "确认密码不能为空")
    @Schema(description = "确认密码", required = true)
    private String confirmPassword;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号")
    private String phone;

    @NotBlank(message = "角色不能为空")
    @Pattern(regexp = "^(PARENT|TEACHER)$", message = "角色只能是PARENT或TEACHER")
    @Schema(description = "角色: PARENT-家长 TEACHER-教师", required = true)
    private String role;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "真实姓名")
    private String realName;
}
