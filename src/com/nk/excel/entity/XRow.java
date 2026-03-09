package com.nk.excel.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Excel行对象封装
 */
public class XRow {
	private int sheetIndex;
	private int rowIndex;
	private List<XCell> cells = new ArrayList<XCell>();

	public int getSheetIndex() {
		return sheetIndex;
	}

	public void setSheetIndex(int sheetIndex) {
		this.sheetIndex = sheetIndex;
	}

	public int getRowIndex() {
		return rowIndex;
	}

	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}

	public int getCellsSize() {
		return cells.size();
	}

	public XRow addCell(XCell cell) {
		this.cells.add(cell);
		return this;
	}

	public XCell getCell(int cellIndex) {
		return cells.get(cellIndex);
	}

	public List<XCell> getCells() {
		return cells;
	}

	public void setCells(List<XCell> cells) {
		this.cells = cells;
	}

	@Override
	public String toString() {
		return "XRow [sheetIndex=" + sheetIndex + ", rowIndex=" + rowIndex + ", cells=" + cells + "]";
	}
	
}
