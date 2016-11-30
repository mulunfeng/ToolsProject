package com.nk.db.util;


import com.nk.db.constant.OracleTypeConstant;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 功能概述：转换类名和属性工具类 <br>
 * 创建时间：2013-4-25上午10:36:47 <br>
 * 修改记录： <br>
 * 
 * @author xiaoliang.li <br>
 */
public class ConvertUtil {

	/**
	 * 根据数据库表名获取类名
	 * 
	 * @param tableName
	 * @return
	 */
	public static String getEntityName(String tableName, String tablefix) {
		String t1 = "";
		if(StringUtils.isNotEmpty(tablefix) && StringUtils.isNotEmpty(tablefix.trim())
				&& tableName.toUpperCase().startsWith(tablefix.toUpperCase())){
			int startPoint = tablefix.length();
			int endPoint = tableName.length();
			t1 = tableName.substring(startPoint, endPoint);
		} else {
			t1 = tableName;
		}
		return convertEntity(t1.toLowerCase());
	}

	/**
	 * 根据数据库表字段名获取类的属性名
	 * 
	 * @param fieldName
	 * @return
	 */
	public static String getAttributeName(String fieldName, String columnsfix) {
		String t1 = "";
		if(StringUtils.isNotEmpty(columnsfix) && StringUtils.isNotEmpty(columnsfix.trim())
				&& (fieldName.startsWith(columnsfix.toUpperCase())||fieldName.startsWith(columnsfix.toLowerCase()))){
			int startPoint = columnsfix.length();
			int endPoint = fieldName.length();
			t1= fieldName.substring(startPoint, endPoint);
		}else{
			t1= fieldName;
		}
		
		return convertAttribute(t1);
	}

	/**
	 * 功能：将输入数据库表名转换成类名
	 * 
	 * @param str
	 * @return
	 */
	private static String convertEntity(String str) {
		StringBuffer temp = new StringBuffer();
		if (str.contains("_")) {
			String[] strArry=str.split("_");
			for(int i =0;i<strArry.length;i++){
				String a = strArry[i];
				a = a.replaceFirst(a.substring(0, 1), a.substring(0, 1)
						.toUpperCase());
				temp.append(a);
			}
			
//			int i = str.indexOf("_");
//			String a = str.substring(0, i);
//			String b = str.substring(i + 1, str.length());
//			a = a.replaceFirst(a.substring(0, 1), a.substring(0, 1)
//					.toUpperCase());
//			temp.append(a);
//			b = b.replaceFirst(b.substring(0, 1), b.substring(0, 1)
//					.toUpperCase());
//			temp.append(b);
		} else {
			str = str.replaceFirst(str.substring(0, 1), str.substring(0, 1)
					.toUpperCase());
			temp.append(str);
		}

		return temp.toString();
	}

	/**
	 * 将数据库字段转转为类的驼峰式属性
	 * 
	 * @param str
	 * @return
	 */
	public static String convertAttribute(String str) {
		StringBuffer temp = new StringBuffer();
		str = str.toLowerCase();
		if (str.contains("_")) {
			String[] strArry=str.split("_");
			for(int i =0;i<strArry.length;i++){
				String a = strArry[i];
				if(i==0){
					a = a.replaceFirst(a.substring(0, 1), a.substring(0, 1)
							.toLowerCase());
				}else{
					a = a.replaceFirst(a.substring(0, 1), a.substring(0, 1)
							.toUpperCase());
				}
				
				temp.append(a);
			}
//			int i = str.indexOf("_");
//			String a = str.substring(0, i);
//			String b = str.substring(i + 1, str.length());
//			a = a.replaceFirst(a.substring(0, 1), a.substring(0, 1)
//					.toLowerCase());
//			temp.append(a);
//			// 如果截取的第二个字符串中还包括"_"则再次进行截取
//			if (b.contains("_")) {
//				int j = b.indexOf("_");
//				String c = b.substring(0, j);
//				String d = b.substring(j + 1, b.length());
//				c = c.replaceFirst(c.substring(0, 1), c.substring(0, 1)
//						.toUpperCase());
//				temp.append(c);
//				d = d.replaceFirst(d.substring(0, 1), d.substring(0, 1)
//						.toUpperCase());
//				temp.append(d);
//			} else {
//				b = b.replaceFirst(b.substring(0, 1), b.substring(0, 1)
//						.toUpperCase());
//				temp.append(b);
//			}
		} else {
			str = str.replaceFirst(str.substring(0, 1), str.substring(0, 1)
					.toLowerCase());
			temp.append(str);
		}

		return temp.toString();
	}
	
