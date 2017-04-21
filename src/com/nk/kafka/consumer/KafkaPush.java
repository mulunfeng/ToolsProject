package com.nk.kafka.consumer;

import com.alibaba.fastjson.JSON;
import com.nk.kafka.producer.Producer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangyuyang1 on 2016/12/19.
 */
public class KafkaPush {
    private final static int size = 10;
    private final static KafkaPush instance = new KafkaPush();

    public final static KafkaPush getInstance() {
        return instance;
    }

    /**
     * 缓冲
     *
     * @param list
     * @param topic
     * @param code
     * @param isAsync
     * @param <T>
     */
    public <T> void push(List<T> list, String topic, String code, boolean isAsync) {
        if (list == null || list.size() == 0)
            return;
        int listSize = list.size();
        int bath = 1;
        if (listSize > size) {
            if (listSize % size == 0) {
                bath = listSize / size;
            } else {
                bath = listSize / size + 1;
            }
        }

        int index = 0;
        for (int i = 0; i < bath; i++) {
            List<T> ll = list.subList((index * size), (index + 1) * size > listSize ? listSize : (index + 1) * size);
            index++;
            Producer.getInstance().run(topic, isAsync, code + index, JSON.toJSONString(ll));
        }
    }

    public <T> void push(String topic, boolean isAsync, String code, List<T> productList) {
        KafkaPush.getInstance().push(productList, topic, code, isAsync);
    }

    public static void main(String[] args) {
        List<String> str = new ArrayList<String>();
        str.add("1");
        str.add("2");
        str.add("3");
        str.add("4");
        str.add("5");
        str.add("6");
        str.add("7");
        str.add("8");
        Consumer.getInstance().run("test_topic");
        KafkaPush.getInstance().push(str, "test_topic", "test", true);
    }
}
