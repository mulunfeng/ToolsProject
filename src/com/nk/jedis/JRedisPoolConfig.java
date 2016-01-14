package com.nk.jedis;

public class JRedisPoolConfig {
	public static String REDIS_IP = "127.0.0.1";
	public static int REDIS_PORT = 6379;
	public static String REDIS_PASSWORD = "";
	public static int MAX_ACTIVE = 1024;
	public static int MAX_IDLE = 200;
	public static long MAX_WAIT = 1000;
	public static boolean TEST_ON_BORROW = true;
	public static boolean TEST_ON_RETURN = true;

}
