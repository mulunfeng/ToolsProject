package com.nk.util;

import com.nk.common.Balance;
import com.nk.common.constant.State;

import java.util.List;

/**
 * 负载均衡算法工具类.
 */
public class Loadbalances {

    /**
     * 随机权重算法
     *
     * @param transports 通道
     * @return 选择的通道
     */
    public static final <T extends Balance> T randomWeight(final List<T> transports) {
        // 根据连接状态计算权重
        if (transports == null) {
            return null;
        }
        int size = transports.size();
        if (size == 0) {
            return null;
        } else if (size == 1) {
            return transports.get(0);
        }
        int[] weights = new int[size];
        int weight = 0;
        int index = 0;
        // 遍历所有通道计算权重
        for (Balance transport : transports) {
            weights[index] = transport.getWeight();
            if (weights[index] < 0) {
                weights[index] = 0;
            }
            weight += weights[index];
            index++;
        }
        if (weight > 0) {
            // 计算随机权重，返回在[1,weight]之间
            int random = (int) (Math.random() * weight) + 1;
            weight = 0;
            // 根据权重找到连接
            for (int i = 0; i < weights.length; i++) {
                weight += weights[i];
                if (random <= weight) {
                    T transport = transports.get(i);
                    if(transport.getState() == State.CONNECTED){
                        return transport;
                    }
                }
            }
        }
        // 没有一个可用的通道，则随机选择一个
        return transports.get((int) (Math.random() * size));
    }

}
