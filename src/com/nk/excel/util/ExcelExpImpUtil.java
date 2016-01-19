package com.nk.excel.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nk.excel.annotation.ExcelExpImp;

/**
 * @author young
 */
public class ExcelExpImpUtil {

	private static final Logger logger = LoggerFactory.getLogger(ExcelExpImpUtil.class);
	
	public static <E> void excelExp(Collection<E> col,File templateFile, File targetFile, boolean needRowNum) throws InvalidFormatException, IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		logger.debug("export data...");
		Workbook workbook = WorkbookFactory.create(templateFile);
		Sheet sheet = workbook.getSheetAt(0);
		int lastRowNum = sheet.getLastRowNum();
		// call cell style
		CellStyle cellStyle = bodyCellStyle(workbook, true);
		if (col == null || col.isEmpty()) {
			FileOutputStream outputStream = new FileOutputStream(targetFile);
			workbook.write(outputStream);
			IOUtils.closeQuietly(outputStream);
			return;
		}
		Object[] objArray = col.toArray();
		// cycle row of excel
		for (int i = 0; i < objArray.length; i++) {
			Object obj = objArray[i];
			Method[] methods = obj.getClass().getMethods();
			Map<Integer, Method> map = new HashMap<Integer, Method>();
			for (int j = 0; j < methods.length; j++) {
				if (methods[j].isAnnotationPresent(ExcelExpImp.class)) {
					map.put(methods[j].getAnnotation(ExcelExpImp.class).value(),methods[j]);
				}
			}
			// sort by key
			Map<Integer, Method> treeMap = new TreeMap<Integer, Method>(map);
			Set<Integer> keys = treeMap.keySet();
			Row row = sheet.createRow(lastRowNum + 1 + i);
			
			//need row number
			if(needRowNum){
				HSSFCell rowCell = (HSSFCell) row.createCell(0);
				rowCell.setCellValue(i + 1);
				rowCell.setCellStyle(cellStyle);
			}
			
			int m = 0;
			// cycle column of excel
			for (Integer key : keys) {
				Method method = treeMap.get(key);
				HSSFCell cell1 = (HSSFCell) row.createCell(needRowNum ? (1+m++) : m++);
				if(method.getReturnType().equals(String.class)){
					Object value = method.invoke(obj, new Object[] {});
					cell1.setCellValue(value == null ? "" : value.toString());
				}else if (method.getReturnType().equals(Date.class)){
					cell1.setCellValue(DateUtils.toDateTimeString((Date)method.invoke(obj, new Object[] {})));
				}
				cell1.setCellStyle(cellStyle);
			}
		}
		FileOutputStream outputStream = new FileOutputStream(targetFile);
		workbook.write(outputStream);
		IOUtils.closeQuietly(outputStream);
	}
	
	/**
	 *  add export cell style ,with black border
	 * @param workbook
	 * @return {@code CellStyle}
	 */
	private static CellStyle bodyCellStyle(Workbook workbook, boolean hasBorder){
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
		cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		cellStyle.setWrapText(true);
		if (!hasBorder) {
			return cellStyle;
		}
		cellStyle.setBorderBottom(CellStyle.BORDER_THIN); //bottom border
		cellStyle.setBorderLeft(CellStyle.BORDER_THIN);//left
		cellStyle.setBorderTop(CellStyle.BORDER_THIN);//up
		cellStyle.setBorderRight(CellStyle.BORDER_THIN);//right
		return cellStyle;
	}
	
}
