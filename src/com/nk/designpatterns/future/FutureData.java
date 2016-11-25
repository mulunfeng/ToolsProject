package com.nk.designpatterns.future;

/**
 * Created by zhangyuyang1 on 2016/11/9.
 */
public class FutureData implements Data {
    RealData realData = null; //FutureData是RealData的封装
    boolean isReady = false;  //是否已经准备好

    public synchronized void setRealData(RealData realData) {
        if(isReady)
            return;
        this.realData = realData;
        isReady = true;
        notifyAll(); //RealData已经被注入到FutureData中了，通知getResult()方法
    }

    @Override
    public synchronized String getResult() throws InterruptedException {
        if(!isReady) {
            wait(); //一直等到RealData注入到FutureData中
        }
        return realData.getResult();
    }
}
