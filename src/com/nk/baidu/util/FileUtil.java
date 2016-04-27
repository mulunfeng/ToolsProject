package com.nk.baidu.util;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtil {
	/**
	 * 
	* @Description: 写入到文件
	* @Title: writeToFile 
	* @param path
	* @param data void
	* @throws
	* @date 2016年4月27日 下午4:41:04
	 */
	public static void writeToFile(String path,String data){
		BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(path,true));
            bufferedWriter.write(data);
            bufferedWriter.newLine();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.flush();
                    bufferedWriter.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
	}
}