	/**
	 * 根据属性名获取Get和Set方法的方法名称
	 * @param javaName
	 * @return
	 */
	public static String getGetSetName(String javaName){
		return javaName.replaceFirst(javaName.substring(0, 1), javaName.substring(0, 1)
				.toUpperCase());
	}

	/**
	 * 功能：获得列的数据类型
	 * @param sqlType
	 * @return
	 */
	public static String sqlType2JavaType(String sqlType) {
		if (sqlType.equalsIgnoreCase("bit")) {
			return "boolean";
		} else if (sqlType.equalsIgnoreCase("tinyint")) {
			return "byte";
		} else if (sqlType.equalsIgnoreCase("smallint")) {
			return "short";
		} else if (sqlType.equalsIgnoreCase("int")||sqlType.equalsIgnoreCase("number")) {
			return "Integer";
		} else if (sqlType.equalsIgnoreCase("bigint")||sqlType.equalsIgnoreCase("bigdecimal")) {
			return "Long";
		} else if (sqlType.equalsIgnoreCase("float")) {
			return "float";
		} else if (sqlType.equalsIgnoreCase("decimal")
				|| sqlType.equalsIgnoreCase("numeric")
				|| sqlType.equalsIgnoreCase("real")
				|| sqlType.equalsIgnoreCase("money")
				|| sqlType.equalsIgnoreCase("smallmoney")
				|| sqlType.equalsIgnoreCase("double")) {
			return "double";
		} else if (sqlType.equalsIgnoreCase("varchar2")
				||sqlType.equalsIgnoreCase("varchar")
				|| sqlType.equalsIgnoreCase("char")
				||sqlType.equalsIgnoreCase("nvarchar2")
				|| sqlType.equalsIgnoreCase("nvarchar")
				|| sqlType.equalsIgnoreCase("nchar")
				|| sqlType.equalsIgnoreCase("text")
				|| sqlType.equalsIgnoreCase("clob")) {
			return "String";
		} else if (sqlType.equalsIgnoreCase("date")||sqlType.equalsIgnoreCase("datetime")||sqlType.equalsIgnoreCase("timestamp")) {
			return "Date";
		} else if (sqlType.equalsIgnoreCase("image")) {
			return "Blod";
		} else if (sqlType.equalsIgnoreCase("LONGBLOB")) {
			return "Blob";
		}
		return null;
	}
	
	
	/** 封装oracle数据类型map**/
	public static Map<String,String> oracleDBTypeMap = new HashMap<String, String>(); 
	
	
	public static Map<String, String> getOracleDBTypeMap() {
		return oracleDBTypeMap;
	}

	static{
		oracleDBTypeMap.put(OracleTypeConstant.O_VARCHAR2,"String");
		oracleDBTypeMap.put(OracleTypeConstant.O_NUMBER,"Integer");
		oracleDBTypeMap.put(OracleTypeConstant.O_NUMBER_LONG,"Long");
		oracleDBTypeMap.put(OracleTypeConstant.O_INTEGER,"Integer");
		oracleDBTypeMap.put(OracleTypeConstant.O_DATE, "Date");
		oracleDBTypeMap.put(OracleTypeConstant.O_FLOAT, "float");
		oracleDBTypeMap.put(OracleTypeConstant.O_FLOAT2, "double");
		oracleDBTypeMap.put(OracleTypeConstant.O_LONG, "Long");
		oracleDBTypeMap.put(OracleTypeConstant.O_CLOB,"Clob");
		oracleDBTypeMap.put(OracleTypeConstant.O_BLOB,"byte[]");
		oracleDBTypeMap.put(OracleTypeConstant.O_CHAR,"String");
		oracleDBTypeMap.put(OracleTypeConstant.O_DECIMAL,"double");
		oracleDBTypeMap.put(OracleTypeConstant.O_TIMESTAMP, "TimeStamp");
	}
}
