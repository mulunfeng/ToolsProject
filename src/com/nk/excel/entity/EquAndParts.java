package com.nk.excel.entity;
import java.util.Date;

import javax.validation.constraints.NotNull;

import com.nk.excel.annotation.ExcelExpImp;


/**
 *
 * 接触网设备及部件明细表
 *
 */
public class EquAndParts {

	
	private static final long serialVersionUID = 4534554172337421336L;
	/** 工区ID **/ 
	private Integer departmentId;
	/** 工区 **/ 
	@NotNull
	private String departmentName;
	/** 区间（站场）id **/ 
	private Integer blockId;
	/** 区间（站场） **/ 
	@NotNull
	private String blockName;
	/** 线别 **/ 
	@NotNull
	private String line;
	/** 线路类型 **/ 
	private String routecode;
	/** 设备名称 **/ 
	@NotNull
	private String equName;
	/** 规格型号(类型) **/ 
	private String modelType;
	/** 材质 **/ 
	private String structure;
	/** 单位 **/ 
	private String unit;
	/** 数量 **/ 
	private Integer num;
	/** 生产厂家 **/ 
	private String factory;
	/** 出厂序号 **/ 
	private String productionOrder;
	/** 出厂日期 **/ 
	private Date productionDate;
	/** 投运日期 **/ 
	private Date operationDate;
	/** 备注 **/ 
	private String remark;

	
	/** 无参的构造函数 **/
	public EquAndParts(){
		super();
	}
	
	/** 有参构造函数 **/
	public EquAndParts(Integer departmentId,String departmentName,Integer blockId,String blockName,String line,String routecode,String equName,String modelType,String structure,String unit,Integer num,String factory,String productionOrder,Date productionDate,Date operationDate,String remark){
		super();
		this.departmentId = departmentId;
		this.departmentName = departmentName;
		this.blockId = blockId;
		this.blockName = blockName;
		this.line = line;
		this.routecode = routecode;
		this.equName = equName;
		this.modelType = modelType;
		this.structure = structure;
		this.unit = unit;
		this.num = num;
		this.factory = factory;
		this.productionOrder = productionOrder;
		this.productionDate = productionDate;
		this.operationDate = operationDate;
		this.remark = remark;
	}
 	
		
	/**
	 * @return 工区ID
	 */
	public Integer getDepartmentId() {
		return departmentId;
	}
	/**
	 * @param departmentId 工区ID
	 */
	public void setDepartmentId(Integer departmentId) {
		this.departmentId = departmentId;
	}
		
	/**
	 * @return 工区
	 */
	@ExcelExpImp(1)
	public String getDepartmentName() {
		return departmentName;
	}
	/**
	 * @param departmentName 工区
	 */
	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}
		
	/**
	 * @return 区间（站场）id
	 */
	public Integer getBlockId() {
		return blockId;
	}
	/**
	 * @param blockId 区间（站场）id
	 */
	public void setBlockId(Integer blockId) {
		this.blockId = blockId;
	}
		
	/**
	 * @return 区间（站场）
	 */
	@ExcelExpImp(2)
	public String getBlockName() {
		return blockName;
	}
	/**
	 * @param blockName 区间（站场）
	 */
	public void setBlockName(String blockName) {
		this.blockName = blockName;
	}
		
	/**
	 * @return 线别
	 */
	@ExcelExpImp(3)
	public String getLine() {
		return line;
	}
	/**
	 * @param line 线别
	 */
	public void setLine(String line) {
		this.line = line;
	}
		
	/**
	 * @return 线路类型
	 */
	@ExcelExpImp(4)
	public String getRoutecode() {
		return routecode;
	}
	/**
	 * @param routecode 线路类型
	 */
	public void setRoutecode(String routecode) {
		this.routecode = routecode;
	}
		
	/**
	 * @return 设备名称
	 */
	@ExcelExpImp(5)
	public String getEquName() {
		return equName;
	}
	/**
	 * @param equName 设备名称
	 */
	public void setEquName(String equName) {
		this.equName = equName;
	}
		
	/**
	 * @return 规格型号(类型)
	 */
	@ExcelExpImp(6)
	public String getModelType() {
		return modelType;
	}
	/**
	 * @param modelType 规格型号(类型)
	 */
	public void setModelType(String modelType) {
		this.modelType = modelType;
	}
		
	/**
	 * @return 材质
	 */
	@ExcelExpImp(7)
	public String getStructure() {
		return structure;
	}
	/**
	 * @param structure 材质
	 */
	public void setStructure(String structure) {
		this.structure = structure;
	}
		
	/**
	 * @return 单位
	 */
	@ExcelExpImp(8)
	public String getUnit() {
		return unit;
	}
	/**
	 * @param unit 单位
	 */
	public void setUnit(String unit) {
		this.unit = unit;
	}
		
	/**
	 * @return 数量
	 */
	@ExcelExpImp(9)
	public Integer getNum() {
		return num;
	}
	/**
	 * @param num 数量
	 */
	public void setNum(Integer num) {
		this.num = num;
	}
		
	/**
	 * @return 生产厂家
	 */
	@ExcelExpImp(10)
	public String getFactory() {
		return factory;
	}
	/**
	 * @param factory 生产厂家
	 */
	public void setFactory(String factory) {
		this.factory = factory;
	}
		
	/**
	 * @return 出厂序号
	 */
	@ExcelExpImp(11)
	public String getProductionOrder() {
		return productionOrder;
	}
	/**
	 * @param productionOrder 出厂序号
	 */
	public void setProductionOrder(String productionOrder) {
		this.productionOrder = productionOrder;
	}
		
	/**
	 * @return 出厂日期
	 */
	@ExcelExpImp(12)
	public Date getProductionDate() {
		return productionDate;
	}
	/**
	 * @param productionDate 出厂日期
	 */
	public void setProductionDate(Date productionDate) {
		this.productionDate = productionDate;
	}
		
	/**
	 * @return 投运日期
	 */
	@ExcelExpImp(13)
	public Date getOperationDate() {
		return operationDate;
	}
	/**
	 * @param operationDate 投运日期
	 */
	public void setOperationDate(Date operationDate) {
		this.operationDate = operationDate;
	}
		
	/**
	 * @return 备注
	 */
	@ExcelExpImp(14)
	public String getRemark() {
		return remark;
	}
	/**
	 * @param remark 备注
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}
		
 	
}
