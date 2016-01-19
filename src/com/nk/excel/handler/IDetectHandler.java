package com.nk.excel.handler;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

public interface IDetectHandler extends RowReaderHandler {

	boolean validateExcelData(List<String> data, int rowIndex, List<String> errList);

	List<String> getErrorMessages();

	List<?> getDetectEntityList();

	void exportExcel(List<?> detectEntities, File templateFile, File targetFile)
			throws InvalidFormatException, IOException;
	
	void setTypeName(String typeName);
}
