package com.nk.interviews;

/**
 * Created by zhangyuyang1 on 2016/1/21.
 *  互斥条件
 *  请求与保持条件
 *  不剥夺条件
 *  循环等待条件
 */
public class DeadLock {
    public static void main(String[] args) {
        Thread t1 = new Thread(new DeadLockThread(true));
        Thread t2 = new Thread(new DeadLockThread(false));
        t1.start();
        t2.start();
    }
}

class DeadLockThread implements Runnable{

    private boolean flag;
    public DeadLockThread(boolean flag){
        this.flag = flag;
    }
    @Override
    public void run() {
        while (true) {
            if (flag) {
                synchronized (Lock.locka) {
                    System.out.println("true lock a!");
                    synchronized (Lock.lockb) {
                        System.out.println("true lock b!");
                    }
                }
            } else {
                synchronized (Lock.lockb) {
                    System.out.println("false lock b!");
                    synchronized (Lock.locka) {
                        System.out.println("false lock a!");
                    }
                }
            }
        }
    }
}

class Lock
{
    static Object locka=new Object();
    static Object lockb=new Object();
}

