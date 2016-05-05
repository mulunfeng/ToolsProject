package com.nk.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密工具
 * 
 * @author young
 * 
 */
public class Md5Utils {
	static final char hexdigits[] = { '0', '1', '2', '3', '4', '5', '6', '7',
			'8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	/**
	 * 把字符串转成md5加密字符
	 * 
	 * @param str
	 * @return
	 */
	public static String strToMd5(String str) {
		byte[] strByte = str.getBytes();
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		md.update(strByte);
		byte[] newByte = md.digest();
		StringBuilder sb = new StringBuilder();
		// 转换为16进制
		for (int i = 0; i < newByte.length; i++) {
			if ((newByte[i] & 0xff) < 0x10) {
				sb.append("0");
			}
			sb.append(Long.toString(newByte[i] & 0xff, 16));
		}
		return sb.toString();
	}

	/**
	 * 
	 * @Description: 获取文件的md5值 比DigestUtils.md5Hex(inputStream)高效
	 * @Title: getMD5
	 * @param file
	 * @return String
	 * @throws
	 * @date 2016年5月5日 下午2:03:23
	 */
	public static String getMD5(File file) {
		FileInputStream fis = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			fis = new FileInputStream(file);
			byte[] buffer = new byte[2048];
			int length = -1;
			while ((length = fis.read(buffer)) != -1) {
				md.update(buffer, 0, length);
			}
			byte[] b = md.digest();
			return byteToHexString(b);
			// 16位加密
			// return buf.toString().substring(8, 24);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			try {
				fis.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	private static String byteToHexString(byte[] tmp) {
		// 用字节表示就是 16 个字节
		char str[] = new char[16 * 2]; // 每个字节用 16 进制表示的话，使用两个字符，
		// 所以表示成 16 进制需要 32 个字符
		int k = 0; // 表示转换结果中对应的字符位置
		for (int i = 0; i < 16; i++) { // 从第一个字节开始，对 MD5 的每一个字节
			// 转换成 16 进制字符的转换
			byte byte0 = tmp[i]; // 取第 i 个字节
			str[k++] = hexdigits[byte0 >>> 4 & 0xf]; // 取字节中高 4 位的数字转换,
			// >>> 为逻辑右移，将符号位一起右移
			str[k++] = hexdigits[byte0 & 0xf]; // 取字节中低 4 位的数字转换
		}
		return new String(str); // 换后的结果转换为字符串
	}
}
