package com.potato.template.controller;

import com.potato.template.annotation.LoginCheck;
import com.potato.template.entity.param.UserLoginParam;
import com.potato.template.entity.param.UserRegisterParam;
import com.potato.template.entity.param.UserUpdateParam;
import com.potato.template.entity.vo.UserVo;
import com.potato.template.service.IUserService;
import com.potato.template.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private IUserService userService;

    @PostMapping("/login")
    @Operation(summary = "登录接口", description = "返回token")
    public Result login(@RequestBody @Validated UserLoginParam userLoginParam){
        String token = userService.login(userLoginParam.getEmail(), userLoginParam.getPassword());
        log.info("login api,loginName={},loginResult={}", userLoginParam.getEmail(), token);
        return Result.ok().data(token);
    }

    @PostMapping("/logout")
    @LoginCheck
    public Result logout(@RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String token){
        Boolean success = userService.logout(token);
        return Result.ok().data(success);
    }

    @PostMapping("/register")
    @Operation(summary = "注册接口")
    public Result register(@RequestBody @Validated UserRegisterParam userRegisterParam){
        String token = userService.register(userRegisterParam);
        return Result.ok().data(token);
    }

    @GetMapping("/info")
    @Operation(summary = "获取用户信息")
    @LoginCheck
    public Result getUserinfo(@RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String token){
        UserVo userinfo = userService.getUserinfo(token);
        return Result.ok().data(userinfo);
    }

    @PutMapping("/info")
    @Operation(summary = "修改用户信息")
    @LoginCheck
    public Result updateUserinfo(@RequestBody @Validated UserUpdateParam userUpdateParam,@RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String token){
        boolean result = userService.updateUserinfo(userUpdateParam,token);
        return Result.ok().data(result);
    }
}
