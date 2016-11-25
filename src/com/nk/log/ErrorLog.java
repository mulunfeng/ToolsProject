package com.nk.log;

import com.nk.excel.util.DateUtils;
import com.nk.excel.util.StringUtil;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangyuyang1 on 2016/10/11.
 */
public class ErrorLog {

    public static void main(String[] args) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Map<String,Integer> map = new HashMap<String,Integer>();
            StringBuilder sb = new StringBuilder();
            InputStream is = null;
            BufferedReader reader = null;
            try {
                is = new FileInputStream("C:\\Users\\zhangyuyang1\\Desktop\\bug库\\app_error.log");
                // 必须设置成GBK，否则将出现乱码
                reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                String line = "";
                String temp = null;
                while ((line = reader.readLine()) != null) {
//                    System.out.println(line);
                    if (StringUtil.isNotEmpty(line)) {
                        temp = line.substring(0, 19);
                        if (DateUtils.isDateTime(temp)) {
                            String error =line.substring(line.indexOf("ERROR") ,line.indexOf("]", line.indexOf("]")+1)+1);

                            if (map.containsKey(error)) {
                                map.put(error, map.get(error)+1);
                            } else{
                                map.put(error, 1);
                            }
                        }
                    }
                }
                for (Map.Entry<String,Integer> entry : map.entrySet()) {
                    System.out.println(entry.getKey()+":"+entry.getValue());
                }
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }



    }
}
