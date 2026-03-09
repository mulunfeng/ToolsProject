package com.nk.ticket.util;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JsonUtils {
	
	private static Logger logger = LoggerFactory.getLogger(JsonUtils.class);
	
	/**
	 * 将bean、List、Map、Array转成Json字符串
	 * @param obj bean、List、Map、Array
	 * @return json 字符串
	 */
	public static String objToString(Object obj){
		String json = "";
		ObjectMapper mapper = new ObjectMapper();
		try {
			json = mapper.writeValueAsString(obj);
		} catch (Exception e) {
			logger.error("生成JSON字符串出错"+obj.getClass().getName(),e);
			json = "";
		}
		return json;
	}
	
	
	public static <T> T stringToObj(String json, Class<T> clazz){
		ObjectMapper mapper = new ObjectMapper();
		JsonFactory factory = new JsonFactory(mapper);
		mapper.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
		mapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
		try {
			JsonParser jsonParser = factory.createJsonParser(json);
			return jsonParser.readValueAs(clazz);
		} catch (Exception e) {
			logger.error(""+e.getMessage(), e);
		}
		return null;
	}	
	
	public static <T> T stringToObj(String json, TypeReference<T> typeReference){
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
		mapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
		JsonFactory factory = new JsonFactory(mapper);
		try {
			JsonParser jsonParser = factory.createJsonParser(json);
			return jsonParser.readValueAs(typeReference);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}
	
	 public static Map<String, Object> toMap(String jsonString) throws JSONException {

	        JSONObject jsonObject = new JSONObject(jsonString);
	        
	        Map<String, Object> result = new HashMap<String, Object>();
	        Iterator<?> iterator = jsonObject.keys();
	        String key = null;
	        Object value = null;
	        
	        while (iterator.hasNext()) {

	            key = (String) iterator.next();
	            value = jsonObject.get(key);
	            result.put(key, value);

	        }
	        return result;
	  }
	
}
