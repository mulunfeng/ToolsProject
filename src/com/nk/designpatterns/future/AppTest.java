package com.nk.designpatterns.future;

/**
 * Created by zhangyuyang1 on 2016/11/9.
 */
public class AppTest {
    public static void main(String[] args) throws InterruptedException {
        Client client = new Client();
        //这里会立即返回，因为获取的是FutureData，而非RealData
        for (int i=0;i<100;i++){
            final Data data = client.request("name" + i);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println("数据="+data.getResult());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        }
        //这里可以用一个sleep代替对其他业务逻辑的处理
        //在处理这些业务逻辑过程中，RealData也正在创建，从而充分了利用等待时间
//        Thread.sleep(2000);
        //使用真实数据
    }
}
