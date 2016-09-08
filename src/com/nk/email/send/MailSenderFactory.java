package com.nk.email.send;

/**
 * Created by zhangyuyang1 on 2016/9/5.
 */
public class MailSenderFactory {
    /**
     * 服务邮箱
     */
    private static SimpleMailSender serviceSms = null;

    /**
     * 获取邮箱
     *
     * @return 符合类型的邮箱
     */
    public static SimpleMailSender getSender() {
        if (serviceSms == null) {
            serviceSms = new SimpleMailSender("email@126.com",
                    "your password");
        }
        return serviceSms;
    }
}
