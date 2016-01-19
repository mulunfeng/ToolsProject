package com.nk.excel.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import com.nk.excel.entity.XCell;
import com.nk.excel.entity.XRow;
import com.nk.excel.handler.RowReaderHandler;
import com.nk.excel.service.ExcelReader;

public class Excel2007Reader extends DefaultHandler implements ExcelReader {
	// 共享字符串表
	private SharedStringsTable sst;
	// 上一次的内容
	private String lastContents;
	private boolean nextIsString;
	private int sheetIndex = -1;
	private boolean hasValue;
	// 当前行
	private int curRow = 0;
	// 当前列
	private int curCol = 0;
	
	private RowReaderHandler readerHandler;
	private XRow rowHandler= new XRow();
	
	@Override
	public void setRowReaderHandler(RowReaderHandler readerHandler) {
		this.readerHandler = readerHandler;
	}
	
	/**
	 * 只遍历一个电子表格，其中sheetId为要遍历的sheet索引，从1开始，1-3
	 * 
	 * @param filename
	 * @param sheetId
	 * @throws OpenXML4JException 
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws Exception
	 */
	@Override
	public void processOneSheet(String filename, int sheetId) throws IOException, OpenXML4JException, SAXException {
		OPCPackage pkg = OPCPackage.open(filename);
		XSSFReader r = new XSSFReader(pkg);
		SharedStringsTable sst = r.getSharedStringsTable();
		XMLReader parser = fetchSheetParser(sst);

		// 根据 rId# 或 rSheet# 查找sheet
		InputStream sheet = null;
		try {
			sheet = r.getSheet("rId" + sheetId);
			sheetIndex++;
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("第["+sheetId+"]个sheet不存在!", e);
		}
		InputSource sheetSource = new InputSource(sheet);
		parser.parse(sheetSource);
		sheet.close();
	}

	/**
	 * 遍历工作簿中所有的电子表格
	 * 
	 * @param filename
	 * @throws Exception
	 */
	public void processAllSheet(String filename) throws IOException, OpenXML4JException, SAXException {
		OPCPackage pkg = OPCPackage.open(filename);
		XSSFReader r = new XSSFReader(pkg);
		SharedStringsTable sst = r.getSharedStringsTable();
		XMLReader parser = fetchSheetParser(sst);
		Iterator<InputStream> sheets = r.getSheetsData();
		while (sheets.hasNext()) {
			curRow = 0;
			sheetIndex++;
//			rowHandler.setSheetIndex(sheetIndex);
//			rowHandler.setRowIndex(curRow);
			
			InputStream sheet = sheets.next();
			InputSource sheetSource = new InputSource(sheet);
			parser.parse(sheetSource);
			sheet.close();
		}
	}

	public XMLReader fetchSheetParser(SharedStringsTable sst)
			throws SAXException {
		XMLReader parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
		this.sst = sst;
		parser.setContentHandler(this);
		return parser;
	}

	public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
		// c => cell
		if(name.equals("c")) {
			// Print the cell reference
			// Figure out if the value is an index in the SST
			String cellType = attributes.getValue("t");
			if(cellType != null && cellType.equals("s")) {
				nextIsString = true;
			} else {
				nextIsString = false;
			}
		}
		// Clear contents cache
		lastContents = "";
	}

	public void endElement(String uri, String localName, String name) throws SAXException {
		// Process the last contents as required.
		// Do now, as characters() may be called more than once
		if (nextIsString) {
			int idx = Integer.parseInt(lastContents);
			lastContents = new XSSFRichTextString(sst.getEntryAt(idx)).toString();
			nextIsString = false;
		}
		// v => contents of a cell
		// Output after we've seen the string contents
		if("c".equals(name) && !hasValue){
			XCell cell = new XCell();
			cell.setColumnIndex(curCol);
			cell.setRowIndex(curRow);
			cell.setValue("");
			rowHandler.addCell(cell);
			curCol++;
		} else if(name.equals("v")) {
			hasValue = true;
			String value = lastContents.trim();
			value = value.equals("") ? " " : value;
			XCell cell = new XCell();
			cell.setColumnIndex(curCol);
			cell.setRowIndex(curRow);
			cell.setValue(value);
			rowHandler.addCell(cell);
			curCol++;
		} else if (name.equals("row")) {
			// 如果标签名称为 row ，这说明已到行尾，调用 optRows() 方法
			rowHandler.setRowIndex(curRow);
			readerHandler.readRows(rowHandler);
			
			rowHandler = new XRow();
			curRow++;
			curCol = 0;
			rowHandler.setSheetIndex(sheetIndex);
			rowHandler.setRowIndex(curRow);
		} else {
			hasValue = false;
		}

	}

	public void characters(char[] ch, int start, int length) throws SAXException {
		// 得到单元格内容的值
		lastContents += new String(ch, start, length);
	}

}
