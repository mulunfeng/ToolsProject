package com.nk.lock.staticlock;

import java.util.Random;

/**
 * Created by zhangyuyang on 2016/3/29.
 * 静态方法锁与非静态方法锁无竞争关系
 */
public class StaticLock {
    public static void main(String[] args) {
        Lock lock= new Lock();
        Thread t = new Thread(new TT(lock,true));
        t.start();
        Thread t1 = new Thread(new TT(lock,false));
        t1.start();
    }
}

class TT implements Runnable{
    private Lock lock;
    private boolean flag = true;
    public TT(Lock a,boolean flag) {
        lock = a;
        this.flag = flag;
    }

    @Override
    public void run() {
        if (flag) {
            try {
                Random r = new Random(100);
                lock.m2(r.nextInt());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            try {
                Lock.m1();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}