package com.nk.log;

import com.nk.excel.util.DateUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by zhangyuyang1 on 2017/1/20.
 * 日志定位
 */
public class LogLocation {
    public static void main(String[] args) throws Exception {

        final File tmpLogFile = new File("E:/catalina.out");
        final RandomAccessFile randomFile = new RandomAccessFile(tmpLogFile, "rw");
        long seek = getLogLocationByTime(tmpLogFile, "2017-03-21 20:39:44");
        randomFile.seek(seek);
        String line;
        int i = 0;
        while ((line = randomFile.readLine()) != null && i++ < 40) {
            System.out.println(line);
        }
        randomFile.close();

    }


    /**
     * 指定日志文件寻找日期的定位点
     *
     * @param logFile
     * @param dateTime
     * @return
     * @throws IOException
     */
    public static Long getLogLocationByTime(File logFile, String dateTime) throws IOException {
        final RandomAccessFile randomFile = new RandomAccessFile(logFile, "rw");
        long low = 0, mid = 0, high = randomFile.length();
        while (low <= high) {
            mid = (low + high) / 2;
            String midDateTime = getDateTime(randomFile, mid);
            if (midDateTime == null) { //超总长度没有获取到日期
                return low;
            } else if (DateUtils.compareDateTime(midDateTime, dateTime) == 0 || mid >= high || mid <= low) { //小于最小或没有日期值
                return mid;
            } else if (DateUtils.compareDateTime(midDateTime, dateTime) > 0) {
                high = mid;
            } else if (DateUtils.compareDateTime(midDateTime, dateTime) < 0) {
                low = mid;
            }
        }
        randomFile.close();
        return mid;
    }

    /**
     * 跳过指定字符找日期
     *
     * @param randomFile
     * @param mid
     * @return
     * @throws IOException
     */
    private static String getDateTime(RandomAccessFile randomFile, long mid) throws IOException {
        randomFile.seek(mid);
        String line;
        String logTime = null;
        while ((line = randomFile.readLine()) != null) {
            if ((logTime = getDateTime(line)) != null) {
                break;
            }
        }
        return logTime;
    }

    /**
     * 判断日期
     *
     * @param line
     * @return
     */
    private static String getDateTime(String line) {
        if (StringUtils.isBlank(line) || line.length() <= 19)
            return null;

        if (DateUtils.isDateTime(line.substring(0, 19))) {
            return line.substring(0, 19);
        }
        return null;
    }
}
