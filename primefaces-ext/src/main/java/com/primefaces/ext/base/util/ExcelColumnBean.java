package com.primefaces.ext.base.util;

public class ExcelColumnBean {

	private int index;

	private boolean required;

	private String pattern;
	private String fieldName;
	private String cellDataType;
	private String query;
	private int maxLength;

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public int getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	private String cellHeader;

	public int getIndex() {
		return index;
	}

	public String getCellDataType() {
		return cellDataType;
	}

	public void setCellDataType(String cellDataType) {
		this.cellDataType = cellDataType;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public String getCellHeader() {
		return cellHeader;
	}

	public void setCellHeader(String cellHeader) {
		this.cellHeader = cellHeader;
	}

	@Override
	public String toString() {
		return "ExcelColumnBean [index=" + index + ", required=" + required + ", pattern=" + pattern + ", fieldName=" + fieldName + ", cellDataType="
				+ cellDataType + ", query=" + query + ", maxLength=" + maxLength + ", cellHeader=" + cellHeader + "]";
	}

}
