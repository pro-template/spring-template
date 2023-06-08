package com.potato.template.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.potato.template.entity.User;
import com.potato.template.entity.UserToken;
import com.potato.template.entity.param.UserRegisterParam;
import com.potato.template.entity.param.UserUpdateParam;
import com.potato.template.entity.vo.UserVo;
import com.potato.template.exception.BusinessException;
import com.potato.template.mapper.UserMapper;
import com.potato.template.mapper.UserTokenMapper;
import com.potato.template.service.IUserService;
import com.potato.template.utils.HttpCodeEnum;
import com.potato.template.utils.JwtUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserTokenMapper userTokenMapper;

    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "potato";

    @Override
    public String login(String email, String password) {
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());
        QueryWrapper<User> w = new QueryWrapper<>();
        w.eq("email", email);
        w.eq("password",encryptPassword);
        User user = userMapper.selectOne(w);
        if(user==null){
            throw new BusinessException(HttpCodeEnum.SYSTEM_ERROR,"用户不存在或密码错误");
        }
        String token = JwtUtils.getJwtToken(user.getId(), user.getEmail());
        UserToken userToken = userTokenMapper.selectById(user.getId());
        if (userToken == null) {
            userToken = new UserToken();
            userToken.setUserId(user.getId());
            userToken.setToken(token);
            int insertResult = userTokenMapper.insert(userToken);
            if(insertResult>0){
                return token;
            }
        }else {
            userToken.setToken(token);
            int updateResult = userTokenMapper.updateById(userToken);
            if(updateResult>0){
                return token;
            }
        }
        throw new BusinessException(HttpCodeEnum.SYSTEM_ERROR,"登录失败");
    }

    @Override
    public Boolean logout(Long userId) {
        return null;
    }

    @Override
    public String register(UserRegisterParam userRegisterParam) {
        String username = userRegisterParam.getUsername();
        String email = userRegisterParam.getEmail();
        String password = userRegisterParam.getPassword();
        String checkPassword = userRegisterParam.getCheckPassword();
        // 密码和校验密码相同
        if (!password.equals(checkPassword)) {
            throw new BusinessException(HttpCodeEnum.PARAMS_ERROR, "两次输入的密码不一致");
        }
        // 账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(HttpCodeEnum.PARAMS_ERROR, "邮箱已经注册");
        }
        // MD5加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());
        // 插入数据
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(encryptPassword);
        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new BusinessException(HttpCodeEnum.SYSTEM_ERROR, "注册失败，数据库错误");
        }

        String token = JwtUtils.getJwtToken(user.getId(), user.getEmail());
        return token;
    }

    @Override
    public UserVo getUserinfo(String token) {
        String userId = JwtUtils.parseId(token);
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(HttpCodeEnum.NOT_LOGIN_ERROR);
        }
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(user,userVo);
        return userVo;
    }

    @Override
    public Boolean updateUserinfo(UserUpdateParam userUpdateParam, String token) {
        User user = new User();
        String password = userUpdateParam.getPassword();
        BeanUtils.copyProperties(userUpdateParam, user);
        String userId = JwtUtils.parseId(token);
        // MD5加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());
        user.setId(userId);
        user.setPassword(encryptPassword);
        boolean result = this.updateById(user);
        return result;
    }

    @Override
    public Boolean logout(String token) {
        String userId = JwtUtils.parseId(token);
        return userTokenMapper.deleteById(userId)>0;
    }
}
