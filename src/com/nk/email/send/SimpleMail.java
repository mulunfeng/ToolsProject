package com.nk.email.send;

/**
 * Created by zhangyuyang1 on 2016/9/5.
 */
public class SimpleMail {
    private String subject;
    private String content;

    public SimpleMail(String subject, String content) {
        this.subject = subject;
        this.content = content;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
