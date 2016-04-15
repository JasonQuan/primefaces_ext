package com.primefaces.ext.base.web.view.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;

import com.primefaces.ext.base.entity.AbstractEntity;
import com.primefaces.ext.base.util.ColumnHelper;

/**
 * TODO:caching all data
 *
 * @author Jason
 * @date Apr 19, 2015
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "BASE_COLUMN_MODEL")
public class BaseColumnModel extends AbstractEntity implements Serializable {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @Column(name = "ID", nullable = false, length = 32)
    private String id;

    @ColumnHelper(sort = "1", editAble = true, filterOptions = "entityItems", style = "float: left;")
    @Column(name = "ENTITY", length = 255)
    private String entity;

    @ColumnHelper(editAble = false, visible = false)
    @Column(name = "SORT", length = 255)
    private String sort;

    @ColumnHelper(sort = "3", style = "float: left;")
    @Column(name = "HEADER", length = 255)
    private String header;

    @ColumnHelper(sort = "2", editAble = true, style = "float: left;")
    @Column(name = "FIELD", length = 255)
    private String field;

    @ColumnHelper(sort = "4")
    @Column(name = "EDIT")
    private Boolean edit;

    @ColumnHelper(sort = "5", editAble = true)
    @Column(name = "CUSTOM_KEY")
    private String customsKey;

    @ColumnHelper
    @Column(name = "HEADER_CN", length = 255)
    private String headerCn;

    @ColumnHelper(sort = "7", editAble = true, style = "float: left;")
    @Column(name = "TABLE_COLUMN", length = 255)
    private String tableColumn;

    @ColumnHelper(sort = "6", editAble = true, style = "float: left;")
    @Column(name = "DATA_TYPE")
    private String dataType;

    @ColumnHelper
    @Column(name = "SORT_BY")
    private String sortBy;

    @ColumnHelper
    @Column(name = "STYLE")
    private String style;

    @ColumnHelper
    @Column(name = "STYLE_CLASS")
    private String styleClass;

    @ColumnHelper
    @Column(name = "SORT_FUNCTION")
    private String sortFunction;

    @ColumnHelper
    @Column(name = "FILTER_BY")
    private String filterBy;

    @ColumnHelper
    @Column(name = "FILTER_STYLE")
    private String filterStyle;

    @ColumnHelper
    @Column(name = "FILTER_STYLE_CLASS")
    private String filterStyleClass;

    @ColumnHelper
    @Column(name = "FILTER_OPTIONS")
    private String filterOptions;

    @ColumnHelper
    @Column(name = "IS_MULTI_FILTER")
    private Boolean isMultiFilter = Boolean.FALSE;

    @ColumnHelper
    @Column(name = "FILTER_MATCH_MODE")
    private String filterMatchMode;

    @ColumnHelper
    @Column(name = "FILTER_POSITION")
    private String filterPosition;

    @ColumnHelper
    @Column(name = "FILTER_VALUE")
    private String filterValue;
    @ColumnHelper
    @Column(name = "FILTER_MAX_LENGTH")
    private String filterMaxLength;
    @ColumnHelper
    @Column(name = "RESIZABLE")
    private Boolean resizable = Boolean.TRUE;
    @ColumnHelper
    @Column(name = "EXPORT_TABLE")
    private Boolean exportable = Boolean.TRUE;
    @ColumnHelper
    @Column(name = "WIDTH")
    private String width;
    @ColumnHelper
    @Column(name = "TOGGLE_ABLE")
    private Boolean toggleable = Boolean.TRUE;
    @ColumnHelper
    @Column(name = "FILTER_FUNCTION")
    private String filterFunction;
    @ColumnHelper
    @Column(name = "PRIORITY")
    private Boolean priority;
    @ColumnHelper
    @Column(name = "SORT_ABLE")
    private Boolean sortable = Boolean.TRUE;
    @ColumnHelper
    @Column(name = "FILTER_ABLE")
    private Boolean filterable = Boolean.TRUE;
    @ColumnHelper
    @Column(name = "VISIBLE")
    private Boolean visible = Boolean.TRUE;
    @ColumnHelper
    @Column(name = "SELECT_ROW")
    private String selectRow;
    @ColumnHelper
    @Column(name = "ROW_SPAN")
    private String rowspan;
    @ColumnHelper
    @Column(name = "COLS_SPAN")
    private String colspan;
    @ColumnHelper
    @Column(name = "FOOTER")
    private String footer;
//	@ColumnHelper
//	@Column(name = "GROUP")
    @Transient
    private String group;

    @ColumnHelper
    @Column(name = "VALIDATOR_MESSAGE")
    private String validatorMessage;
    @ColumnHelper
    @Column(name = "VALIDATOR_REGEX")
    private String validateRegex;
    @ColumnHelper
    @Column(name = "VALIDATOR_ID")
    private String validatorId;
    @ColumnHelper
    @Column(name = "DROP_DOWN")
    private String dropDown;

    @ColumnHelper(tips = "extension for same options,can be button, picture. the value must is a method name in manager bean,if the column is not from database")
    @Column(name = "EXT_FUNCTION")
    private String extFunction;
    @ColumnHelper
    @Column(name = "EXT_ICON")
    private String extIcon;
    @ColumnHelper
    @Column(name = "ONUPDATE")
    private String onupdate;
    @ColumnHelper
    @Column(name = "ONCOMPLETE")
    private String oncomplete;
    @ColumnHelper
    @Column(name = "ONSTART")
    private String onstart;
    @ColumnHelper
    @Column(name = "ONSUCCESS")
    private String onsuccess;
    @ColumnHelper
    @Column(name = "TITLE")
    private String title;
    @ColumnHelper
    @Column(name = "EXT_VALUE")
    private String extValue;
    @ColumnHelper
    @Column(name = "FILTER_PLACE_HOLDER")
    private String filterPlaceHolder;
    @ColumnHelper
    @Column(name = "AUTO_COMPLETE")
    private String autoComplete;
    @ColumnHelper(tips = "support button and select currently")
    @Column(name = "EXT_TYPE")
    private String extType;

    @ColumnHelper
    @Column(name = "OUT_FORMAT")
    private String outFormat;

    @Column(name = "TIPS")
    private String tips;
    /**
     * #{entity[column.extRendered]}
     */
    @Column(name = "EXT_RENDERED")
    private String extRendered;
    @Column(name = "AJAX_EVENT")
    private String ajaxEvent;
    @Column(name = "AJAX_LISENTER")
    private String ajaxLisenter;
    @Column(name = "AJAX_PROCESS")
    private String ajaxProcess;
    @Column(name = "AJAX_UPDATE")
    private String ajaxUpdate;
    @Column(name = "INCLUDE")
    private String include;

    @Transient
    private Boolean hasInclude;
    @Transient
    private Boolean hasValidateRegex;
    @Transient
    private Boolean hasAjaxEvent;
    @Transient
    private Boolean hasValidatorId;
    @Transient
    private Boolean hasDropDown;
    @Transient
    private Boolean hasFilterOptions;
    @Transient
    private Boolean hasExtFunction;
    @Transient
    private Boolean hasAutoComplete;
    @Transient
    private Boolean hasOutFormat;
    @Transient
    private Boolean isBoolean;
    @Transient
    private Boolean isDate;
    @Transient
    private Boolean isString;
    @Transient
    private Boolean isInteger;
    @Transient
    private Boolean isShort;
    @Transient
    private Boolean isCharacter;
    @Transient
    private Boolean isBigInteger;
    @Transient
    private Boolean isBigDecimal;

    @Transient
    private Boolean isExtInclude;
    @Transient
    private Boolean isExtButton;
    @Transient
    private Boolean isExtSelect;

    public BaseColumnModel() {
    }

    public String getDataType() {
        if (StringUtils.isBlank(dataType)) {
            dataType = "";
        }
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getStyleClass() {
        return styleClass;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    public String getSortFunction() {
        return sortFunction;
    }

    public void setSortFunction(String sortFunction) {
        this.sortFunction = sortFunction;
    }

    public String getFilterBy() {
        return filterBy;
    }

    public void setFilterBy(String filterBy) {
        this.filterBy = filterBy;
    }

    public String getFilterStyle() {
        return filterStyle;
    }

    public void setFilterStyle(String filterStyle) {
        this.filterStyle = filterStyle;
    }

    public String getFilterStyleClass() {
        return filterStyleClass;
    }

    public void setFilterStyleClass(String filterStyleClass) {
        this.filterStyleClass = filterStyleClass;
    }

    public String getFilterOptions() {
        return filterOptions;
    }

    public void setFilterOptions(String filterOptions) {
        this.filterOptions = filterOptions;
    }

    public String getFilterMatchMode() {
        return filterMatchMode;
    }

    public void setFilterMatchMode(String filterMatchMode) {
        this.filterMatchMode = filterMatchMode;
    }

    public String getFilterPosition() {
        return filterPosition;
    }

    public void setFilterPosition(String filterPosition) {
        this.filterPosition = filterPosition;
    }

    public String getFilterValue() {
        return filterValue;
    }

    public void setFilterValue(String filterValue) {
        this.filterValue = filterValue;
    }

    public String getRowspan() {
        return rowspan;
    }

    public void setRowspan(String rowspan) {
        this.rowspan = rowspan;
    }

    public String getColspan() {
        return colspan;
    }

    public void setColspan(String colspan) {
        this.colspan = colspan;
    }

    public String getFooter() {
        return footer;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    public String getFilterMaxLength() {
        return filterMaxLength;
    }

    public void setFilterMaxLength(String filterMaxLength) {
        this.filterMaxLength = filterMaxLength;
    }

    public Boolean getResizable() {
        return resizable;
    }

    public void setResizable(Boolean resizable) {
        this.resizable = resizable;
    }

    public Boolean getExportable() {
        return exportable;
    }

    public void setExportable(Boolean exportable) {
        this.exportable = exportable;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public Boolean getToggleable() {
        if (toggleable == null) {
            toggleable = Boolean.TRUE;
        }
        return toggleable;
    }

    public void setToggleable(Boolean toggleable) {
        this.toggleable = toggleable;
    }

    public String getFilterFunction() {
        return filterFunction;
    }

    public void setFilterFunction(String filterFunction) {
        this.filterFunction = filterFunction;
    }

    public Boolean getPriority() {
        return priority;
    }

    public void setPriority(Boolean priority) {
        this.priority = priority;
    }

    public Boolean getSortable() {
        return sortable;
    }

    public void setSortable(Boolean sortable) {
        this.sortable = sortable;
    }

    public Boolean getFilterable() {
        return filterable;
    }

    public void setFilterable(Boolean filterable) {
        this.filterable = filterable;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public String getSelectRow() {
        return selectRow;
    }

    public void setSelectRow(String selectRow) {
        this.selectRow = selectRow;
    }

    public String getCustomsKey() {
        return customsKey;
    }

    public void setCustomsKey(String customsKey) {
        this.customsKey = customsKey;
    }

    public String getHeaderCn() {
        return headerCn;
    }

    public void setHeaderCn(String headerCn) {
        this.headerCn = headerCn;
    }

    public String getTableColumn() {
        return tableColumn;
    }

    public void setTableColumn(String tableColumn) {
        this.tableColumn = tableColumn;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public Boolean getEdit() {
        if (edit == null) {
            edit = Boolean.FALSE;
        }
        return edit;
    }

    public void setEdit(Boolean edit) {
        this.edit = edit;
    }

    public void setHasValidatorId(Boolean hasValidatorId) {
        this.hasValidatorId = hasValidatorId;
    }

    public String getValidatorMessage() {
        return validatorMessage;
    }

    public void setValidatorMessage(String validatorMessage) {
        this.validatorMessage = validatorMessage;
    }

    public String getValidateRegex() {
        return validateRegex;
    }

    public void setValidateRegex(String validateRegex) {
        this.validateRegex = validateRegex;
    }

    public String getValidatorId() {
        return validatorId;
    }

    public void setValidatorId(String validatorId) {
        this.validatorId = validatorId;
    }

    public String getDropDown() {
        return dropDown;
    }

    public void setDropDown(String dropDown) {
        this.dropDown = dropDown;
    }

    public Boolean getHasValidateRegex() {
        hasValidateRegex = StringUtils.isNotBlank(validateRegex);
        return hasValidateRegex;
    }

    public void setHasValidateRegex(Boolean hasValidateRegex) {
        this.hasValidateRegex = hasValidateRegex;
    }

    public Boolean getHasValidatorId() {
        hasValidatorId = StringUtils.isNotBlank(validatorId);
        return hasValidatorId;
    }

    public Boolean getHasDropDown() {
        hasDropDown = StringUtils.isNotBlank(dropDown);
        return hasDropDown;
    }

    public void setHasDropDown(Boolean hasDropDown) {
        this.hasDropDown = hasDropDown;
    }

    public Boolean getHasFilterOptions() {
        hasFilterOptions = StringUtils.isNotBlank(filterOptions);
        return hasFilterOptions;
    }

    public void setHasFilterOptions(Boolean hasFilterOptions) {
        this.hasFilterOptions = hasFilterOptions;
    }

    public String getFilterPlaceHolder() {
        return filterPlaceHolder;
    }

    public void setFilterPlaceHolder(String filterPlaceHolder) {
        this.filterPlaceHolder = filterPlaceHolder;
    }

    public String getAutoComplete() {
        return autoComplete;
    }

    public void setAutoComplete(String autoComplete) {
        this.autoComplete = autoComplete;
    }

    public Boolean getHasAutoComplete() {
        hasAutoComplete = StringUtils.isNotBlank(autoComplete);
        return hasAutoComplete;
    }

    public void setHasAutoComplete(Boolean hasAutoComplete) {
        this.hasAutoComplete = hasAutoComplete;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getExtFunction() {
        return extFunction;
    }

    public void setExtFunction(String extFunction) {
        this.extFunction = extFunction;
    }

    public String getExtIcon() {
        if (extIcon == null) {
            extIcon = "ui-icon-search";
        }
        return extIcon;
    }

    public void setExtIcon(String extIcon) {
        this.extIcon = extIcon;
    }

    public String getOnupdate() {
        return onupdate;
    }

    public void setOnupdate(String onupdate) {
        this.onupdate = onupdate;
    }

    public String getOncomplete() {
        return oncomplete;
    }

    public void setOncomplete(String oncomplete) {
        this.oncomplete = oncomplete;
    }

    public String getOnstart() {
        return onstart;
    }

    public void setOnstart(String onstart) {
        this.onstart = onstart;
    }

    public String getOnsuccess() {
        return onsuccess;
    }

    public void setOnsuccess(String onsuccess) {
        this.onsuccess = onsuccess;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExtValue() {
        return extValue;
    }

    public void setExtValue(String extValue) {
        this.extValue = extValue;
    }

    public Boolean getHasExtFunction() {
        hasExtFunction = getIsExtButton() || getIsExtSelect() || getIsExtInclude();
        return hasExtFunction;
    }

    public void setHasExtFunction(Boolean hasExtFunction) {
        this.hasExtFunction = hasExtFunction;
    }

    public String getExtRendered() {
        if (StringUtils.isBlank(extRendered)) {
            extRendered = "rendered";
        }
        return extRendered;
    }

    public void setExtRendered(String extRendered) {
        this.extRendered = extRendered;
    }

    public Boolean getIsBoolean() {
        isBoolean = "java.lang.Boolean".equals(getDataType()) || "boolean".equals(getDataType());
        return isBoolean;
    }

    public void setIsBoolean(Boolean isBoolean) {
        this.isBoolean = isBoolean;
    }

    public Boolean getIsDate() {
        isDate = "java.util.Date".equals(getDataType());
        return isDate;
    }

    public void setIsDate(Boolean isDate) {
        this.isDate = isDate;
    }

    public Boolean getIsString() {
        isString = "java.lang.String".equals(getDataType());
        return isString;
    }

    public void setIsString(Boolean isString) {
        this.isString = isString;
    }

    public Boolean getIsInteger() {
        isInteger = "java.lang.Integer".equals(getDataType()) || "int".equals(getDataType());
        return isInteger;
    }

    public void setIsInteger(Boolean isInteger) {
        this.isInteger = isInteger;
    }

    public Boolean getIsShort() {
        isShort = "java.lang.Short".equals(getDataType());
        return isShort;
    }

    public void setIsShort(Boolean isShort) {
        this.isShort = isShort;
    }

    public Boolean getIsCharacter() {
        isCharacter = "java.lang.Character".equals(getDataType());
        return isCharacter;
    }

    public void setIsCharacter(Boolean isCharacter) {
        this.isCharacter = isCharacter;
    }

    public Boolean getIsBigInteger() {
        isBigInteger = "java.math.BigInteger".equals(getDataType());
        return isBigInteger;
    }

    public void setIsBigInteger(Boolean isBigInteger) {
        this.isBigInteger = isBigInteger;
    }

    public Boolean getIsBigDecimal() {
        isBigDecimal = "java.math.BigDecimal".equals(getDataType());
        return isBigDecimal;
    }

    public void setIsBigDecimal(Boolean isBigDecimal) {
        this.isBigDecimal = isBigDecimal;
    }

    public String getExtType() {
        if (StringUtils.isBlank(extType)) {
            extType = "";
        }
        return extType;
    }

    public void setExtType(String extType) {
        this.extType = extType;
    }

    public Boolean getIsExtInclude() {
        isExtInclude = getExtType().equals("include");
        return isExtInclude;
    }

    public void setIsExtInclude(Boolean isExtInclude) {
        this.isExtInclude = isExtInclude;
    }

    public Boolean getIsExtButton() {
        isExtButton = getExtType().equals("button");
        return isExtButton;
    }

    public void setIsExtButton(Boolean isExtButton) {
        this.isExtButton = isExtButton;
    }

    public Boolean getIsExtSelect() {
        isExtSelect = getExtType().equals("select");
        return isExtSelect;
    }

    public void setIsExtSelect(Boolean isExtSelect) {
        this.isExtSelect = isExtSelect;
    }

    public String getOutFormat() {
        return outFormat;
    }

    public void setOutFormat(String outFormat) {
        this.outFormat = outFormat;
    }

    public Boolean getHasOutFormat() {
        hasOutFormat = StringUtils.isNotBlank(outFormat);
        return hasOutFormat;
    }

    public Boolean getIsMultiFilter() {
        return isMultiFilter;
    }

    public void setIsMultiFilter(Boolean isMultiFilter) {
        this.isMultiFilter = isMultiFilter;
    }

    public String getAjaxEvent() {
        return ajaxEvent;
    }

    public void setAjaxEvent(String ajaxEvent) {
        this.ajaxEvent = ajaxEvent;
    }

    public String getAjaxLisenter() {
        return ajaxLisenter;
    }

    public void setAjaxLisenter(String ajaxLisenter) {
        this.ajaxLisenter = ajaxLisenter;
    }

    public String getAjaxProcess() {
        return ajaxProcess;
    }

    public void setAjaxProcess(String ajaxProcess) {
        this.ajaxProcess = ajaxProcess;
    }

    public String getAjaxUpdate() {
        return ajaxUpdate;
    }

    public void setAjaxUpdate(String ajaxUpdate) {
        this.ajaxUpdate = ajaxUpdate;
    }

    public Boolean getHasAjaxEvent() {
        hasAjaxEvent = StringUtils.isNotBlank(ajaxEvent);
        return hasAjaxEvent;
    }

    public void setHasAjaxEvent(Boolean hasAjaxEvent) {
        this.hasAjaxEvent = hasAjaxEvent;
    }

    public String getInclude() {
        return include;
    }

    public void setInclude(String include) {
        this.include = include;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
