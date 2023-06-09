package com.potato.template.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.potato.template.entity.User;
import com.potato.template.entity.UserToken;
import com.potato.template.entity.param.UserRegisterParam;
import com.potato.template.entity.param.UserUpdateParam;
import com.potato.template.entity.param.UserUpdatePwdParam;
import com.potato.template.entity.vo.UserVo;
import com.potato.template.exception.BusinessException;
import com.potato.template.mapper.UserMapper;
import com.potato.template.mapper.UserTokenMapper;
import com.potato.template.service.IUserService;
import com.potato.template.utils.HttpCodeEnum;
import com.potato.template.utils.JwtUtils;
import com.potato.template.utils.RedisCache;
import com.potato.template.utils.UploadUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserTokenMapper userTokenMapper;

    @Resource
    private RedisCache redisCache;

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

    @Override
    public String updateAvatar(MultipartFile file,String token) {
        try {
            if(file.isEmpty()){
                throw new BusinessException(HttpCodeEnum.PARAMS_ERROR, "文件不能为空");
            }
            //获取文件的内容
            InputStream is = file.getInputStream();
            boolean isImage = UploadUtils.isImage(is);
            if(!isImage){
                throw new BusinessException(HttpCodeEnum.PARAMS_ERROR, "上传文件不是图片");
            }
            // 获取文件名
            String fileName = file.getOriginalFilename();
            // 产生一个随机目录
            String randomDir = UploadUtils.getDir();
            // 产生一个随机文件名
            String uuidFilename = UploadUtils.getUUIDName(fileName);
            String resourcePath = getClass().getResource("/").getPath();
            File fileDir = new File(resourcePath+"static/"+"avatar" + randomDir);
            //若文件夹不存在,则创建出文件夹
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            //创建新的文件夹
            File newFile = new File(fileDir, uuidFilename);
            String userId = JwtUtils.parseId(token);

            //将保存的文件路径更新到用户信息
            String savePath = "http://localhost:3000/avatar"+ randomDir + "/" + uuidFilename;
            log.info(savePath);
            User user = new User();
            user.setId(userId);
            user.setAvatar(savePath);
            boolean saveResult = this.updateById(user);
            if (!saveResult) {
                throw new BusinessException(HttpCodeEnum.SYSTEM_ERROR, "上传头像失败，数据库错误");
            }
            file.transferTo(newFile);
            return savePath;
        } catch (IOException e) {
            throw new BusinessException(HttpCodeEnum.SYSTEM_ERROR, "上传头像失败");
        }
    }

    @Override
    public Boolean updatePwd(UserUpdatePwdParam userUpdatePwdParam) {
        String email = userUpdatePwdParam.getEmail();
        String code = userUpdatePwdParam.getCode();
        String password = userUpdatePwdParam.getPassword();
        QueryWrapper<User> w = new QueryWrapper<>();
        w.eq("email",email);
        User user = this.getOne(w);

        //账号存在校验
        if (null == user) {
            throw new BusinessException(HttpCodeEnum.PARAMS_ERROR, "账号不存在！");
        };

        // 验证码过期校验
        String cacheCode = redisCache.getCacheObject(email); // 获取缓存中该账号的验证码
        if (cacheCode == null) {
            throw new BusinessException(HttpCodeEnum.OPERATION_ERROR, "验证码已过期，请重新获取！");
        }

        // 验证码正确性校验
        if (!cacheCode.equals(code)) {
            throw new BusinessException(HttpCodeEnum.OPERATION_ERROR, "验证码错误");
        }
        // MD5加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());
        user.setPassword(encryptPassword);
        boolean updateResult = this.updateById(user);
        if (updateResult) {
            // 将验证码过期
            redisCache.expire(email, 0);
        }
        // 修改密码
        return updateResult;
    }
}
