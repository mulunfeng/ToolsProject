package com.nk.db.app;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.nk.db.constant.EntityConstant;
import com.nk.db.entity.Column;
import com.nk.db.entity.Table;
import com.nk.db.util.ConvertUtil;
import com.nk.db.util.DataSource;

/**
 * 数据库全文检索
 * @author young
 *
 */
public abstract class SearchDatabaseUtil {
 
	//要检索的字符串
	private static String[] SEARCH_STR = {"庆祝厦门供电段"};
	//日志位置
	private static String PATH = "D:/search_database.log";
	public static void main(String[] args) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SearchDatabaseUtil.writeToFile(sdf.format(new Date()));
		
		SearchDatabaseUtil.generator("oracle", "jdbc:oracle:thin:@192.168.10.207:1512:oradb",
				"xmgdd", "xmgdd");
	}
	
	public static void generator(String type, String url, String schema, String password){
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
		try {
			List<Table> tablesname = SearchDatabaseUtil.getTables(pro);
			System.out.println("共检索"+tablesname.size()+"张表");
			SearchDatabaseUtil.search(pro,tablesname);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected static List<Table> getTables(Properties pro){
		DatabaseMetaData dbMetaData = null ;
		Statement stmt = null;
		ResultSet rs = null;
		List<Table> tableList = new ArrayList<Table>();
		try{
			Connection conn = DataSource.getConnection(pro);
			@SuppressWarnings("unchecked")
			List<String> tables = (List<String>) pro.get("TABLE");
			
			if(tables == null || tables.isEmpty()){
				dbMetaData = conn.getMetaData(); 
				String[] types = { "TABLE" };    
				rs = dbMetaData.getTables(null, pro.getProperty("user").toUpperCase(), "%", types);  
				Table table;
				while (rs.next()) {    
				    table = new Table();
				    table.setTableName(rs.getString("TABLE_NAME"));
				    table.setTableComment(rs.getString("REMARKS"));
				    tableList.add(table);
				}
			} else {
				StringBuilder sqlBuilder = new StringBuilder();
				if (pro.get("jdbc.driver").toString().equalsIgnoreCase(EntityConstant.JDBC_DRIVER_MYSQL)){
					sqlBuilder.append("SELECT `TABLES`.TABLE_NAME as Table_Name, `TABLES`.TABLE_COMMENT as Comments FROM `INFORMATION_SCHEMA`.`TABLES` where TABLE_NAME in (");
					for(String tableName : tables){
						sqlBuilder.append("'").append(tableName.toLowerCase()).append("'").append(",");
					}
					sqlBuilder = sqlBuilder.deleteCharAt(sqlBuilder.lastIndexOf(","));
					sqlBuilder.append(")");
				} else if (pro.get("jdbc.driver").toString().equalsIgnoreCase(EntityConstant.JDBC_DRIVER_ORACLE)) {
					sqlBuilder.append("Select A.Table_Name,B.Comments From User_Tables A join User_Tab_Comments B on A.Table_Name=B.Table_Name where 1=1 ");
					sqlBuilder.append(" and A.Table_Name in (");
					for(String tableName : tables){
						sqlBuilder.append("'").append(tableName).append("'").append(",");
					}
					sqlBuilder = sqlBuilder.deleteCharAt(sqlBuilder.lastIndexOf(","));
					sqlBuilder.append(")");
				}
				stmt = conn.createStatement();
				rs = stmt.executeQuery(sqlBuilder.toString());
				while(rs.next()){
					Table table = new Table();
					table.setTableName(rs.getString("Table_Name"));
					table.setTableComment(rs.getString("Comments"));
					tableList.add(table);
				}
			}
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
		return tableList;
	}

	private static void search(Properties pro,List<Table> tableNames) {
		Statement stmt = null;
		ResultSet rs = null;
		Connection conn = DataSource.getConnection(pro);
		try {
			stmt = conn.createStatement();
			int i = 1;
			for(Table table : tableNames){
				System.out.println((i++)+" 正在检索"+table.getTableName()+"....");
				List<Column> columnList = SearchDatabaseUtil.getColumns(table.getTableName(), pro);
				List<Column> columns = SearchDatabaseUtil.getColumnByType(columnList,"String");
				if(columns!=null && columns.size()>0){
					StringBuilder sqlBuilder = new StringBuilder();
					sqlBuilder.append("select count(1) dataSize from ");
					sqlBuilder.append(table.getTableName());
					rs = stmt.executeQuery(sqlBuilder.toString());
					while(rs.next()){
						int dataSize = rs.getInt("dataSize");
						System.out.println(table.getTableName()+"共有"+dataSize+"条数据");
					}
					rs.close();
					rs = null;
					for(Column column : columns){
						for(String searchStr : SEARCH_STR){
							sqlBuilder = new StringBuilder();
							sqlBuilder.append("select count(1) as existData from ");
							sqlBuilder.append(table.getTableName());
							sqlBuilder.append(" t where t. ");
							sqlBuilder.append(column.getName());
							sqlBuilder.append(" like '%" + searchStr + "%'");
							rs = stmt.executeQuery(sqlBuilder.toString());
							boolean existData = false;
							while(rs.next()){
								int size = rs.getInt("existData");
								existData = size>0;
							}
							if(existData){
								System.out.println("*********************检索到数据"+table.getTableName()+"===="+column.getName());
								SearchDatabaseUtil.writeToFile(table.getTableName()+"===="+column.getName());
							}
						}
					}
				}
				System.out.println(table.getTableName()+"已经检索完----");
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
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
	
	private static List<Column> getColumnByType(List<Column> columnList,
			String type) {
		List<Column> columns = new ArrayList<Column>();
		if(columnList!=null && columnList.size()>0){
			for(Column column : columnList){
				if(column.getDataType()!=null && column.getDataType().equals(type)){
					columns.add(column);
				}
			}
		}
		return columns;
	}

	/**
	 * 根据数据库表明获得数据列信息
	 * @param tableName 表名
	 * @return
	 */
	protected static List<Column> getColumns(String tableName, Properties pro) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		DatabaseMetaData dbmd = null;
		ResultSet resultSet = null;
		ResultSetMetaData rsmd = null;
		ResultSet rs2 = null;
		try {
			Connection conn = DataSource.getConnection(pro);
			stmt = conn.prepareStatement("select *  from " + tableName + " where 1=0 ");
			
			
			Map<String,String> columnRemarkMap = new HashMap<String, String>();
			// 获取列注释
			dbmd = conn.getMetaData();
			rs = dbmd.getColumns(null, null, tableName, null);
			while (rs.next()) {
				columnRemarkMap.put(rs.getString("COLUMN_NAME"), rs.getString("REMARKS"));
			}
		
			resultSet = stmt.executeQuery();
			rsmd = resultSet.getMetaData();
			// 获得所有列的数目及实际列数
			int n = rsmd.getColumnCount();
			List<Column> columns = new ArrayList<Column>();
			//如果需要主键则需要从第一行开始
			for (int i = 1; i <= n; i++) {
				// 获得指定列的列名
				String colName = rsmd.getColumnName(i);
				Column column = new Column();
				column.setName(colName);
				String javaName = ConvertUtil.getAttributeName(colName, pro.getProperty(EntityConstant.ATTPREFIX));
				// javaBean中的列名
				column.setJavaName(javaName);
				//get和set方法的方法名称
				column.setGetSetName(ConvertUtil.getGetSetName(javaName));
				if(ConvertUtil.sqlType2JavaType(rsmd.getColumnTypeName(i).toLowerCase()) == null){
					System.out.println("+++++++++++++++++++++++++++++++"+rsmd.getColumnTypeName(i));
				}
				column.setType(ConvertUtil.sqlType2JavaType(rsmd.getColumnTypeName(i).toLowerCase()));
				column.setTypeDB(rsmd.getColumnTypeName(i).toLowerCase().equals("int")?"INTEGER":rsmd.getColumnTypeName(i));
				column.setDataType(ConvertUtil.sqlType2JavaType(rsmd.getColumnTypeName(i).toLowerCase()));
				// 某列类型的精确度(类型的长度)
				column.setPrecision(String.valueOf(rsmd.getPrecision(i)));
				// 小数点后的位数
				column.setScale(String.valueOf(rsmd.getScale(i)));
				// 获取列的长度
				column.setLength(String.valueOf(rsmd.getColumnDisplaySize(i)));
				
				// 是否为空
				column.setNullable(String.valueOf("1".equals(rsmd.isNullable(i))));
				
				String columnRemark = columnRemarkMap.get(column.getName());
				if(columnRemark != null){
					column.setComments(columnRemark);
				}
				
				// 获取主键列
				rs2 = dbmd.getPrimaryKeys(null, null, tableName);
				while (rs2.next()) {
					if (colName.equals(rs2.getString("COLUMN_NAME")))
						column.setColumnKey("TRUE");
				}
				rs2.close();
				columns.add(column);
				rs.close();
			}
			return columns;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally{
			try {
				resultSet.close();
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	private static void writeToFile(String data){
		BufferedWriter bufferedWriter = null;
        try {

            //Construct the BufferedWriter object
            bufferedWriter = new BufferedWriter(new FileWriter(PATH,true));

            //Start writing to the output stream
            bufferedWriter.write(data);
            bufferedWriter.newLine();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.flush();
                    bufferedWriter.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
	}
}
