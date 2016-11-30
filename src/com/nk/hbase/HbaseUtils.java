package com.nk.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.List;

public class HbaseUtils {

	public static Configuration configuration;
	public static Connection connection;
	public static Admin admin;

//	public static void main(String[] args) throws IOException {
//		println("Start...");
//		int pageSize = 100;
//		int totalCount = DbToVoUtils.getCountFromDB(WebPageInfo.class);
//		System.out.println(totalCount);
//		int i=0;
//		for (int j = 310; j < (totalCount / pageSize + 1); j++) {
////			int endIndex = pageSize * (j + 1) > totalCount ? totalCount : pageSize * (j + 1);
//			List<WebPageInfo> webPageInfos = DbToVoUtils.getDataFromDB(WebPageInfo.class ,pageSize * j, pageSize);
//			List<Put> puts = new ArrayList<Put>();
//			for (WebPageInfo webPageInfo : webPageInfos) {
//				puts.add(getPut(webPageInfo.getId(), "webUrl", "webUrl", webPageInfo.getWebUrl()));
//				puts.add(getPut(webPageInfo.getId(), "pageInfo", "pageInfo", webPageInfo.getPageInfo()));
//				puts.add(getPut(webPageInfo.getId(), "createTime", "createTime", String.valueOf(webPageInfo.getCreateTime().getTime())));
//				puts.add(getPut(webPageInfo.getId(), "updateTime", "updateTime", String.valueOf(webPageInfo.getUpdateTime().getTime())));
//				System.out.println(i++);
//			}
//			insert("webpageinfo", puts);
//		}
		//getData("webpageinfo", "angelababy");
//		deleteTable("webpageinfo");
//		createTable("webpageinfo", new String[]{"webUrl","pageInfo","createTime","updateTime"});
//
//		println("End...");
//	}

