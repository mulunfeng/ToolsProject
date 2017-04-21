package com.nk.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by zhangyuyang1 on 2016/9/21.
 */
public class RedisClientFactory {

    private static Logger logger = LoggerFactory.getLogger("redis");
    private static int TIME_OUT = 3000;

    private static JedisPoolConfig defaultConfig;

    static {
        defaultConfig = new JedisPoolConfig();
        defaultConfig.setTestOnBorrow(true);
        defaultConfig.setTestOnReturn(true);
        defaultConfig.setTestWhileIdle(true);
        defaultConfig.setMaxTotal(50);
        defaultConfig.setMaxWaitMillis(3000L);
        defaultConfig.setMinIdle(10);
    }

    /**
     * 自己缓存 重对象  需要创建连接池
     *
     * @param host
     * @param port
     * @param auth
     * @return
     */
    public static IRedisClient createRedisClient(String host, int port, String auth) {
        JedisPool jedisPool = new JedisPool(defaultConfig, host, port, TIME_OUT, auth);
        return RedisClientProxyFactory.getProxy(jedisPool);
    }

    public static IRedisClient createRedisClient(String host, int port) {
        JedisPool jedisPool = new JedisPool(defaultConfig, host, port, TIME_OUT);
        return RedisClientProxyFactory.getProxy(jedisPool);
    }

    public static IRedisClient createRedisClient(JedisPoolConfig jedisPoolConfig, String host, int port, String auth) {
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, host, port, TIME_OUT, auth);
        return RedisClientProxyFactory.getProxy(jedisPool);
    }

    public static IRedisClient createRedisClient(JedisPoolConfig jedisPoolConfig, String host, int port, int timeOut) {
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, host, port, timeOut);
        return RedisClientProxyFactory.getProxy(jedisPool);
    }

    private static class RedisClientProxyFactory {
        public static IRedisClient getProxy(final JedisPool jedisPool) {
            return (IRedisClient) Proxy.newProxyInstance(RedisClientProxyFactory.class.getClassLoader(), new Class[]{IRedisClient.class}, new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                    Jedis jedis = null;
                    try {


                        jedis = jedisPool.getResource();
                        return method.invoke(jedis, args);
                    } catch (Exception e) {
                        logger.error("redis operation error", e);
                    } finally {
                        if (jedis != null) {
                            try {
                                jedisPool.returnResource(jedis);
                            } catch (Exception e) {
                                logger.error("return jedis to pool error", e);
                            }
                        }
                    }
                    //if runs here means redis error
                    Class<?> reutrnType = method.getReturnType();
                    return getReturnValue(reutrnType);
                }
            });
        }

        /**
         * 获取默认的返回值
         *
         * @param returnType
         * @return
         */
        private static Object getReturnValue(Class returnType) {
            if (returnType == Boolean.class)
                return Boolean.FALSE;
            if (returnType == Long.class)
                return Long.valueOf(0L);
            if (returnType == Integer.class)
                return Integer.valueOf(0);
            if (returnType == Double.class)
                return Double.valueOf("0");
            return null;
        }
    }
}
