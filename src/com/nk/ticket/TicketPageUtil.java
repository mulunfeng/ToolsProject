package com.nk.ticket;

import com.nk.email.send.SendEmailUtil;
import com.nk.excel.util.DateUtils;
import com.nk.excel.util.StringUtil;
import com.nk.ticket.model.Ticket;
import com.nk.ticket.util.HttpsUtil;
import com.nk.ticket.util.JPushHelper;
import com.nk.ticket.util.JsonUtils;
import org.codehaus.jackson.type.TypeReference;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 车票监控
 */
public class TicketPageUtil {

	private static final List<String> URILIST = new ArrayList<String>(){{add("https://kyfw.12306.cn/otn/lcxxcx/query?purpose_codes=ADULT&queryDate=2016-10-01&from_station=BJP&to_station=WNY");}};
	private static Map<String, String> TRAINLIST = new HashMap<String, String>();
	static {
//		TRAINLIST.put("G89", "G89");
//		TRAINLIST.put("G307", "G307");
//		TRAINLIST.put("G81", "G81");
//		TRAINLIST.put("G83", "G83");
	}
	private static final List<String> TIMERANGE = new ArrayList<String>(){{add("07:00");add("18:00");}};
	public static final boolean flag = true;
	public static void main(String[] args) throws IllegalStateException, IOException {
		final List<String> strs = new ArrayList<String>();
		strs.add("0803fa7bbd5");
		
		for(final String url:URILIST){
			new Thread(new Runnable() {
				int i=0;
				@Override
				public void run() {
					Map<String, String> checkMap = new HashMap<String, String>();
					while(flag){
						System.out.println("---------------"+(i++)+"'s check-----------------------");
						try {
							String newString = new String(HttpsUtil.getMethod(url));
							Map<String,Object> map = JsonUtils.toMap(newString);
							JSONObject trainsStr = (JSONObject) map.get("data");
							if(trainsStr!=null){
								Map<String,Object> trainMap = JsonUtils.toMap(trainsStr.toString());
								JSONArray trainListStr = (JSONArray) trainMap.get("datas");
								if(trainListStr!=null){
									List<Ticket> list = JsonUtils.stringToObj(trainListStr.toString(), new TypeReference<List<Ticket>>(){});
									if(list!=null){
										String msg = "";
										for(Ticket ticket:list){
											//时间过滤
											if (TIMERANGE!=null && TIMERANGE.size() == 2) {
												if ("24:00".equals(ticket.getStart_time()) || DateUtils.compareTime(ticket.getStart_time(), TIMERANGE.get(0)) < 0
														|| DateUtils.compareTime(ticket.getStart_time(), TIMERANGE.get(1)) > 0) {
													continue;
												}
											}
											//车次过滤
											if (TRAINLIST != null && TRAINLIST.size() >0){
												if (!TRAINLIST.containsKey(ticket.getStation_train_code())) {
													continue;
												}
											}
											String need = ticket.getStation_train_code().startsWith("G")?ticket.getZe_num():ticket.getYw_num();
											System.out.print(ticket.getStation_train_code()+" surplus of "+need +" ");
											if (checkMap.get(ticket.getStation_train_code()) == null) {
												checkMap.put(ticket.getStation_train_code(), need);
											}
											if(!checkMap.get(ticket.getStation_train_code()).equals(need)){
												System.out.println("send msg!!");
												checkMap.put(ticket.getStation_train_code(), need);
												msg += "车票提醒"+ ticket.getStation_train_code()+"次车还剩"+ticket.getZe_num()+"张 ";
//														JPushHelper.getInstance().pushRegistration(strs, "车票提醒", train+"次车还剩"+ticket.getYw_num()+"张");
											}
										}
										if (StringUtil.isNotEmpty(msg))
											SendEmailUtil.sendEmal("1015947808@qq.com", "车票提醒", msg);
										System.out.println("");
									}
								}
							}
							try {
								Thread.sleep(500);
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
