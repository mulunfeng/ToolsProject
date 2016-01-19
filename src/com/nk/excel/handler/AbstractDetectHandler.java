package com.nk.excel.handler;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;

public abstract class AbstractDetectHandler implements IDetectHandler {

	private String typeName;
	/**
	 * 创建单元格
	 * @param row
	 * @param value
	 * @param column
	 * @param cellStyle
	 * @return {@code Cell}
	 */
	protected Cell createRowCell(Row row, String value, int column, CellStyle cellStyle) {
		Cell cell = row.createCell(column);
		cell.setCellValue(value);
		cell.setCellStyle(cellStyle);
		return cell;
	}
	
	/**
	 * 设置Excel列头样式
	 * @param workbook
	 * @return CellStyle 列头样式
	 */
	protected CellStyle headerCellStyle(Workbook workbook) {
		Font headFont = workbook.createFont();
		headFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		headFont.setFontName("宋体");
		headFont.setFontHeightInPoints((short) 22);

		CellStyle headStyle = workbook.createCellStyle();
		headStyle.setFont(headFont);
		headStyle.setAlignment(CellStyle.ALIGN_CENTER);
		headStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		return headStyle;
	}
	
	/**
	 * 导出加样式，带黑色边框
	 * @param workbook
	 * @return {@code CellStyle}
	 */
	protected CellStyle bodyCellStyle(Workbook workbook){
		return bodyCellStyle(workbook, true);
	}
	/**
	 * 历史记录导出加样式，带黑色边框
	 * @param workbook
	 * @return {@code CellStyle}
	 */
	protected CellStyle historyBodyCellStyle(Workbook workbook){
		return historyBodyCellStyle(workbook, true);
	}
	
	/**
	 * 历史记录导出加样式，带黑色边框
	 * @param workbook
	 * @return {@code CellStyle}
	 */
	protected CellStyle historyBodyCellStyle(Workbook workbook, boolean hasBorder){
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setAlignment(CellStyle.ALIGN_LEFT);//设置左对齐
		cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		cellStyle.setWrapText(true);
		if (!hasBorder) {
			return cellStyle;
		}
		//导出的Execl加黑线
		cellStyle.setBorderBottom(CellStyle.BORDER_THIN); //下边框
		cellStyle.setBorderLeft(CellStyle.BORDER_THIN);//左边框
		cellStyle.setBorderTop(CellStyle.BORDER_THIN);//上边框
		cellStyle.setBorderRight(CellStyle.BORDER_THIN);//右边框
		return cellStyle;
	}
	/**
	 * 导出加样式，带黑色边框
	 * @param workbook
	 * @return {@code CellStyle}
	 */
	protected CellStyle bodyCellStyle(Workbook workbook, boolean hasBorder){
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
		cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		cellStyle.setWrapText(true);
		if (!hasBorder) {
			return cellStyle;
		}
		//导出的Execl加黑线
		cellStyle.setBorderBottom(CellStyle.BORDER_THIN); //下边框
		cellStyle.setBorderLeft(CellStyle.BORDER_THIN);//左边框
		cellStyle.setBorderTop(CellStyle.BORDER_THIN);//上边框
		cellStyle.setBorderRight(CellStyle.BORDER_THIN);//右边框
		return cellStyle;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	
}
