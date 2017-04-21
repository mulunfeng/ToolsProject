package com.nk.wechat;

/**
 * Created by zhangyuyang1 on 2017/1/17.
 */
public class MessageInfo {
    private String content;

    public MessageInfo() {
    }

    public MessageInfo(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
