package com.nk.kafka.utils;


import org.apache.commons.lang3.StringUtils;

public class StringUtilsExt extends StringUtils {

    public static String merge(String... strings){

        StringBuilder stringBuilder = new StringBuilder();

        if(null == strings){
            return stringBuilder.toString();
        }

        for(String str : strings){
            stringBuilder.append(str);
        }

        return stringBuilder.toString();
    }
}
