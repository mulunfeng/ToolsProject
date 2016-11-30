package com.nk.ticket.util;

import cn.jpush.api.JPushClient;

import java.util.List;

public class JPushHelper {

	private String apiKey = "bd7b611f69ad11c6a3b48a58";
	private String masterSecret = "2b48116e7c078f3906273f0b";

	private JPushClient jpushClient = null;

	private JPushHelper() {
		if (apiKey == null || masterSecret == null) {
			throw new RuntimeException("请初始化极光apiKey/masterSecret.");
		}

		jpushClient = new JPushClient(masterSecret, apiKey, 3);
	}

	private static class SingletonHolder {
		private static final JPushHelper INSTANCE = new JPushHelper();
	}

	public static final JPushHelper getInstance() {
		return SingletonHolder.INSTANCE;
	}

	/**
	 * 向指定注册的手机发送消息。
	 * 
	 * @param idRegisters
	 * @param title
	 * @param content
	 */
	public void pushRegistration(List<String> idRegisters, String title, String content) {
		Thread th = new Thread(new ThreadJPush(idRegisters, title, content, jpushClient));
		th.start();
	}
}
