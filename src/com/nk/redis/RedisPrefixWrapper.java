package com.nk.redis;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by zhangyuyang1 on 2016/9/21.
 */
public class RedisPrefixWrapper {

    private IRedisClient iRedisClient;

    public RedisPrefixWrapper(IRedisClient redisClient) {
        this.iRedisClient = redisClient;
    }

    public IRedisClient getRedisClient(final String prefix) {
        return (IRedisClient) Proxy.newProxyInstance(RedisPrefixWrapper.class.getClassLoader(), new Class[]{IRedisClient.class}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (args != null && args.length > 0) {
                    args[0] = prefix + args[0];
                }
                return method.invoke(iRedisClient, args);
            }
        });
    }

}
