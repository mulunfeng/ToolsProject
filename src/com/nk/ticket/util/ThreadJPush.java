package com.nk.ticket.util;

import cn.jpush.api.JPushClient;
import cn.jpush.api.common.resp.APIConnectionException;
import cn.jpush.api.common.resp.APIRequestException;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ThreadJPush implements Runnable {

	private static Logger logger = LoggerFactory.getLogger(ThreadJPush.class);

	private JPushClient jpushClient = null;
	private List<String> idRegisters;
	private String title;
	private String content;

	public ThreadJPush(List<String> idRegisters, String title, String content, JPushClient jpushClient) {
		super();
		this.idRegisters = idRegisters;
		this.title = title;
		this.content = content;
		this.jpushClient = jpushClient;
	}

	@Override
	public void run() {
		PushPayload payload = PushPayload
				.newBuilder()
				.setPlatform(Platform.android_ios())
				.setAudience(Audience.registrationId(idRegisters))
				.setNotification(
						Notification.newBuilder().addPlatformNotification(IosNotification.newBuilder().setAlert(content).addExtra("tilte", title).build())
								.addPlatformNotification(AndroidNotification.newBuilder().setAlert(content).setTitle(title).build()).build())
				.setOptions(Options.newBuilder().setApnsProduction(true).build()).build();
		sendingPush(payload);
	}

	private void sendingPush(PushPayload payload) {
		try {
			PushResult result = jpushClient.sendPush(payload);
			logger.info("Got result - " + result);
		} catch (APIConnectionException e) {
			logger.error("Connection error. Should retry later. ", e);
		} catch (APIRequestException e) {
			logger.error("Error response from JPush server. Should review and fix it. ", e);
			logger.info("HTTP Status: " + e.getStatus());
			logger.info("Error Code: " + e.getErrorCode());
			logger.info("Error Message: " + e.getErrorMessage());
			logger.info("Msg ID: " + e.getMsgId());
		}
	}

	public List<String> getIdRegisters() {
		return idRegisters;
	}

	public void setIdRegisters(List<String> idRegisters) {
		this.idRegisters = idRegisters;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
