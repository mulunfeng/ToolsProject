package com.nk.email.send;

import javax.mail.MessagingException;

/**
 * Created by zhangyuyang1 on 2016/9/5.
 */
public class SendEmailUtil {
    public static void sendEmal(String address, String title, String content){
        try {
            MailSenderFactory.getSender().send(address, title, content);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
