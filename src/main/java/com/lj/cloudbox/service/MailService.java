package com.lj.cloudbox.service;

import com.lj.cloudbox.mapper.MailCodeMapper;
import com.lj.cloudbox.pojo.MailCode;
import com.lj.cloudbox.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class MailService {

    @Value("${spring.mail.username}")
    private String emailName;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    MailCodeMapper mailCodeMapper;

    public void mail(String verifierMail) {
        int code = (int)((Math.random() * 9 + 1) * 100000);
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(emailName);
        simpleMailMessage.setTo(verifierMail);
        simpleMailMessage.setSubject("cloudbox注册账号");
        simpleMailMessage.setText("验证码: " + code);
        javaMailSender.send(simpleMailMessage);
        System.out.println("邮箱"+verifierMail+"发送完毕 ");
        MailCode mailCode = new MailCode(verifierMail, code, new Date());
        mailCode.insertOrUpdate();
    }

    public boolean verifierMailCode(String verifierMail,String code){
        if (!CommonUtils.haveValue(verifierMail,code)) return false;
        MailCode mailCode = mailCodeMapper.selectById(verifierMail);
        if (mailCode == null) return false;
        return code.equals(String.valueOf(mailCode.getCode()));
    }
}
