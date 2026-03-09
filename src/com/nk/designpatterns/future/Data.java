package com.nk.designpatterns.future;

/**
 * Created by zhangyuyang1 on 2016/11/9.
 */
public interface Data {
    String getResult() throws InterruptedException;
}
