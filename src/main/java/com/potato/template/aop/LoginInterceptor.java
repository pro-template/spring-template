package com.potato.template.aop;

import com.potato.template.annotation.LoginCheck;
import com.potato.template.entity.UserToken;
import com.potato.template.exception.BusinessException;
import com.potato.template.mapper.UserTokenMapper;
import com.potato.template.utils.HttpCodeEnum;
import com.potato.template.utils.JwtUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 登录拦截器
 */
@Aspect
@Component
public class LoginInterceptor {
    private HttpServletRequest request;

    @Resource
    private UserTokenMapper userTokenMapper;

    @Resource
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Around("@annotation(loginCheck)")
    public Object checkLogin(ProceedingJoinPoint joinPoint, LoginCheck loginCheck) throws Throwable {
        // 检查Token逻辑
        String token = request.getHeader("Authorization");
        boolean tokenIsValid = JwtUtils.checkToken(token);

        // 如果验证失败，可以抛出异常或返回错误信息
        if (!tokenIsValid) {
            throw new BusinessException(HttpCodeEnum.NOT_LOGIN_ERROR);
        }
        String userId = JwtUtils.parseId(token);
        UserToken userToken = userTokenMapper.selectById(userId);
        if(userToken == null){
            throw new BusinessException(HttpCodeEnum.NOT_LOGIN_ERROR);
        }

        if(!userToken.getToken().equals(token)){
            throw new BusinessException(HttpCodeEnum.NOT_LOGIN_ERROR);
        }

        // 验证通过后继续执行原方法
        return joinPoint.proceed();
    }
}
