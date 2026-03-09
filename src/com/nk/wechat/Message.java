package com.nk.wechat;

/**
 * Created by zhangyuyang1 on 2017/1/17.
 */
public class Message {
    private String touser;
    private String msgtype;
    private Integer agentid;
    private MessageInfo text;
    private Integer safe;

    public Message() {
    }

    public Message(Integer safe, String touser, String toparty, String totag, String msgtype, Integer agentid, MessageInfo text) {
        this.safe = safe;
        this.touser = touser;
        this.msgtype = msgtype;
        this.agentid = agentid;
        this.text = text;
    }

    public String getTouser() {
        return touser;
    }

    public void setTouser(String touser) {
        this.touser = touser;
    }


    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public Integer getAgentid() {
        return agentid;
    }

    public void setAgentid(Integer agentid) {
        this.agentid = agentid;
    }

    public MessageInfo getText() {
        return text;
    }

    public void setText(MessageInfo text) {
        this.text = text;
    }

    public Integer getSafe() {
        return safe;
    }

    public void setSafe(Integer safe) {
        this.safe = safe;
    }
}
