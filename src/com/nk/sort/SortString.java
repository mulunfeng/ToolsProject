package com.nk.sort;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangyuyang on 2017/7/10.
 */
public class SortString {
    public static void main(String[] args) {
        String str = "a a a man man take take take minus minus minus minus ";
        System.out.println(getSortList(str));
    }

    public static List<String> getSortList(String str) {
        Map<String , Integer> map = new HashMap<String, Integer>();
        for (String s : str.split(" ")) {
            if (map.containsKey(s)) {
                map.put(s, map.get(s) + 1);
            } else {
                map.put(s, 1);
            }
        }
        Map.Entry[] arr = new Map.Entry[4];
        for (Map.Entry entry : map.entrySet()) {
            for (int j = 0 ; j < arr.length ;j++) {
                if (arr[j] == null) {
                    arr[j] = entry;
                    break;
                }
                if ((Integer)arr[j].getValue() < (Integer)entry.getValue()) {
                    for (int m = arr.length - 1; m > j;m--) {
                        arr[m] = arr[m-1];
                    }
                    arr[j] = entry;
                    break;
                }
            }
        }
        List<String> list = new ArrayList<String>();
        for (Map.Entry e : arr) {
            list.add((String) e.getKey());
        }
        return list;
    }
}
