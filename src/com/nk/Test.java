package com.nk;

import com.nk.jedis.JedisUtil;
import org.apache.commons.lang.StringUtils;

import java.util.*;

import static oracle.net.aso.C00.i;

/**
 * Created by zhangyuyang1 on 2016/6/7.
 */
public class Test {
    private static boolean flag  = false;
    public static void main(String[] args) throws InterruptedException {
//        new Thread(new MethodTime(1)).start();
//        new Thread(new MethodTime(2)).start();
        System.out.println("--------------");
        System.out.println("0000000000010");
        assert true;
        System.out.println("0000000000002");
        System.out.println("0000000000003");

        String uri = "http://ip:port/interfaceId/alias/methodName";
        String[] end = uri.split("/");
        byte resultLength = 3;
        String[] result = new String[resultLength];
        System.arraycopy(end, 1, result, 0, resultLength);
        System.out.println(result);
    }

    static class MethodTime implements Runnable{
        static ThreadLocal<Integer> i = new ThreadLocal<Integer>();

        public MethodTime(int i){
            this.i.set(i);
        }
        @Override
        public void run() {
            while (true){
                System.out.println(this.i.get()+"开始执行了");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void testPlus() {
        String s = "";
        long ts = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            s = s + String.valueOf(i);
        }
        long te = System.currentTimeMillis();
        System.out.println("testPlus"+(te - ts));
    }
    public static void testConcat() {
        String s = "";
        long ts = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            s = s.concat(String.valueOf(i));
        }
        long te = System.currentTimeMillis();
        System.out.println("testConcat"+(te - ts));
    }
    public static void testJoin() {
        List<String> list = new ArrayList<String>();
        long ts = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            list.add(String.valueOf(i));
        }
        StringUtils.join(list, "");
        long te = System.currentTimeMillis();
        System.out.println("testJoin"+(te - ts));
    }
    public static void testStringBuffer() {
        StringBuffer sb = new StringBuffer();
        long ts = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            sb.append(String.valueOf(i));
        }
        sb.toString();
        long te = System.currentTimeMillis();
        System.out.println("testStringBuffer"+(te - ts));
    }
    public static void testStringBuilder() {
        StringBuilder sb = new StringBuilder();
        long ts = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            sb.append(String.valueOf(i));
        }
        sb.toString();
        long te = System.currentTimeMillis();
        System.out.println("testStringBuilder"+(te - ts));
    }
}
