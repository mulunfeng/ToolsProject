package com.nk.lock;

import com.nk.redis.IRedisClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisException;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Created by zhangyuyang on 2016/4/20.
 */
public class RedisLock {
    private final IRedisClient redisClient;

    public RedisLock(IRedisClient redisClient) {
        this.redisClient = redisClient;
    }

    /**
     * 加锁 * @param locaName 锁的key * @param acquireTimeout 获取超时时间 * @param timeout 锁的超时时间 * @return 锁标识
     */
    public String lockWithTimeout(String locaName, long acquireTimeout, long timeout) {
        String retIdentifier = null;
        try {
            // 获取连接
            // 随机生成一个value
            String identifier = UUID.randomUUID().toString();
            // 锁名，即key值
            String lockKey = "lock:" + locaName;
            // 超时时间，上锁后超过此时间则自动释放锁
            int lockExpire = (int) (timeout / 1000);
            // 获取锁的超时时间，超过这个时间则放弃获取锁
            long end = System.currentTimeMillis() + acquireTimeout;
            while (System.currentTimeMillis() < end) {
                if (redisClient.setnx(lockKey, identifier) == 1) {
//                    redisClient.expire(lockKey, lockExpire);
                    // 返回value值，用于释放锁时间确认
                    retIdentifier = identifier;
                    return retIdentifier;
                }
                // 返回-1代表key没有设置超时时间，为key设置一个超时时间
//                if (redisClient.ttl(lockKey) == -1) {
//                    redisClient.expire(lockKey, lockExpire);
//                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        } catch (JedisException e) {
            e.printStackTrace();
        } finally {
            if (redisClient != null) {
                try {
                    redisClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return retIdentifier;
    }

    /**
     * 释放锁 * @param lockName 锁的key * @param identifier 释放锁的标识 * @return
     */
    public boolean releaseLock(String lockName, String identifier) {
        String lockKey = "lock:" + lockName;
        boolean retFlag = false;
        try {
            while (true) { // 监视lock，准备开始事务
                redisClient.watch(lockKey); // 通过前面返回的value值判断是不是该锁，若是该锁，则删除，释放锁
                if (identifier.equals(redisClient.get(lockKey))) {
                    Long results = redisClient.del(lockKey);
                    if (results == null || results != 1) {
                        continue;
                    }
                    retFlag = true;
                }
                redisClient.unwatch();
                break;
            }
        } catch (JedisException e) {
            e.printStackTrace();
        } finally {
            if (redisClient != null) {
                try {
                    redisClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return retFlag;
    }
}