package com.nk.excel.handler;

import com.nk.excel.entity.XRow;

public interface RowReaderHandler {

	/**
	 * @param row
	 */
	public void readRows(XRow row);
}
