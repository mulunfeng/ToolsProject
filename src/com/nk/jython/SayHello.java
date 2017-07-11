package com.nk.jython;

/**
 * Created by zhangyuyang on 2017/7/11.
 */
public class SayHello {
    private String userName;

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public void say(int i)
    {
        System.out.println(i + ":Hello " + userName);
    }
}
