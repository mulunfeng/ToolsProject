package com.nk.ticket;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.type.TypeReference;
import org.json.JSONArray;
import org.json.JSONObject;

import com.nk.ticket.model.Ticket;
import com.nk.ticket.util.HttpsUtil;
import com.nk.ticket.util.JsonUtils;

public class TicketPageUtil {

	private static final String URI = "https://kyfw.12306.cn/otn/lcxxcx/query?purpose_codes=ADULT&queryDate=2016-05-06&from_station=BJP&to_station=ZZF";
	public static final boolean flag = true;
	public static void main(String[] args) throws IllegalStateException, IOException {
		new Thread(new Runnable() {
			int i=0;
			@Override
			public void run() {
				while(flag){
					System.out.println("---------------"+(i++)+"次查询-----------------------");
					String newString = new String(HttpsUtil.getMethod(URI));
					Map<String,Object> map = JsonUtils.toMap(newString);
					JSONObject trainsStr = (JSONObject) map.get("data");
					if(trainsStr!=null){
						Map<String,Object> trainMap = JsonUtils.toMap(trainsStr.toString());
						JSONArray trainListStr = (JSONArray) trainMap.get("datas");
						if(trainListStr!=null){
							List<Ticket> list = (List<Ticket>)JsonUtils.stringToObj(trainListStr.toString(), new TypeReference<List<Ticket>>(){});
							if(list!=null){
								for(Ticket ticket:list){
									if(ticket.getStation_train_code().equals("1303")){
										System.out.println("1303次车还剩"+ticket.getYw_num()+"张");
										break;
									}
								}
							}
						}
					}
//					try {
//						Thread.sleep(2 * 1000);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
				}
			}
		}).start();
	}
}
