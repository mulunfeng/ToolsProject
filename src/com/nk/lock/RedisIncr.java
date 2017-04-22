package com.nk.lock;

import com.nk.excel.util.DateUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisException;

import java.util.List;

/**
 * Created by zhangyuyang1 on 2017/2/22.
 * 代码实现redis原子自增incr功能
 */
public class RedisIncr {

    public static void main(String[] args) {
        JedisPoolConfig config = new JedisPoolConfig();
        // 设置最大连接数
        config.setMaxTotal(200);
        // 设置最大空闲数
        config.setMaxIdle(8);
        // 设置最大等待时间
        config.setMaxWaitMillis(1000 * 100);
        // 在borrow一个jedis实例时，是否需要验证，若为true，则所有jedis实例均是可用的
        config.setTestOnBorrow(true);
        JedisPool jedisPool =new JedisPool(config, "127.0.0.1", 6379, 3000);
        for (int i = 0;i < 10;i++) {
            new Thread(new RedisIncrRun(jedisPool, "incr")).start();//并发覆盖写入脏数据
//            new Thread(new RedisIncrRun1(jedisPool, "incr")).start();//线程安全，但存在分布式写脏数据隐患
//            new Thread(new RedisIncrRun2(jedisPool, "incr")).start();//线程安全，分布式安全
        }
    }
}

class RedisIncrRun implements Runnable{
    private JedisPool jedisPool;
    private String key;
    public RedisIncrRun(JedisPool jedisPool, String key) {
        this.jedisPool = jedisPool;
        this.key = key;
    }

    @Override
    public void run() {
        //非同步线程会出现数据脏写入
        System.out.println("-----");
        Jedis conn = jedisPool.getResource();
        try {
            Integer id = Integer.valueOf(conn.get(key));
            System.out.println(id++);
            conn.set(key, String.valueOf(id));
        } catch (JedisException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.close();
            }
        }

    }
}

class RedisIncrRun1 implements Runnable{
    private JedisPool jedisPool;
    private String key;
    public RedisIncrRun1(JedisPool jedisPool, String key) {
        this.jedisPool = jedisPool;
        this.key = key;
    }

    @Override
    public void run() {
        //同步代码块关键字只可以同步本实例的，如果是分布式的项目也会出现脏写入
        synchronized (DateUtils.Format_Date) {
            System.out.println("-----");
            Jedis conn = jedisPool.getResource();
            try {
                Integer id = Integer.valueOf(conn.get(key));
                System.out.println(id++);
                conn.set(key, String.valueOf(id));
            } catch (JedisException e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.close();
                }
            }
        }
    }
}
class RedisIncrRun2 implements Runnable{
    private JedisPool jedisPool;
    private String key;
    public RedisIncrRun2(JedisPool jedisPool, String key) {
        this.jedisPool = jedisPool;
        this.key = key;
    }

    @Override
    public void run() {
        //一方面做好单实例的线程安全，另一方面避免分布式的脏数据写入
        synchronized (DateUtils.Format_Date) {
            System.out.println("RedisIncrRun2-----");
            Jedis conn = jedisPool.getResource();
            try {
                while (true) { // 监视lock，准备开始事务
                    conn.watch(key);
                    Integer id = Integer.valueOf(conn.get(key));
                    Transaction transaction = conn.multi();
                    System.out.println(id++);
                    transaction.set(key, String.valueOf(id));
                    List<Object> results = transaction.exec();
                    if (results == null || results.size() == 0) {
                        continue;
                    }
                    conn.unwatch();
                    break;
                }
            } catch (JedisException e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.close();
                }
            }
        }
    }
}
