package com.nk.lock;

/**
 * Created by zhangyuyang1 on 2017/4/22.
 */
public class ThreadA extends Thread {
    private Service service;

    public ThreadA(Service service) {
        this.service = service;
    }

    @Override
    public void run()

    {
        service.seckill();
    }
}

class Test {
    public static void main(String[] args) {
        Service service = new Service();
        for (int i = 0; i < 50; i++) {
            ThreadA threadA = new ThreadA(service);
            threadA.start();
        }
    }
}
