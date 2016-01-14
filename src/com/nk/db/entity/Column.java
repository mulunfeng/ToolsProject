package com.nk.db.entity;

import java.io.Serializable;

/**
 * 功能概述：接收数据列载体 <br>
 * 创建时间：2013-4-24下午2:54:18 <br>
 * 修改记录： <br>
 * 
 * @author xiaoliang.li <br>
 */
public class Column implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** 列名 **/
	private String name;
	/** javaBean中的列名 **/
	private String javaName;
	/** getSet方法中的属性名 **/
	private String getSetName;
	/** 对应数据类型的类 **/
	private String dataType;
	/** 对应的数据类型名 **/
	private String type; 
	/** 数据类型名 **/
	private String typeDB; 
	/** 列类型的精确度 **/
	private String precision;
	/** 小数点后的位数 **/
	private String scale;
	/** 数据长度 **/
	private String length;
	/** 是否为空 **/
	private String nullable;
	/** 列注释 **/
	private String comments;
	/** 主键 **/
	private String columnKey;

	public Column() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Column(String name, String javaName, String getSetName,
			String dataType, String type, String typeDB, String precision,
			String scale, String length, String nullable, String comments,
			String columnKey) {
		super();
		this.name = name;
		this.javaName = javaName;
		this.getSetName = getSetName;
		this.dataType = dataType;
		this.type = type;
		this.typeDB = typeDB;
		this.precision = precision;
		this.scale = scale;
		this.length = length;
		this.nullable = nullable;
		this.comments = comments;
		this.columnKey = columnKey;
	}

	public Column(String name, String javaName, String dataType,
			String precision, String scale, String length, String nullable,
			String comments, String columnKey) {
		super();
		this.name = name;
		this.javaName = javaName;
		this.dataType = dataType;
		this.precision = precision;
		this.scale = scale;
		this.length = length;
		this.nullable = nullable;
		this.comments = comments;
		this.columnKey = columnKey;
	}

	public String getTypeDB() {
		return typeDB;
	}

	public void setTypeDB(String typeDB) {
		this.typeDB = typeDB;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getJavaName() {
		return javaName;
	}

	public void setJavaName(String javaName) {
		this.javaName = javaName;
	}

	public String getGetSetName() {
		return getSetName;
	}

	public void setGetSetName(String getSetName) {
		this.getSetName = getSetName;
	}
	
	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPrecision() {
		return precision;
	}

	public void setPrecision(String precision) {
		this.precision = precision;
	}

	public String getScale() {
		return scale;
	}

	public void setScale(String scale) {
		this.scale = scale;
	}

	public String getLength() {
		return length;
	}

	public void setLength(String length) {
		this.length = length;
	}

	public String getNullable() {
		return nullable;
	}

	public void setNullable(String nullable) {
		this.nullable = nullable;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getColumnKey() {
		return columnKey;
	}

	public void setColumnKey(String columnKey) {
		this.columnKey = columnKey;
	}

}
