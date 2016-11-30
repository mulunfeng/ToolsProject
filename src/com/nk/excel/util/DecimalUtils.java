package com.nk.excel.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;


public class DecimalUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(DecimalUtils.class);

	// 默认除法运算精度
	private static final int DEF_DIV_SCALE = 10;

	/**
	 * 提供精确的加法运算。
	 * @param one 被加数
	 * @param two 加数
	 * @return 两个参数的和
	 */
	public static Double add(Double one, Double two) {
		if(one==null)
			one=0.0;
		if(two==null)
			two=0.0;
		BigDecimal b1 = new BigDecimal(Double.toString(one));
		BigDecimal b2 = new BigDecimal(Double.toString(two));
		return b1.add(b2).doubleValue();
	}
	
	/**
	 * 提供精确的减法运算。
	 * @param one 被减数
	 * @param two  减数
	 * @return 两个参数的差
	 */
	public static Double sub(Double one, Double two) {
		if(one==null)
			one=0.0;
		if(two==null)
			two=0.0;
		BigDecimal b1 = new BigDecimal(Double.toString(one));
		BigDecimal b2 = new BigDecimal(Double.toString(two));
		return b1.subtract(b2).doubleValue();
	}

	/**
	 * 提供精确的乘法运算。
	 * @param one 被乘数
	 * @param two 乘数
	 * @return 两个参数的积
	 */
	public static Double mul(Double one, Double two) {
		if(one==null)
			one=0.0;
		if(two==null)
			two=0.0;
		BigDecimal b1 = new BigDecimal(Double.toString(one));
		BigDecimal b2 = new BigDecimal(Double.toString(two));
		return b1.multiply(b2).doubleValue();
	}

	/**
	 * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到 小数点以后10位，以后的数字四舍五入。
	 * @param one 被除数
	 * @param two 除数
	 * @return 两个参数的商
	 */
	public static Double div(Double one, Double two) {
		if(one==null)
			one=0.0;
		if(two==null)
			two=0.0;
		return div(one, two, DEF_DIV_SCALE);
	}

	/**
	 * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指 定精度，以后的数字四舍五入。
	 * @param one 被除数
	 * @param two  除数
	 * @param scale 表示表示需要精确到小数点以后几位。
	 * @return 两个参数的商
	 */
	public static Double div(Double one, Double two, int scale) {
		if(one==null)
			one=0.0;
		if(two==null)
			two=0.0;
		if (scale < 0) {
			throw new IllegalArgumentException(
					"The scale must be a positive integer or zero");
		}
		BigDecimal b1 = new BigDecimal(Double.toString(one));
		BigDecimal b2 = new BigDecimal(Double.toString(two));
		return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * 提供精确的小数位四舍五入处理。
	 * @param decimal 需要四舍五入的数字
	 * @param scale 小数点后保留几位
	 * @return 四舍五入后的结果
	 */
	public static Double round(Double decimal, int scale) {
		if(decimal==null)
			decimal=0.0;
		if (scale < 0) {
			throw new IllegalArgumentException(
					"The scale must be a positive integer or zero");
		}
		BigDecimal b = new BigDecimal(Double.toString(decimal));
		BigDecimal one = new BigDecimal("1");
		return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * 
	* @Description: judge the string is Double or not
	* @Title: isDouble 
	* @param targetNum
	* @return boolean
	* @throws
	* @date 2016年1月19日 上午11:45:41
	 */
	public static boolean isDouble(String targetNum){
		try {
			Double.parseDouble(targetNum);
			return true;
		} catch (NumberFormatException ex) {
			logger.warn(ex.toString());
		}
		return false;
	}
	
	/**
	 * 
	* @Description: judge the string is Integer or not
	* @Title: isInteger 
	* @param targetNum
	* @return boolean
	* @throws
	* @date 2016年1月19日 上午11:45:28
	 */
	public static boolean isInteger(String targetNum){
		try {
			Integer.parseInt(targetNum);
			return true;
		} catch (NumberFormatException ex) {
			logger.warn(ex.toString());
		}
		return false;
	}
	
	/**
	 * 
	* @Description: judge the string is number or not
	* @Title: isNumber 
	* @param targetNum
	* @return boolean
	* @throws
	* @date 2016年1月19日 上午11:44:00
	 */
	public static boolean isNumber(String targetNum){
		return isDouble(targetNum) || isInteger(targetNum);
	}
}
