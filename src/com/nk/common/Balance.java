package com.nk.common;

import com.nk.common.constant.State;

public interface Balance {

    /**
     * 获取连接状态
     *
     * @return 连接状态
     */
    State getState();
    /**
     * 获取权重
     *
     * @return 权重
     */
    int getWeight();

}
