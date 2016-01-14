package com.nk.baidu.helper;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.POIXMLTextExtractor;
import org.apache.poi.hslf.HSLFSlideShow;
import org.apache.poi.hslf.model.Slide;
import org.apache.poi.hslf.model.TextRun;
import org.apache.poi.hslf.usermodel.SlideShow;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.xmlbeans.XmlException;

import com.nk.baidu.excel.ExcelReaderUtils;
import com.nk.baidu.excel.RowReaderHandler;
import com.nk.baidu.excel.XCell;
import com.nk.baidu.excel.XRow;

public abstract class ReadFileHelper {
	
	private ReadFileHelper(){
	}
	
	/**
	 * 处理word2003
	 * 
	 * @param path
	 * @return
	 * @throws IOException 
	 * @throws OpenXML4JException 
	 * @throws XmlException 
	 * @throws Exception
	 */
	public static String readWord(String path) throws IOException, XmlException, OpenXML4JException {
		String bodyText = null;
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(path);
			
			boolean is2003Format = false;
			int index = path.lastIndexOf(".");
			if (index != -1) {
				String suffix = path.substring(index+1);
				is2003Format = suffix == null ? false : suffix.toLowerCase().equals("doc");
			}
			if (is2003Format) {
				WordExtractor extractor = new WordExtractor(inputStream);
				bodyText = extractor.getText();
			} else {
				OPCPackage opcPackage = POIXMLDocument.openPackage(path);
				POIXMLTextExtractor ex = new XWPFWordExtractor(opcPackage);
				bodyText = ex.getText();
			}
		} finally {
			if (inputStream != null){
				inputStream.close();
			}
		}
		return bodyText;
	}

	/**
	 * 处理excel
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static String readExcel(String path) {
		ExcelContentHandler contentHandler = new ExcelContentHandler();
		ExcelReaderUtils.readExcelAllSheet(path, contentHandler);
		return contentHandler.getContent();
	}

	/**
	 * 处理ppt
	 * 
	 * @param path
	 * @return
	 * @throws IOException 
	 */
	public static String readPowerPoint(String path) throws IOException {
		StringBuilder content = new StringBuilder();
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(path);
			
			boolean is2003Format = false;
			int index = path.lastIndexOf(".");
			if (index != -1) {
				String suffix = path.substring(index+1);
				is2003Format = suffix == null ? false : suffix.toLowerCase().equals("ppt");
			}
			
			if (is2003Format) {
				SlideShow ss = new SlideShow(new HSLFSlideShow(inputStream));// is
				// 为文件的InputStream，建立SlideShow
				Slide[] slides = ss.getSlides();// 获得每一张幻灯片
				for (int i = 0; i < slides.length; i++) {
					// 为了取得幻灯片的文字内容，建立TextRun
					TextRun[] t = slides[i].getTextRuns();
					for (int j = 0; j < t.length; j++) {
						// 这里会将文字内容加到content中去
						content.append(t[j].getText()).append("\r\n");
					}
				}
			} else {
				XMLSlideShow xmlSlideShow = new XMLSlideShow(inputStream);
				XSLFSlide[] slides = xmlSlideShow.getSlides();// 获得每一张幻灯片
				for (int i = 0; i < slides.length; i++) {
					XSLFTextShape[] holder = slides[i].getPlaceholders();
					// 为了取得幻灯片的文字内容，建立TextRun
					for (int j = 0; j < holder.length; j++) {
						// 这里会将文字内容加到content中去
						content.append(holder[j].getText()).append("\r\n");
					}
				}
			}
		} finally {
			if(inputStream != null){
				inputStream.close();
			}
		}
		return content.toString();
	}

	/**
	 * 处理pdf
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static String readPdf(String path) throws IOException {
		StringBuffer content = new StringBuffer("");// 文档内容
		PDDocument pdfDocument = null;
		try {
			FileInputStream fis = new FileInputStream(path);
			PDFTextStripper stripper = new PDFTextStripper();
			pdfDocument = PDDocument.load(fis);
			StringWriter writer = new StringWriter();
			stripper.writeText(pdfDocument, writer);
			content.append(writer.getBuffer().toString());
			fis.close();
		} finally {
			if (pdfDocument != null) {
				COSDocument cos = pdfDocument.getDocument();
				cos.close();
				pdfDocument.close();
			}
		}

		return content.toString();

	}

	/**
	 * 处理txt
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static String readTxt(String path) throws IOException {
		StringBuilder sb = new StringBuilder();
		InputStream is = null;
		BufferedReader reader = null;
		try {
			is = new FileInputStream(path);
			// 必须设置成GBK，否则将出现乱码
			reader = new BufferedReader(new InputStreamReader(is, "GBK"));
			String line = "";
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\r\n");
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		return sb.toString().trim();
	}
}

class ExcelContentHandler implements RowReaderHandler {

	StringBuffer buffer = new StringBuffer();
	
	public void readRows(XRow row) {
		for(XCell cell : row.getCells()){
			buffer.append(cell.getValue());
		}
		buffer.append("\r\n");
	}

	public String getContent() {
		return buffer.toString();
	}
}

