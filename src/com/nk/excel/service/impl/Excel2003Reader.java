package com.nk.excel.service.impl;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.eventusermodel.EventWorkbookBuilder.SheetRecordCollectingListener;
import org.apache.poi.hssf.eventusermodel.FormatTrackingHSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFEventFactory;
import org.apache.poi.hssf.eventusermodel.HSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFRequest;
import org.apache.poi.hssf.eventusermodel.MissingRecordAwareHSSFListener;
import org.apache.poi.hssf.eventusermodel.dummyrecord.LastCellOfRowDummyRecord;
import org.apache.poi.hssf.eventusermodel.dummyrecord.MissingCellDummyRecord;
import org.apache.poi.hssf.record.BOFRecord;
import org.apache.poi.hssf.record.BlankRecord;
import org.apache.poi.hssf.record.BoolErrRecord;
import org.apache.poi.hssf.record.BoundSheetRecord;
import org.apache.poi.hssf.record.FormulaRecord;
import org.apache.poi.hssf.record.LabelRecord;
import org.apache.poi.hssf.record.LabelSSTRecord;
import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.SSTRecord;
import org.apache.poi.hssf.record.StringRecord;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import com.nk.excel.entity.XCell;
import com.nk.excel.entity.XRow;
import com.nk.excel.handler.RowReaderHandler;
import com.nk.excel.service.ExcelReader;

public class Excel2003Reader implements ExcelReader, HSSFListener {

	private int minColumns = -1;

	private POIFSFileSystem fs;

	private int lastRowNumber;

	private int lastColumnNumber;

	private RowReaderHandler readerHandler;

	/** Should we output the formula, or the value it has? */
	private boolean outputFormulaValues = true;

	/** For parsing Formulas */
	private SheetRecordCollectingListener workbookBuildingListener;
	// excel2003工作薄
	private HSSFWorkbook stubWorkbook;

	// Records we pick up as we process
	private SSTRecord sstRecord;
	private FormatTrackingHSSFListener formatListener;

	// 表索引
	private int sheetIndex = -1;
	private BoundSheetRecord[] orderedBSRs;
	private List<BoundSheetRecord> boundSheetRecords = new ArrayList<BoundSheetRecord>();

	// For handling formulas with string results
	private int nextRow;
	private int nextColumn;
	private boolean outputNextStringRecord;
	// 当前行
	private int curRow = 0;

	private int optSheetIndex = -1;
	private XRow row = new XRow();

	@Override
	public void setRowReaderHandler(RowReaderHandler readerHandler) {
		this.readerHandler = readerHandler;
	}

	@Override
	public void processOneSheet(String filename, int optSheetIndex) throws IOException {
		this.optSheetIndex = optSheetIndex;
		processAllSheet(filename);
	}

	@Override
	public void processAllSheet(String filename) throws FileNotFoundException,
			IOException {
		this.fs = new POIFSFileSystem(new FileInputStream(filename));
		MissingRecordAwareHSSFListener listener = new MissingRecordAwareHSSFListener(this);
		formatListener = new FormatTrackingHSSFListener(listener);

		HSSFEventFactory factory = new HSSFEventFactory();
		HSSFRequest request = new HSSFRequest();
		workbookBuildingListener = new SheetRecordCollectingListener(formatListener);
		if(outputFormulaValues) {
			request.addListenerForAllRecords(formatListener);
		} else {
			request.addListenerForAllRecords(workbookBuildingListener);
		}
		factory.processWorkbookEvents(request, fs);
	}

