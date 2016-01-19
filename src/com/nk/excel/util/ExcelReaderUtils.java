package com.nk.excel.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

import org.apache.poi.POIXMLDocument;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.xmlbeans.impl.piccolo.io.FileFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.nk.excel.handler.RowReaderHandler;
import com.nk.excel.service.ExcelReader;
import com.nk.excel.service.impl.Excel2003Reader;
import com.nk.excel.service.impl.Excel2007Reader;

public class ExcelReaderUtils {

	private static final Logger logger = LoggerFactory.getLogger(ExcelReaderUtils.class);
	
	public static void readExcelAllSheet(String fileName, RowReaderHandler readerHandler) {
		try {
			readExcel(fileName, true, 0, readerHandler);			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new IllegalArgumentException("读取EXCEL文件异常,请联系管理员.异常["+e.getMessage()+"]", e);
		}

	}
	
	/**
	 * @param fileName
	 * @param handleAllSheet
	 * @param sheetId
	 * @param readerHandler
	 * @throws SAXException 
	 * @throws OpenXML4JException 
	 * @throws IOException 
	 * @throws Exception
	 */
	public static void readExcelOptSheet(String fileName, int sheetId, RowReaderHandler readerHandler) {
		try {
			readExcel(fileName,false, sheetId, readerHandler);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new IllegalArgumentException("读取EXCEL文件异常,请联系管理员.异常["+e.getMessage()+"]", e);
		}
	}
	
	private static boolean isExcel2003(InputStream inputStream) throws IOException{
		// If clearly doesn't do mark/reset, wrap up
		if(! inputStream.markSupported()) {
			inputStream = new PushbackInputStream(inputStream, 8);
		}
		if(POIFSFileSystem.hasPOIFSHeader(inputStream)) {
			return true;
		} else if(POIXMLDocument.hasOOXMLHeader(inputStream)) {
			return false;
		} else {
			throw new FileFormatException("文件格式错误，fileName的扩展名只能是xls或xlsx。");
		}
	}
	
	private static void readExcel(String fileName, boolean handleAllSheet, int sheetId, RowReaderHandler readerHandler) throws IOException, OpenXML4JException, SAXException{
		InputStream inp = null;
		try {
			inp = new FileInputStream(new File(fileName));
			boolean flag = isExcel2003(inp);
			
			ExcelReader excelReader = flag?new Excel2003Reader():new Excel2007Reader();
			excelReader.setRowReaderHandler(readerHandler);
			if(handleAllSheet){
				excelReader.processAllSheet(fileName);
			} else {
				excelReader.processOneSheet(fileName, sheetId);
			}
		} finally {
			if(inp != null){
				inp.close();
				inp = null;
			}
		}
	}

}
