package com.nk.ticket;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.type.TypeReference;
import org.json.JSONArray;
import org.json.JSONObject;

import com.nk.ticket.model.Ticket;
import com.nk.ticket.util.HttpsUtil;
import com.nk.ticket.util.JPushHelper;
import com.nk.ticket.util.JsonUtils;

/**
 * 车票监控
 */
public class TicketPageUtil {

	private static final List<String> URILIST = new ArrayList<String>(){{add("https://kyfw.12306.cn/otn/lcxxcx/query?purpose_codes=ADULT&queryDate=2016-06-11&from_station=NUH&to_station=BJP");}};  
	private static final List<String> TRAINLIST = new ArrayList<String>(){{add("Z52");}};  
	public static final boolean flag = true;
	public static void main(String[] args) throws IllegalStateException, IOException {
		final List<String> strs = new ArrayList<String>();
		strs.add("0803fa7bbd5");
		
		for(final String url:URILIST){
			new Thread(new Runnable() {
				int i=0;
				@Override
				public void run() {
					String ywNum = "";
					while(flag){
						System.out.println("---------------"+(i++)+"'s check-----------------------");
						String newString = null;
						try {
							newString = new String(HttpsUtil.getMethod(url));
							Map<String,Object> map = JsonUtils.toMap(newString);
							JSONObject trainsStr = (JSONObject) map.get("data");
							if(trainsStr!=null){
								Map<String,Object> trainMap = JsonUtils.toMap(trainsStr.toString());
								JSONArray trainListStr = (JSONArray) trainMap.get("datas");
								if(trainListStr!=null){
									List<Ticket> list = (List<Ticket>)JsonUtils.stringToObj(trainListStr.toString(), new TypeReference<List<Ticket>>(){});
									if(list!=null){
										for(Ticket ticket:list){
											for(String train:TRAINLIST){
												if(ticket.getStation_train_code().equals(train)){
													System.out.println(train+" surplus of "+ticket.getYw_num()+"");
													if(!ywNum.equals(ticket.getYw_num())){
														System.out.println("send msg!!");
														ywNum = ticket.getYw_num();
														JPushHelper.getInstance().pushRegistration(strs, "车票提醒", train+"次车还剩"+ticket.getYw_num()+"张");
													}
													break;
												}
											}
										}
									}
								}
							}
							try {
								Thread.sleep(2 * 5000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						} catch (Exception e1) {
							JPushHelper.getInstance().pushRegistration(strs, "程序异常", e1.getMessage());
							e1.printStackTrace();
						}
					}
				}
			}).start();
		}
	}
}
