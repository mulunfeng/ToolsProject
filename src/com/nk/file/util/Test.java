package com.nk.file.util;

import java.io.File;
import java.io.IOException;

/**
 * Created by zhangyuyang1 on 2016/11/16.
 */
public class Test {
    public static void main(String[] args) throws IOException {
        File file = FileOperate.createFile("D:/123.txt");
        System.out.println(file.exists());
        FileOperate.string2File("我是要写入的内容","D:/123.txt");
        System.out.print(FileOperate.file2String("D:/123.txt"));
        try {
            FileOperate.lockFile("D:/123.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
