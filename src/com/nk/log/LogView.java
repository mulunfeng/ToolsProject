package com.nk.log;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhangyuyang1 on 2017/1/5.
 */
public class LogView {
    private long lastTimeFileSize = 0;  //上次文件大小

    /**
     * 实时输出日志信息
     *
     * @param logFile 日志文件
     * @throws IOException
     */
    public void realtimeShowLog(File logFile) throws IOException {
        //指定文件可读可写
        final RandomAccessFile randomFile = new RandomAccessFile(logFile, "rw");
        lastTimeFileSize = randomFile.length();
        //启动一个线程每10秒钟读取新增的日志信息
        ScheduledExecutorService exec =
                Executors.newScheduledThreadPool(1);
        exec.scheduleWithFixedDelay(new Runnable() {
            public void run() {
                try {
                    //获得变化部分的
                    randomFile.seek(lastTimeFileSize);
                    String tmp = "";
                    while ((tmp = randomFile.readLine()) != null) {
                        String log = new String(tmp.getBytes("ISO8859-1"));
                        if (log.contains("0.7")) {
                            System.out.println(log + "----------------------");
                        }
                    }
                    lastTimeFileSize = randomFile.length();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }, 0, 1, TimeUnit.MILLISECONDS);
    }

    public static void main(String[] args) throws Exception {
//        LogView view = new LogView();
        final File tmpLogFile = new File("E:/catalina.out");
//        view.realtimeShowLog(tmpLogFile);
        final RandomAccessFile randomFile = new RandomAccessFile(tmpLogFile,"rw");
//        randomFile.seek(0);
        System.out.println(randomFile.readLine());
        System.out.println(randomFile.readLine());
        System.out.println(randomFile.readLine());
        System.out.println(randomFile.readLine());
        System.out.println(randomFile.readLine());
        System.out.println(randomFile.readLine());
        System.out.println(randomFile.readLine());
        System.out.println(randomFile.readLine());
        System.out.println(randomFile.readLine());
        System.out.println(randomFile.readLine());
        randomFile.close();

    }

}