	public static void main(String[] args) {
		try {
			getData("webpageinfo", "300");
		}catch (Exception e){
			e.printStackTrace();
		}

	}
	/**
	 * 初始化链接
	 */
	public static void init() {
		configuration = HBaseConfiguration.create();
		configuration.setInt("hbase.rpc.timeout",20000);
		configuration.setInt("hbase.client.operation.timeout",30000);
		configuration.setInt("hbase.client.scanner.timeout.period",20000);
		configuration.set("hbase.zookeeper.property.clientPort", "2181");
		configuration.set("hbase.zookeeper.quorum", "172.25.44.1");
		configuration.set("hbase.master", "hdfs://172.25.44.1:16010");
		configuration.set("hbase.root.dir", "hdfs://172.25.44.1:16010/hbase");

		try {
			connection = ConnectionFactory.createConnection(configuration);
			admin = connection.getAdmin();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 关闭连接
	 */
	public static void close() {
		try {
			if (null != admin) {
				admin.close();
			}
			if (null != connection) {
				connection.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 创建表
	 * 
	 * @param tableName 表名
	 * @param family 列族列表
	 * @throws IOException
	 */
	public static void createTable(String tableName, String[] cols) throws IOException {
		init();
		TableName tName = TableName.valueOf(tableName);
		if (admin.tableExists(tName)) {
			println(tableName + " exists.");
		} else {
			HTableDescriptor hTableDesc = new HTableDescriptor(tName);
			for (String col : cols) {
				HColumnDescriptor hColumnDesc = new HColumnDescriptor(col);
				hTableDesc.addFamily(hColumnDesc);
			}
			admin.createTable(hTableDesc);
		}

		close();
	}

	/**
	 * 删除表
	 * 
	 * @param tableName 表名称
	 * @throws IOException
	 */
	public static void deleteTable(String tableName) throws IOException {
		init();
		TableName tName = TableName.valueOf(tableName);
		if (admin.tableExists(tName)) {
			admin.disableTable(tName);
			admin.deleteTable(tName);
		} else {
			println(tableName + " not exists.");
		}
		close();
	}

	/**
	 * 查看已有表
	 * 
	 * @throws IOException
	 */
	public static void listTables() {
		init();
		HTableDescriptor hTableDescriptors[] = null;
		try {
			hTableDescriptors = admin.listTables();
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (HTableDescriptor hTableDescriptor : hTableDescriptors) {
			println(hTableDescriptor.getNameAsString());
		}
		close();
	}

	/**
	 * 插入单行
	 * 
	 * @param tableName 表名称
	 * @param rowKey RowKey
	 * @param colFamily 列族
	 * @param col 列
	 * @param value 值
	 * @throws IOException
	 */
	public static void insert(String tableName, String rowKey, String colFamily, String col, String value) throws IOException {
		init();
		Table table = connection.getTable(TableName.valueOf(tableName));
		Put put = new Put(Bytes.toBytes(rowKey));
		put.addColumn(Bytes.toBytes(colFamily), Bytes.toBytes(col), Bytes.toBytes(value));
		table.put(put);

		/*
		 * 批量插入 List<Put> putList = new ArrayList<Put>(); puts.add(put); table.put(putList);
		 */

		table.close();
		close();
	}

	/**
	 * 获取put
	 * @param rowKey
	 * @param colFamily
	 * @param col
	 * @param value
     * @return
     */
	public static Put getPut(String rowKey, String colFamily, String col, String value){
		Put put = new Put(Bytes.toBytes(rowKey));
		put.addColumn(Bytes.toBytes(colFamily), Bytes.toBytes(col), Bytes.toBytes(value));
		return put;
	}

	/**
	 * 批量插入
	 * @param tableName
	 * @param putList
	 * @throws IOException
     */
	public static void insert(String tableName, List<Put> putList) throws IOException {
		if (putList == null || putList.size() == 0)
			return;

		init();
		Table table = connection.getTable(TableName.valueOf(tableName));
		table.put(putList);
		table.close();
		close();
	}


	public static void delete(String tableName, String rowKey, String colFamily, String col) throws IOException {
		init();

		if (!admin.tableExists(TableName.valueOf(tableName))) {
			println(tableName + " not exists.");
		} else {
			Table table = connection.getTable(TableName.valueOf(tableName));
			Delete del = new Delete(Bytes.toBytes(rowKey));
			if (colFamily != null) {
				del.addFamily(Bytes.toBytes(colFamily));
			}
			if (colFamily != null && col != null) {
				del.addColumn(Bytes.toBytes(colFamily), Bytes.toBytes(col));
			}
			/*
			 * 批量删除 List<Delete> deleteList = new ArrayList<Delete>(); deleteList.add(delete); table.delete(deleteList);
			 */
			table.delete(del);
			table.close();
		}
		close();
	}

	/**
	 * 根据RowKey获取数据
	 * 
	 * @param tableName 表名称
	 * @param rowKey RowKey名称
	 * @param colFamily 列族名称
	 * @param col 列名称
	 * @throws IOException
	 */
	public static void getData(String tableName, String rowKey, String colFamily, String col) throws IOException {
		init();
		Table table = connection.getTable(TableName.valueOf(tableName));
		Get get = new Get(Bytes.toBytes(rowKey));
		if (colFamily != null) {
			get.addFamily(Bytes.toBytes(colFamily));
		}
		if (colFamily != null && col != null) {
			get.addColumn(Bytes.toBytes(colFamily), Bytes.toBytes(col));
		}
		Result result = table.get(get);
		showCell(result);
		table.close();
		close();
	}

	/**
	 * 根据RowKey获取信息
	 * 
	 * @param tableName
	 * @param rowKey
	 * @throws IOException
	 */
	public static void getData(String tableName, String rowKey) throws IOException {
		getData(tableName, rowKey, null, null);
	}

	/**
	 * 格式化输出
	 * 
	 * @param result
	 */
	public static void showCell(Result result) {
		Cell[] cells = result.rawCells();
		for (Cell cell : cells) {
			println("RowName: " + new String(CellUtil.cloneRow(cell)) + " ");
			println("Timetamp: " + cell.getTimestamp() + " ");
			println("column Family: " + new String(CellUtil.cloneFamily(cell)) + " ");
			println("row Name: " + new String(CellUtil.cloneQualifier(cell)) + " ");
			println("value: " + new String(CellUtil.cloneValue(cell)) + " ");
		}
	}

	/**
	 * 打印
	 * 
	 * @param obj 打印对象
	 */
	private static void println(Object obj) {
		System.out.println(obj);
	}
}
