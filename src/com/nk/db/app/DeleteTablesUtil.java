package com.nk.db.app;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.nk.db.util.DataSource;
import com.nk.excel.util.FileUtil;

/**
 * 删除空表
 * @author young
 */
public abstract class DeleteTablesUtil {

	private static String PATH = "D:/database.log";
	private static String CREBASPATH = "C:/Users/young/Desktop/crebas.sql";
	public static void main(String[] args) {
//		List<String> tables = new ArrayList<String>();
//		tables.add("test");
//		tables.add("T_ACCIDENT_RECORD");
//		tables.add("T_ANTIMINE_RING");
		FileUtil.writeToFile(PATH, "###############");
		
		
		DeleteTablesUtil.generator("oracle", "jdbc:oracle:thin:@192.168.10.208:1521:oradb",
				"nkan", "nkan", DeleteTablesUtil.getTable());
	}
	
	public static void generator(String type, String url, String schema, String password,
			 List<String> tables){
		Properties pro = new Properties();
		if (type.equals("mysql")) {
			pro.put("jdbc.driver", "com.mysql.jdbc.Driver");
		} else if (type.equals("oracle")) {
			pro.put("jdbc.driver", "oracle.jdbc.driver.OracleDriver");
		} else {
			throw new IllegalArgumentException("错误的数据库类型!");
		}
		
		pro.put("jdbc.url", url);
		pro.put("user", schema);
		pro.put("password", password);
		pro.put("remarksReporting", "true");
		pro.put("TABLES", tables);
		try {
			DeleteTablesUtil.createGenerator(pro);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void createGenerator(Properties pro) {
		Statement stmt = null;
		ResultSet rs = null;
		List<String> deleteTable = new ArrayList<String>();
		List<String> haveDataTable = new ArrayList<String>();
		List<String> notExists = new ArrayList<String>();
		try{
			Connection conn = DataSource.getConnection(pro);
			@SuppressWarnings("unchecked")
			List<String> tables = (List<String>) pro.get("TABLES");
			stmt = conn.createStatement();
			for(String table:tables){
				StringBuilder sqlBuilder = new StringBuilder();
				sqlBuilder.append("select count(1) as TableExists from user_tables t where t.table_name = upper('");
				sqlBuilder.append(table);
				sqlBuilder.append("')");
				rs = stmt.executeQuery(sqlBuilder.toString());
				boolean tableExists = false;
				while(rs.next()){
					int size = rs.getInt("TableExists");
					tableExists = size>0;
				}
				if(rs != null){
					rs.close();
					rs = null;
				}
				//查询是否存在数据
				if(tableExists){
					sqlBuilder = new StringBuilder();
					sqlBuilder.append("select count(1) as dataSize from ");
					sqlBuilder.append(table);
					boolean haveData = false;
					rs = stmt.executeQuery(sqlBuilder.toString());
					while(rs.next()){
						int size = rs.getInt("dataSize");
						haveData = size>0;
					}
					//没有数据
					if(!haveData){
						sqlBuilder = new StringBuilder();
						sqlBuilder.append("DROP TABLE "+table+" CASCADE CONSTRAINTS");
						stmt.execute(sqlBuilder.toString());
						deleteTable.add(table);
						FileUtil.writeToFile(PATH, table+"已删除(*^__^*)");
					}else{
						haveDataTable.add(table);
						FileUtil.writeToFile(PATH, table+"已有数据删除失败-----------");
					}
				}else{
					FileUtil.writeToFile(PATH, table+"表不存在");
					notExists.add(table);
				}
			}
			String str = "结束！删除"+deleteTable.size()+"个、有数据未删除"+haveDataTable.size()+"个、不存在"+notExists.size()+"个";
			System.out.println(str);
			FileUtil.writeToFile(PATH, str);
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			try{
				if(rs != null){
					rs.close();
					rs = null;
				}
				if(stmt != null){
					stmt.close();
					stmt = null;
				}
			}catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private static List<String> getTable(){
		try {
			BufferedReader reader = new BufferedReader(new FileReader(CREBASPATH));
			String s =null;
			List<String> tables = new ArrayList<String>();
			while((s = reader.readLine())!=null&&!s.startsWith("DROP USER")){
				if(s.startsWith("DROP TABLE")){
					tables.add(s.substring(s.indexOf("DROP TABLE")+11, s.indexOf("CASCADE CONSTRAINTS")-1));
				}
			}
			reader.close();
			return tables;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
