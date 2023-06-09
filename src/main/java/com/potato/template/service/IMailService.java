package com.potato.template.service;

public interface IMailService {

    /**
     * 发送验证码
     * @param mail
     * @return
     */
    Boolean getCode(String mail);
}
