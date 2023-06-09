package com.potato.template.controller;

import com.potato.template.service.IMailService;
import com.potato.template.utils.Result;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/mail")
@Slf4j
public class MailController {

    @Resource
    private IMailService mailService;


    @GetMapping("/code")
    public Result getCode(String mail){
        Boolean result = mailService.getCode(mail);
        return Result.ok().data(result);
    }
}
