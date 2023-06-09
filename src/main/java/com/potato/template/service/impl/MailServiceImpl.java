package com.potato.template.service.impl;

import com.potato.template.exception.BusinessException;
import com.potato.template.service.IMailService;
import com.potato.template.utils.HttpCodeEnum;
import com.potato.template.utils.RedisCache;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class MailServiceImpl implements IMailService {
    @Resource
    private JavaMailSender mailSender;

    @Resource
    private TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String from;

    @Value("${mail.code.overtime}")
    private Integer overtime;

    @Resource
    private RedisCache redisCache;


    @Override
    public Boolean getCode(String mail) {

        String code = String.valueOf(new Random().nextInt(899999) + 100000);

        redisCache.setCacheObject(mail, code, overtime, TimeUnit.MINUTES);

        // 读取模板设置参数
        Context context = new Context();
        context.setVariable("code",code);
        String content = templateEngine.process("email-template", context);
        log.info("code:" + code);
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        // 发件配置并发送邮件
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setTo(mail);
            mimeMessageHelper.setFrom(from);
            mimeMessage.setSubject("邮箱验证-重置密码");
            mimeMessageHelper.setText(content, true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new BusinessException(HttpCodeEnum.SYSTEM_ERROR,"邮件发送失败");
        }
        return true;
    }
}
