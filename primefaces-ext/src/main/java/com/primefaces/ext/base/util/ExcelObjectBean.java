package com.primefaces.ext.base.util;

import java.util.List;

public class ExcelObjectBean {
	private int startRow;

	private int sheetAt;

	private int messageColumn;
	private String beanName;
	private List<ExcelColumnBean> columns;

	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public int getStartRow() {
		return startRow;
	}

	public List<ExcelColumnBean> getColumns() {
		return columns;
	}

	public void setColumns(List<ExcelColumnBean> columns) {
		this.columns = columns;
	}

	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}

	public int getSheetAt() {
		return sheetAt;
	}

	public void setSheetAt(int sheetAt) {
		this.sheetAt = sheetAt;
	}

	public int getMessageColumn() {
		return messageColumn;
	}

	public void setMessageColumn(int messageColumn) {
		this.messageColumn = messageColumn;
	}

	@Override
	public String toString() {
		return "ExcelObjectBean [startRow=" + startRow + ", sheetAt=" + sheetAt + ", messageColumn=" + messageColumn + ", columns=" + columns + "]";
	}

}
