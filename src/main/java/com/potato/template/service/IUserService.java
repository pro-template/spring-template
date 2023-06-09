package com.potato.template.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.potato.template.entity.User;
import com.potato.template.entity.param.UserRegisterParam;
import com.potato.template.entity.param.UserUpdateParam;
import com.potato.template.entity.param.UserUpdatePwdParam;
import com.potato.template.entity.vo.UserVo;
import org.springframework.web.multipart.MultipartFile;

public interface IUserService extends IService<User> {

    /**
     * 登录
     * @param email 邮箱
     * @param password 密码
     * @return jwt
     */
    String login(String email, String password);

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

    /**
     * 登出
     * @param token
     * @return
     */
    Boolean logout(String token);

    /**
     * 更新用户头像
     * @param file
     * @param token
     * @return
     */
    String updateAvatar(MultipartFile file,String token);

    /**
     * 修改用户密码
     * @param userUpdatePwdParam
     * @return
     */
    Boolean updatePwd(UserUpdatePwdParam userUpdatePwdParam);
}
