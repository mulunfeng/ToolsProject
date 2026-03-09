package com.nk.db.util;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 功能概述：jdbc连接数据库获得数据源 <br>
 * 创建时间：2013-4-24上午11:27:37 <br>
 * 修改记录： <br>
 * 
 * @author xiaoliang.li <br>
 */
public class DataSource {

//	static Properties pro = null;
//
//   	static {
//   		if(pro == null){
//			pro = PropertiesUtil.getProperties("freeMarker.properties");
//		}
//	}
   	
   	private static Connection connection = null;

//	private static final String url = pro.getProperty("jdbc.url");
	
	public static Connection getConnection(Properties pro) {
		// 事先导入驱动
		try {
			if(connection == null){
				String driver = pro.getProperty("jdbc.driver");
				Class.forName(driver);// 加载驱动
				// 创建连接对象,即已经连接到数据库
				if (driver.equalsIgnoreCase("com.mysql.jdbc.Driver")) {
					connection = DriverManager.getConnection(pro.getProperty("jdbc.url"), pro.getProperty("user"), pro.getProperty("password"));	
				} else if (driver.equalsIgnoreCase("oracle.jdbc.driver.OracleDriver")) {
					connection = DriverManager.getConnection(pro.getProperty("jdbc.url"), pro);
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return connection;
	}
}
