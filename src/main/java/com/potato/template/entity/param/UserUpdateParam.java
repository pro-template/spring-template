package com.potato.template.entity.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.io.Serializable;

@Data
public class UserUpdateParam implements Serializable {

    @Schema(title = "用户名")
    @NotEmpty(message = "用户名不能为空")
    @Size(min = 4, max = 12, message = "用户名必须在4到12字符之间")
    private String username;

    @Schema(title = "邮箱")
    @NotEmpty(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;


    @Schema(title = "密码")
    @NotEmpty(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码必须在 6 到 20 个字符之间")
    private String password;
}
