package com.nk.lock.staticlock;

/**
 * Created by zhangyuyang on 2016/3/29.
 */
public class Lock {
    private Object object = new Object();
    public synchronized static void m1() throws InterruptedException {
        System.out.println("m1-------------");
        Thread.sleep(3000);
    }

    public synchronized void m2(int i) throws InterruptedException {
        System.out.println(i+"m2-------------");
        Thread.sleep(1000);
    }
}
