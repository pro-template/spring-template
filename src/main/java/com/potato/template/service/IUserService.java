package com.potato.template.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.potato.template.entity.User;
import com.potato.template.entity.param.UserRegisterParam;
import com.potato.template.entity.param.UserUpdateParam;
import com.potato.template.entity.vo.UserVo;

public interface IUserService extends IService<User> {

    /**
     * 登录
     * @param email 邮箱
     * @param password 密码
     * @return jwt
     */
    String login(String email, String password);

    /**
     * 登出接口
     * @param userId
     * @return
     */
    Boolean logout(Long userId);

    /**
     * 注册
     * @param userRegisterParam
     * @return
     */
    String register(UserRegisterParam userRegisterParam);

    /**
     * 获取用户信息
     * @param token
     * @return
     */
    UserVo getUserinfo(String token);

    /**
     * 更新用户信息
     * @param userUpdateParam
     * @param token
     * @return
     */
    Boolean updateUserinfo(UserUpdateParam userUpdateParam,String token);
}
