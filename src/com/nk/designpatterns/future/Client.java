package com.nk.designpatterns.future;

/**
 * Created by zhangyuyang1 on 2016/11/9.
 */
public class Client {
    /**
     * 模拟客户端远程请求数据
     * @param str
     * @return
     */
    public Data request(final String str) {
        final FutureData futureData = new FutureData();

        new Thread(new Runnable() {
            @Override
            public void run() {
                //RealData的构建很慢，所以放在单独的线程中运行
                RealData realData = new RealData(str);
                futureData.setRealData(realData);
            }
        }).start();

        return futureData; //先直接返回FutureData
    }
}
