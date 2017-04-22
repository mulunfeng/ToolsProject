package com.nk.lock;

import com.nk.excel.util.StringUtil;
import com.nk.redis.IRedisClient;
import com.nk.redis.RedisClientFactory;

/**
 * Created by liuyang on 2017/4/20.
 */
public class Service {
    private static IRedisClient redisClient = null;

    static {
        redisClient = RedisClientFactory.createRedisClient("127.0.0.1", 6379);
    }

    RedisLock lock = new RedisLock(redisClient);
    int n = 500;

    public void seckill() {
        // 返回锁的value值，供释放锁时候进行判断
        String indentifier = lock.lockWithTimeout("resource", 5000, 1000);
        System.out.println(Thread.currentThread().getName() + "获得了锁" + indentifier);
        if (StringUtil.isEmpty(indentifier)) {
            System.out.println("获取锁超时");
            return;
        }
        System.out.println(--n);
        lock.releaseLock("resource", indentifier);
    }
}