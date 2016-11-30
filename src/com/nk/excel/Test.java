package com.nk.excel;

import com.nk.excel.entity.EquAndParts;
import com.nk.excel.handler.ExcelImpHandler;
import com.nk.excel.handler.IDetectHandler;
import com.nk.excel.util.ExcelExpImpUtil;
import com.nk.excel.util.ExcelReaderUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;


public class Test {

	public static void main(String[] args) throws ClassNotFoundException, InvalidFormatException, IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		File templateFile = new File("src/com/nk/excel/template/tempEquAndParts.xls");
		// 导入
		IDetectHandler detectHandler = new ExcelImpHandler(true,"com.nk.excel.entity.EquAndParts", templateFile);
		ExcelReaderUtils.readExcelAllSheet("src/com/nk/excel/template/equAndParts.xls", detectHandler);
		List<String> errList = detectHandler.getErrorMessages();

		// 将数据导入到数据库中
		List detectEntities = detectHandler.getDetectEntityList();
		if (detectEntities == null || detectEntities.isEmpty()) {
			errList.add("excel中没有数据.");
		}
		for(Object obj:detectEntities){
			System.out.println(obj);
		}
		
		//导出
		EquAndParts equ = new EquAndParts();
		equ.setBlockName("blockName");
		equ.setDepartmentName("departmentName");
		equ.setEquName("equName");
		equ.setModelType("modelType");
		detectEntities.add(equ);
		File targetFile = new File("D://targetFile.xls");
		ExcelExpImpUtil.excelExp(detectEntities, templateFile, targetFile, true);
	}

}
