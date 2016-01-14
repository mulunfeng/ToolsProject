package com.nk.baidu.excel;

import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.xml.sax.SAXException;

public interface ExcelReader {

	void setRowReaderHandler(RowReaderHandler readerHandler);
	
	void processOneSheet(String filename, int sheetId) throws InvalidFormatException, IOException, OpenXML4JException, SAXException;
	
	void processAllSheet(String filename) throws IOException, OpenXML4JException, SAXException;
	
}