	/**
	 * This method listens for incoming records and handles them as required.
	 * 
	 * @param record
	 *            The record that was found while reading.
	 */
	@Override
	public void processRecord(Record record) {
		int thisRow = -1;
		int thisColumn = -1;
		String thisStr = null;
		String value = null;
		switch (record.getSid()) {
		case BoundSheetRecord.sid:
			boundSheetRecords.add((BoundSheetRecord)record);
			break;
		case BOFRecord.sid:
			BOFRecord br = (BOFRecord) record;
			if (br.getType() == BOFRecord.TYPE_WORKSHEET) {
				// 如果有需要，则建立子工作薄
				if (workbookBuildingListener != null && stubWorkbook == null) {
					stubWorkbook = workbookBuildingListener.getStubHSSFWorkbook();
				}
				sheetIndex++;
				if (orderedBSRs == null) {
					orderedBSRs = BoundSheetRecord.orderByBofPosition(boundSheetRecords);
				}
			}
			break;
		case SSTRecord.sid:
			sstRecord = (SSTRecord) record;
			break;
		case BlankRecord.sid:
			BlankRecord brec = (BlankRecord) record;
			thisRow = brec.getRow();
			thisColumn = brec.getColumn();
			thisStr = "";
			break;
		case BoolErrRecord.sid: // 单元格为布尔类型
			BoolErrRecord berec = (BoolErrRecord) record;
			thisRow = berec.getRow();
			thisColumn = berec.getColumn();
			thisStr = berec.getBooleanValue() + "";
			break;
		case FormulaRecord.sid: // 单元格为公式类型
			FormulaRecord frec = (FormulaRecord) record;
			thisRow = frec.getRow();
			thisColumn = frec.getColumn();
			
			if(outputFormulaValues) {
				if(frec.hasCachedResultString()) {
					outputNextStringRecord = true;
					// Formula result is a string
					// This is stored in the next record
					nextRow = frec.getRow();
					nextColumn = frec.getColumn();
				} else {
					outputNextStringRecord = false;
					thisStr = formatListener.formatNumberDateCell(frec);
				}
			}
			break;
		case StringRecord.sid:// 单元格中公式的字符串
			if (outputNextStringRecord) {
				// String for formula
				StringRecord srec = (StringRecord) record;
				thisStr = srec.getString();
				thisRow = nextRow;
				thisColumn = nextColumn;
				outputNextStringRecord = false;
			}
			break;
		case LabelRecord.sid:
			LabelRecord lrec = (LabelRecord) record;
			curRow = thisRow = lrec.getRow();
			thisColumn = lrec.getColumn();
			value = lrec.getValue().trim();
			value = value.equals("") ? " " : value;
			thisStr = value;
			break;
		case LabelSSTRecord.sid: // 单元格为字符串类型
			LabelSSTRecord lsrec = (LabelSSTRecord) record;
			curRow = thisRow = lsrec.getRow();
			thisColumn = lsrec.getColumn();
			if (sstRecord == null) {
				thisStr = " ";
			} else {
				value = sstRecord.getString(lsrec.getSSTIndex()).toString().trim();
				value = value.equals("") ? " " : value;
				thisStr = value;
			}
			break;
		case NumberRecord.sid: // 单元格为数字类型
			NumberRecord numrec = (NumberRecord) record;
			curRow = thisRow = numrec.getRow();
			thisColumn = numrec.getColumn();
			value = String.valueOf(numrec.getValue());
			value = value.equals("") ? " " : value;
			thisStr = value;
			break;
		default:
			break;
		}

		// 如果不是要操作的sheet，跳过
		if ((sheetIndex + 1) != optSheetIndex && optSheetIndex != -1) {
			return;
		}

		// 遇到新行的操作
		if (thisRow != -1 && thisRow != lastRowNumber) {
			lastColumnNumber = -1;
		}

		// 空值的操作
		if (record instanceof MissingCellDummyRecord) {
			MissingCellDummyRecord mc = (MissingCellDummyRecord) record;
			curRow = thisRow = mc.getRow();
			thisColumn = mc.getColumn();
			thisStr = " ";
		}

		// If we got something to print out, do so
		if (thisStr != null) {
			XCell cell = new XCell();
			cell.setRowIndex(curRow + 1);
			cell.setColumnIndex(thisColumn + 'A');
			cell.setValue(thisStr);
			row.addCell(cell);
		}

		// 更新行和列的值
		if (thisRow > -1)
			lastRowNumber = thisRow;
		if (thisColumn > -1)
			lastColumnNumber = thisColumn;

		// 行结束时的操作
		if (record instanceof LastCellOfRowDummyRecord) {
			if (minColumns > 0) {
				// 列值重新置空
				if (lastColumnNumber == -1) {
					lastColumnNumber = 0;
				}
			}
			lastColumnNumber = -1;
			row.setSheetIndex(sheetIndex + 1);
			row.setRowIndex(curRow);
			// End the row
			if(!row.getCells().isEmpty() && !isBlankRow(row)){
				readerHandler.readRows(row);
			}
			row = new XRow();
			curRow++;
		}
	}

	private boolean isBlankRow(XRow row) {
		boolean flag = false;
		for (int i = 0; i < row.getCellsSize(); i++) {
			XCell cell = row.getCell(i);
			boolean isBlank = StringUtils.isBlank(cell.getValue());
			if(i == 0){
				flag = isBlank;
				continue;
			}
			flag = flag & isBlank;
		}
		return flag;
	}
}
