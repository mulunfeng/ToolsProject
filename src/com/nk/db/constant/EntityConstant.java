package com.nk.db.constant;


/**
 * 功能概述：生成实体的常量类
 * <br>
 * 创建时间：2013-4-25下午1:03:51
 * <br>
 * 修改记录：
 * <br>
 * @author xiaoliang.li
 * <br>
 */
public class EntityConstant {

	/** 模板文件 **/
	public static final String ENTITY_TEMPLATE_DIR = "GenTemplate";
	
	/** 模板文件 **/
	public static final String ENTITY_TEMPLATE = "Entity.java.ftl";
	
	/** 模板文件-Mapper **/
	public static final String MAPPER_TEMPLATE = "Mapper.xml.ftl";
	
	/** 实体包名 **/
	public static final String PACKAGE = "PACKAGE";//"com.ikang.generate.entity";
	
	/** 继承类包名 **/
	public static final String EXTEND_PACKAGE = "EXTEND_PACKAGE";//"com.ikang.base.entity";//com.ikang.base.entity
	
	/** 继承类 **/
	public static final String EXTEND_NAME = "EXTEND_NAME";//"IdEntity";//IdEntity
	
	public static final String TABLES = "TABLES";
	
//	/** 数据库所属用户 **/
//	public static final String SYSNAME = "RMS";
	
	/** 数据表前缀 **/
	public static final String TABPREFIX = "TABPREFIX";
	
	/** 数据库表字段前缀 **/
	public static final String ATTPREFIX = "ATTPREFIX";
	
	/** 是否需要主键 **/
	public static final String ISNEEDKEY = "ISNEEDKEY";
	
	public static final boolean ISNEEDKEY_B = true;
	
	public static final String JDBC_DRIVER_MYSQL = "com.mysql.jdbc.Driver";
	
	public static final String JDBC_DRIVER_ORACLE = "oracle.jdbc.driver.OracleDriver";
	
}
