package com.nk.securityfile;

/**
 * Created by zhangyuyang1 on 2017/3/23.
 */
public class ByteDistube {

    public static byte[] disturbByte(byte[] bytes) {
        if (bytes == null)
            return null;

        byte[] ins = new byte[bytes.length];
        for (int i = 0 ; i< bytes.length ; i++) {
            ins[i] = distube(bytes[i], i);
        }
        return ins;
    }

    public static byte[] unDisturbByte(byte[] bytes) {
        byte[] ins = new byte[bytes.length];
        for (int i = 0 ; i< bytes.length ; i++) {
            ins[i] = undistube(bytes[i], i);
        }
        return ins;
    }

    private static byte distube(byte b, int i) {
        b = (byte) (3*i + 7 + b);
        return b;
    }

    private static byte undistube(byte dis, int i) {
        return (byte) (dis - (3*i + 7));
    }

}
