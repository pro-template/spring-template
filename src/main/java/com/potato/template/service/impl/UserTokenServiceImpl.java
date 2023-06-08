package com.potato.template.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.potato.template.entity.UserToken;
import com.potato.template.mapper.UserTokenMapper;
import com.potato.template.service.IUserTokenService;
import org.springframework.stereotype.Service;

@Service
public class UserTokenServiceImpl  extends ServiceImpl<UserTokenMapper, UserToken> implements IUserTokenService {
}
