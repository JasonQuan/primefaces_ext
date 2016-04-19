package com.primefaces.ext.base.web;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.persistence.metamodel.SingularAttribute;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.ColumnResizeEvent;
import org.primefaces.event.ReorderEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleSelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.event.data.PageEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.StreamedContent;

import com.primefaces.ext.base.ejb.BaseEJB;
import com.primefaces.ext.base.entity.AbstractEntity;
import com.primefaces.ext.base.util.BaseKeyValue;
import com.primefaces.ext.base.util.BaseLogger;
import com.primefaces.ext.base.util.MessageBundle;
import com.primefaces.ext.base.util.ObjectUtil;
import com.primefaces.ext.base.web.view.dao.BaseColumnModelSB;
import com.primefaces.ext.base.web.view.entity.BaseColumnModel;
import com.primefaces.ext.base.web.view.entity.BaseColumnModel_;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/**
 *
 * @author Jason
 *
 * @param <T> DataTable数据类型
 * @param <E> DataTable数据类型，开发测试中，目前跟T保持一致
 */
@SuppressWarnings({"serial", "rawtypes", "unchecked"})
public abstract class BaseMB<T extends AbstractEntity, E extends AbstractEntity> implements Serializable {

    protected BaseLogger logger = new BaseLogger(this.getClass());

    /**
     * get BaseDao
     *
     * @return BaseDao
     */
    protected abstract BaseEJB<T, E> dao();

    /**
     * the value for select item label
     *
     * @return
     */
    protected SingularAttribute<?, ?> selectItemLabel() {
        return null;
    }

    private LazyEntityDataModel<T, E> dataModel;
    private T entity;
    /**
     * 实 体 数 组，用 于 datatable 多 选
     */
    private List<T> entitys;

    // t est
    public BaseMB() {
    }

    /**
     * get Request Parameter
     *
     * @param key
     * @return String
     */
    public String getRequestParameter(String key) {
        try {
            Map<String, String> requestParameter = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
            return requestParameter.get(key);
        } catch (NullPointerException e) {
            logger.debug("[no parameter with key :]" + key);
        } catch (Exception e) {
            logger.error(e);
        }
        return null;
    }

    protected void afterRemove() {
    }

    protected void afterUpdate() {
    }

    // TODO: final can not overwrite
    public final void remove() {
        remove(entity);
    }

    public void remove(T t) {
        // TODO: delete condaition
        MessageBundle.autoMessage(dao().remove(t));
        afterRemove();
    }

    /**
     * go to list page, if update successful.
     *
     */
    public void update() {
        FacesMessage message = dao().update(entity);
        MessageBundle.autoMessage(message);
        afterUpdate();
        // if (message.getSeverity().equals(FacesMessage.SEVERITY_INFO)) {
        // reset();
        // FacesContext.getCurrentInstance().getExternalContext().redirect("a.hxtml");
        // }
    }

    // public void persist() {
    // if (entity.isNew()) {
    // create();
    // } else {
    // update();
    // }
    // }
    /**
     * get BaseConverter
     *
     * @return BaseConverter
     */
    public BaseConverter<T> getConverter() {
        return new BaseConverter<T>(dao());
    }

    /**
     *
     * get primefaces LazyDataModel<T>
     *
     * @throws FacesException , must lazy="true"
     * @return LazyDataModel<T>
     */
    public LazyEntityDataModel<T, E> getDataModel() {
        if (dataModel == null) {
            dataModel = new LazyEntityDataModel<T, E>(getDataModelJpql(), dao(), allColumns, getJpqlParameters());
        }

        return dataModel;
    }

    protected Map<String, Object> getJpqlParameters() {
        return null;
    }

    protected String getExcelFileName() {
        return null;

    }

    /**
     *
     *
     * @return JPQL
     */
    protected String getDataModelJpql() {
        return "SELECT o FROM " + dao().getEntityClass().getSimpleName() + " o where 1 = 1 ";
    }

    public void setDataModel(LazyEntityDataModel<T, E> dataModel) {
        this.dataModel = dataModel;
    }

    /**
     * @deprecated get all entitys
     *
     * @return ALL entity
     */
    public List<T> all() {
        return dao().findAll();
    }

    /**
     * get getEntity()
     *
     * @return entity
     */
    public T getEntity() {
        // setEntityByRequestEntityId();
        if (entity == null) {
            try {
                entity = dao().getEntityClass().newInstance();
            } catch (Exception e) {
                logger.error(e);
            }
        }
        return entity;
    }

    public List<T> getEntitys() {
        if (this.entitys == null) {
            entitys = new ArrayList<T>();
        }

        if (!getPageSelectEntitys().isEmpty()) {
            entitys.addAll(pageSelectEntitys);
            entitys = new ArrayList<T>(new HashSet<T>(entitys));
        }

        return entitys;
    }

    private List<T> pageSelectEntitys;

    public void setEntitys(List<T> entitys) {
        /*
		 * pageSelectEntitys = getPageSelectEntitys();
		 * pageSelectEntitys.addAll(entitys);
         */
        this.entitys = entitys;
    }

    public List<T> getPageSelectEntitys() {
        if (pageSelectEntitys == null) {
            pageSelectEntitys = new ArrayList<T>();
        }
        pageSelectEntitys = new ArrayList<T>(new HashSet<T>(pageSelectEntitys));
        return pageSelectEntitys;
    }

    // public T getSelected() {
    // if (selected == null) {
    // try {
    // selected = (T) dao().getEntityClass().newInstance();
    // } catch (Exception e) {
    //
    // System.err.println(ex.getMessage());
    // }
    // }
    // return selected;
    // }
    //
    // public void setSelected(T selected) {
    // this.selected = selected;
    // }
    public void setEntity(T entity) {
        this.entity = entity;
    }

    /**
     * use for select on the view
     *
     * @param selectOne
     * @return SelectItem[]
     */
    // TODO: 抽象条件
    public SelectItem[] selectItemsI18n(Boolean selectOne) {
        return dao().getSelectItems(selectItemLabel(), null, null, selectOne, true);
    }

    public SelectItem[] selectItems(Boolean selectOne) {
        return dao().getSelectItems(selectItemLabel(), null, null, selectOne);
    }

    /**
     * clear up
     */
    public void reset() {
        entity = null;
//		this.pageSelectEntitys = null;
    }

    /**
     *
     *
     * @return entity is new
     */
    // public boolean isNew() {
    // return getEntity().isNew();
    // }
    /**
     * <br /validation unique column <br/>
     *
     * id must like addName or editName todo move<br/>
     *
     * this function to entity<br/>
     *
     * remark: Entity
     *
     * remark:unique can't work, find way to solve it, this is temporary way
     *
     * @param context
     * @param toValidate
     * @param value
     * @see dao validationUniqueColumn
     * @deprecated
     */
    // TODO:not work on page material status, attribute name
    @Deprecated
    public void unique(FacesContext context, UIComponent toValidate, Object value) {
        String viewId = toValidate.getId();

        if (verificationId(viewId)) {
            MessageBundle.validatorMessage(FacesMessage.SEVERITY_ERROR, toValidate, context, viewId, "system parameter error, call developer");
            return;
        }
        if (value == null || value.equals("")) {
            MessageBundle.validatorMessage(FacesMessage.SEVERITY_WARN, toValidate, context, "null", MessageBundle.REQUIRED);
            return;
        }

        String field = getIdByViewId(viewId);
        List<T> findByColumn = dao().findByField(field, value);
        int size = findByColumn.size();
        // if (entity.isNew()) {
        // if (size > 0) {
        // MessageBundle.validatorMessage(FacesMessage.SEVERITY_WARN,
        // toValidate, context, value.toString(),
        // MessageBundle.ALREADY_EXISTS);
        // }
        // } else {
        if (size == 1) {
            try {
                T o = findByColumn.get(0);
                Object id = o.getId();

                if (id != entity.getId()) {
                    MessageBundle.validatorMessage(FacesMessage.SEVERITY_WARN, toValidate, context, value.toString(), MessageBundle.ALREADY_EXISTS);
                }
            } catch (NullPointerException e) {
                logger.error(e);
            } catch (SecurityException e) {
                logger.error(e);
            } catch (Exception e) {
                logger.error(e);
            }
        }
        // }

    }

    protected boolean verificationId(String id) {
        boolean outcome = false;

        if (!(id.startsWith("add") || id.startsWith("edit"))) {
            String message = "this input element id must start with add or edit, and after 'add' or 'edit' must is entity attburt name";
            MessageBundle.showError(message);
            outcome = true;
        }

        return outcome;
    }

    /**
     *
     * @param str xhtml view id
     * @return field name
     */
    protected String getIdByViewId(String str) {
        str = str.replace("add", "").replace("edit", "");
        String[] a = str.split("");
        return str.replaceFirst(a[1], a[1].toLowerCase());
    }

    protected String getSetForegin(String str) {
        // str = str.replace("add", "").replace("edit", "");
        String[] a = str.split("");
        return "set" + str.replaceFirst(a[1], a[1].toUpperCase());
    }

    protected void foreign(FacesContext context, UIComponent toValidate, Object value, SingularAttribute foreignField) {
        // TODO: 校验 value
        String viewId = toValidate.getId();
        if (verificationId(viewId) || value == null || value == "") {
            return;
        }
        try {
            Class<?> entityClass = entity.getClass();
            String field = getIdByViewId(viewId);
            Field declaredField = entityClass.getDeclaredField(field);
            Class<?> foreignClass = declaredField.getType();
            Method method = entityClass.getMethod(getSetForegin(field), foreignClass);
            Object foreginEntity = dao().findByColumnAndEntityClass(foreignClass, foreignField.getName(), value);

            if (foreginEntity != null) {
                method.invoke(entity, foreginEntity);
            } else {
                MessageBundle.validatorMessage(FacesMessage.SEVERITY_WARN, toValidate, context, value.toString(), MessageBundle.DOES_NOT_EXIST);
            }
        } catch (IllegalAccessException e) {
            logger.error(e);
        } catch (InvocationTargetException e) {
            logger.error(e);
        } catch (IllegalArgumentException e) {
            logger.error(e);
        } catch (NoSuchMethodException e) {
            logger.error(e);
        } catch (SecurityException e) {
            logger.error(e);
        } catch (NoSuchFieldException e) {
            logger.error(e);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    /**
     * page redirect
     *
     * @param page page path
     */
    public void redirect(String page) {
        try {
            if (page != null && !"".equals(page)) {
                FacesContext.getCurrentInstance().getExternalContext().redirect(page);
            }
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    /**
     * in testing
     *
     * <br/>
     * this method can add foriegn entity<br/>
     *
     * value must like <b> #{entityField}<b/>
     *
     * <br/>
     * id must like <b>(add,edit) + Entity name<b/> *
     *
     * eg: <h:input id="addType" value="#{name}"
     * converter="#{**.foreignConverter()"}/>
     *
     * @param foreignField
     * @return ForeignConverter
     */
    protected ForeignConverter foreignConverter(SingularAttribute foreignField) {
        return new ForeignConverter<T, E>(foreignField, this);
    }

    /**
     *
     * DataTable column id must same as entity field
     *
     * @param event CellEditEvent
     */
    public void cellEdit(CellEditEvent event) {
        if (event.getNewValue() != event.getOldValue()) {
            try {
                beforeCellEdit();
                // String o = event.getNewValue().toString();
                String key = event.getColumn().getColumnKey();
                logger.debug("[cellEdit] " + key);
                DataTable table = (DataTable) event.getSource();
                entity = (T) table.getRowData();
                String id = entity.getId().toString();
                String[] split = key.split(":");
                key = split[split.length - 1];
                String methodName = ObjectUtil.getGetMethodNameByFiledName(key);
                BaseKeyValue typeColumn = ObjectUtil.filedNameToColumnName(entity.getClass(), key);

                String column = typeColumn.getValue();
                Object object = entity.getClass().getMethod(methodName).invoke(entity);
                String newValue = "";
                if (object instanceof AbstractEntity) {
                    newValue = ((AbstractEntity) object).getId().toString();
                } else if ("boolean".equals(typeColumn.getKey()) || "Boolean".equals(typeColumn.getKey())) {
                    String value = entity.getClass().getMethod(methodName).invoke(entity).toString();
                    newValue = "true".equals(value) ? "1" : "0";
                } else {
                    newValue = entity.getClass().getMethod(methodName).invoke(entity).toString();
                }
                FacesMessage message = dao().update(id, column, newValue);
                MessageBundle.autoMessage(message);
                afterCellEdit();
            } catch (Exception ex) {
                logger.error(ex);
                MessageBundle.showLocalWarning(MessageBundle.FAILURE);
            }
        }
    }

    public void removeById(String id) {
        MessageBundle.autoMessage(dao().remove(dao().find(id)));
    }

    public void removeByIds(String... id) {
        // TODO
        // MessageBundle.autoMessage(dao().removeBatch(getEntitys()));
    }

    public void batchRemove() {
        // TODO: remove validation
        MessageBundle.autoMessage(dao().removeBatch(getEntitys()));
    }

    public void batchRemoveByIds() {
        // TODO: remove validation
       // MessageBundle.autoMessage(dao().removeBatchByIds(selectedIds));
        MessageBundle.autoMessage(dao().removeBatch(entitys));
    }

    public void afterCellEdit() {
    }

    public void beforeCellEdit() {
    }

    /**
     * add a new entity using datatable add
     */
    public void newEmptyEntity() {
        try {
            entity = dao().getEntityClass().newInstance();
            // TODO: sort by create time usful show new entity at first row
            MessageBundle.autoMessage(dao().create(entity));
        } catch (Exception e) {
            MessageBundle.showWarning("create", "error");
            //TODO: hits
            logger.error(e);
        }
    }

    public void rowReorder(ReorderEvent event) {
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("fromIndex", event.getFromIndex());
        context.addCallbackParam("toIndex", event.getToIndex());
    }

    public void switchSort(ActionEvent event) {
        MessageBundle.autoMessage(dao().switchSort(switchSortFromId, switchSortFrom, switchSortToId, switchSortTo));
    }

    private String switchSortFromId;
    private String switchSortToId;
    private String switchSortFrom;
    private String switchSortTo;

    public String getSwitchSortFromId() {
        return switchSortFromId;
    }

    public void setSwitchSortFromId(String switchSortFromId) {
        this.switchSortFromId = switchSortFromId;
    }

    public String getSwitchSortToId() {
        return switchSortToId;
    }

    public void setSwitchSortToId(String switchSortToId) {
        this.switchSortToId = switchSortToId;
    }

    public String getSwitchSortFrom() {
        return switchSortFrom;
    }

    public void setSwitchSortFrom(String switchSortFrom) {
        this.switchSortFrom = switchSortFrom;
    }

    public String getSwitchSortTo() {
        return switchSortTo;
    }

    public void setSwitchSortTo(String switchSortTo) {
        this.switchSortTo = switchSortTo;
    }

    /**
     * bug on pf5.2: PF().filter() not working under the p:columns,so, just inputText can working on column filter for now.
     */
    private List<BaseColumnModel> columns = new ArrayList<>();

    @EJB
    private BaseColumnModelSB columnModelDao;

    /**
     * use for get customs columns
     *
     * default key is "default"
     *
     * if user need get customs column,just over write this method,
     *
     *
     *
     * @return customs key
     */
    protected String getCustomColumnsKey() {
        return "default";
    }

    protected String getCustomColumnsGroup() {
        return "global";
    }

    private List<BaseColumnModel> getAllColumns() {
        return columnModelDao.getColumnModel(dao().getEntityClass(), getCustomColumnsKey(), getCustomColumnsGroup());
    }

    public List<BaseColumnModel> getColumns() {
        if (columns == null || columns.isEmpty()) {
            initViewColums();
        }
        return columns;
    }

    /**
     * @param event
     */
    public void colReorder(AjaxBehaviorEvent event) {
        logger.info("call colReorder");
        Map<String, String> requestParameterMap = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String clientId = event.getComponent().getClientId();
        String columnOrder = requestParameterMap.get(clientId + "_columnOrder");
        try {
            String[] orders = columnOrder.split(",");
            String[] condations;
            // TODO: change to batch update
            for (String order : orders) {
                condations = order.split("-");
                columnModelDao.update(condations[0], BaseColumnModel_.sort, condations[1]);
            }
            initViewColums();
            MessageBundle.showInfo("order sucessful");
        } catch (Exception e) {
            logger.error("colReorder columnOrder:" + columnOrder);
            MessageBundle.showMessage("update columns order error", FacesMessage.SEVERITY_ERROR);
        }
        // logger.info("order: " + order);
    }

    public void resetColumns() {
        columnModelDao.resetColumns(dao().getEntityClass(), getCustomColumnsKey());
        initViewColums();
        MessageBundle.showInfo("reset custom columns sucess");
    }

    private List<String> selectColumns = new ArrayList<>();

    public List<String> getSelectColumns() {
        return selectColumns;
    }

    public void setSelectColumns(List<String> selectColumns) {
        this.selectColumns = selectColumns;
    }

    public void updateColumns() {
        StringBuilder ids = new StringBuilder("'");
        for (String bcm : selectColumns) {
            ids.append(",'").append(bcm).append("'");
        }

        FacesMessage outcome = columnModelDao.updateColumnsVisible(dao().getEntityClass().getName(), getCustomColumnsKey(),
                ids.toString().replaceFirst("',", ""), Boolean.TRUE);
        initViewColums();
        // if update sucess
        // this.columns = selectColumns;
        MessageBundle.autoMessage(outcome);
        logger.info("------------------selectColumns.size()--------" + selectColumns.size());
    }

    public SelectItem[] selectColumnsItems = new SelectItem[0];

    public SelectItem[] getSelectColumnsItems() {
        if (selectColumnsItems == null || selectColumnsItems.length == 0) {
            initViewColums();
        }
        return selectColumnsItems;
    }

    private List<BaseColumnModel> allColumns;

    protected void initViewColums() {
        allColumns = getAllColumns();
        selectColumns.clear();
        columns.clear();
        selectColumnsItems = new SelectItem[allColumns.size()];
        for (int i = 0; i < allColumns.size(); i++) {
            selectColumnsItems[i] = new SelectItem(allColumns.get(i).getId(), allColumns.get(i).getHeader());
            if (allColumns.get(i).getVisible()) {
                selectColumns.add(allColumns.get(i).getId());
                columns.add(allColumns.get(i));
            }
        }

        // TDOO sort columns
        /*
		 * Collections.sort(columns, new Comparator<BaseColumnModel>() {
		 * 
		 * public int compare(BaseColumnModel arg0, BaseColumnModel arg1) {
		 * return arg0.getSort().compareTo(arg1.getSort()); } });
         */
    }

    public void onColumnResize(ColumnResizeEvent event) {
        Map<String, String> requestParameterMap = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String clientId = event.getComponent().getClientId();
        try {
            String[] ids = requestParameterMap.get(clientId + ":newWidth").split(",");
            for (String id : ids) {
                String[] ic = id.split("-");
                if (ic.length == 2) {
                    columnModelDao.update(ic[0], BaseColumnModel_.width, ic[1] + "px");
                } else {
                    logger.warn("onColumnResize id: " + id);
                }
            }
        } catch (Exception e) {
            logger.error("onColumnResize: " + e.getMessage());
        }
        initViewColums();
    }

    public void onPage(PageEvent event) {
    }

    /**
     * 动态列方案
     * <p:ajax event="keyup" listener= * * * * * * * * * * * *
     * "#{dtColumnsView.cellEdit(car.id, column.property,car[column.property])}"
     * * * * * * * * * * * * * />
     *
     * @param id
     * @param field
     * @param value
     */
    public void cellEdit(Object id, String field, Object value) {
        try {
            logger.debug("call cellEdit:\nid:" + id + "\nfield:" + field + "\nvalue:" + value);
            // TODO: validation dataType
            // BaseKeyValue typeColumn =
            // ObjectUtil.filedNameToColumnName(dao().getEntityClass(), field);

            // String column = typeColumn.getValue();
            FacesMessage message = dao().validationField(field, value);
            if (message.getSeverity().equals(FacesMessage.SEVERITY_INFO)) {
                message = dao().updateField(id, field, value);
            }
            MessageBundle.autoMessage(message);
        } catch (Exception ex) {
            logger.error(ex);
            MessageBundle.showLocalWarning(MessageBundle.FAILURE);
        }
        // }
    }

    /**
     * @deprecated @param key
     * @return
     */
    public SelectItem[] selectItemField(String key) {
        SelectItem[] outcome = new SelectItem[0];
        try {
            Method method = this.getClass().getMethod(key);
            outcome = (SelectItem[]) method.invoke(this);
        } catch (IllegalAccessException ex) {
            MessageBundle.showError("Illegal Access Exception:" + key);
        } catch (IllegalArgumentException ex) {
            MessageBundle.showError("Illegal Argument Exception:" + key);
        } catch (InvocationTargetException ex) {
            MessageBundle.showError("Invocation Targe tException:" + key);
        } catch (NoSuchMethodException ex) {
            MessageBundle.showError("No Such Method:" + key);
        } catch (SecurityException ex) {
            MessageBundle.showError("Security Exception:" + key);
        }
        return outcome;
    }

    private List<String> selectedIds = new ArrayList<>();
    private String selectedArryIds;

    public List<String> getSelectedIds() {
        return selectedIds;
    }

    public void setSelectedIds(List<String> selectedIds) {
        this.selectedIds = selectedIds;
    }

    public void onRowSelect(SelectEvent event) {
        T t = (T) event.getObject();
        entitys.add(t);
        pageSelectEntitys.add(t);
        selectedIds.add(t.getId().toString());
    }

    public void onRowUnselect(UnselectEvent event) {
        T t = (T) event.getObject();
        entitys.remove(t);
        pageSelectEntitys.remove(t);
        selectedIds.remove(t.getId());
    }

    public void onToggleSelectEvent(ToggleSelectEvent event) {
        if (event.isSelected()) {
            List<T> ts = (List<T>) ((DataTable) event.getSource()).getSelection();
            pageSelectEntitys.addAll(ts);
            for (T t : ts) {
                selectedIds.add(t.getId().toString());
            }
        } else {
            List<T> ts = (List<T>) ((DataTable) event.getSource()).getSelection();
            pageSelectEntitys.removeAll(ts);
            for (T t : ts) {
                selectedIds.remove(t.getId());
            }
        }
    }

    public String getSelectedArryIds() {
        if (!selectedIds.isEmpty()) {
            selectedArryIds = ObjectUtil.converterListTOString(selectedIds.toArray());
        }
        return selectedArryIds;
    }

    public void setSelectedArryIds(String selectedArryIds) {
        this.selectedArryIds = selectedArryIds;
    }

    public void onColumnsChosen(SelectEvent event) {
        // Car car = (Car) event.getObject();
        // FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,
        // "Car Selected", "Id:" + car.getId());

        // FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void chooseColumns() {
        Map<String, Object> options = new HashMap<String, Object>();
        options.put("modal", true);
        options.put("draggable", false);
        options.put("resizable", false);
        options.put("contentHeight", 320);
        options.put("contentHeight", 320);
        options.put("value", getColumns());
        RequestContext.getCurrentInstance().openDialog("SelectColumns", options, null);
    }

    private DualListModel<BaseColumnModel> advancedSelectColumns;

    public DualListModel<BaseColumnModel> getAdvancedSelectColumns() {
//		if (advancedSelectColumns == null) {
        List<BaseColumnModel> source = new ArrayList<BaseColumnModel>();
        List<BaseColumnModel> target = new ArrayList<BaseColumnModel>();
        if (allColumns == null) {
            allColumns = getAllColumns();
        }
        for (BaseColumnModel bcm : allColumns) {
            // if (bcm.getToggleable()) {
            if (bcm.getVisible()) {
                source.add(bcm);
            } else {
                target.add(bcm);
            }
            // }
        }
        advancedSelectColumns = new DualListModel<>(source, target);
//		}
        return advancedSelectColumns;
    }

    public void setAdvancedSelectColumns(DualListModel<BaseColumnModel> advancedSelectColumns) {
        this.advancedSelectColumns = advancedSelectColumns;
    }

    // TODO: batch commit
    /**
     * using pickList select and sort view fields
     */
    public void updateAdvancedColumns() {
        List<BaseColumnModel> source = advancedSelectColumns.getSource();
        for (int i = 0; i < source.size(); i++) {
            BaseColumnModel o = source.get(i);
            o.setSort(String.valueOf(i));
            o.setVisible(true);
            columnModelDao.update(o);
        }
        List<BaseColumnModel> target = getAdvancedSelectColumns().getTarget();
        for (int i = 0; i < target.size(); i++) {
            BaseColumnModel o = target.get(i);
            o.setSort("999");
            o.setVisible(false);
            columnModelDao.update(o);
        }
        initViewColums();
        // if update sucess
        // this.columns = selectColumns;
        FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_INFO, "Operation", "Success");
        MessageBundle.autoMessage(fm);
    }

    /**
     * @deprecated
     */
    public void updateAdvancedColumnsBak() {
        StringBuilder ids = new StringBuilder("'");
        List<BaseColumnModel> source = getAdvancedSelectColumns().getSource();
        for (int i = 0; i < source.size(); i++) {
            for (BaseColumnModel bcm2 : allColumns) {
                if (source.get(i).equals(bcm2)) {
                    ids.append(",'").append(bcm2.getId()).append("'");
                    columnModelDao.update(bcm2.getId(), BaseColumnModel_.sort, String.valueOf(i));
                    break;
                }
            }
        }

        FacesMessage outcome = columnModelDao.updateColumnsVisible(dao().getEntityClass().getName(), getCustomColumnsKey(),
                ids.toString().replaceFirst("',", ""), Boolean.TRUE);
        initViewColums();
        // if update sucess
        // this.columns = selectColumns;
        MessageBundle.autoMessage(outcome);
    }

    public void callColumnExtension(String methodName, Object t) {
        try {
            Method method = this.getClass().getMethod(methodName, t.getClass());
            method.invoke(this, t);
        } catch (IllegalAccessException ex) {
            logger.error("Illegal Access Exception:" + methodName);
        } catch (IllegalArgumentException ex) {
            logger.error("Illegal Argument Exception:" + methodName);
        } catch (InvocationTargetException ex) {
            logger.error("Invocation Targe Exception:" + methodName);
        } catch (NoSuchMethodException ex) {
            logger.error("No Such Method:" + methodName);
        } catch (SecurityException ex) {
            logger.error("Security Exception:" + methodName);
        }
    }

    public List<String> callCompleteMethod(String methodName, String query) {
        List<String> outcome = new ArrayList<>();
        try {
            Method method = this.getClass().getMethod(methodName, String.class);
            outcome = (List<String>) method.invoke(this, query);
        } catch (IllegalAccessException ex) {
            MessageBundle.showError("Illegal Access Exception:" + methodName);
        } catch (IllegalArgumentException ex) {
            MessageBundle.showError("Illegal Argument Exception:" + methodName);
        } catch (InvocationTargetException ex) {
            MessageBundle.showError("Invocation Targe tException:" + methodName);
        } catch (NoSuchMethodException ex) {
            MessageBundle.showError("No Such Method:" + methodName);
        } catch (SecurityException ex) {
            MessageBundle.showError("Security Exception:" + methodName);
        }
        return outcome;
    }

    public List<BaseColumnModel> defaultColumns;
    public StreamedContent excel;//TODO: remove

    /**
     * @deprecated TODO: remove
     */
    public void exportAllData() {
        excel = null;
        List<E> exportData = dao().getExportAllLazyDataModelData();
        exportXLS(exportData);
    }

    /**
     * @deprecated
     */
    private void exportXLS(List<? extends AbstractEntity> exportData) {
        if (null == exportData || exportData.size() <= 0) {
            return;
        }
        String fileName = getExcelFileName();
        try {
            SXSSFWorkbook sxwb = null;//POIUtils.generateSXSS(this.columns, exportData);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            sxwb.write(outputStream);
            byte[] bytes = outputStream.toByteArray();
            outputStream.flush();
            outputStream.close();

            if (fileName == null || fileName.isEmpty()) {
                fileName = "default.xlsx";
            }
            excel = new DefaultStreamedContent(new ByteArrayInputStream(bytes), "application/ms-excel", fileName);
            sxwb.dispose();
        } catch (Exception e) {
            logger.error("Generate file " + fileName + " encounter error :");
            logger.error(e);
            e.printStackTrace();
            MessageBundle.showError("download error");
        }
    }

    public StreamedContent getExcel() {
        return excel;
    }

    public void exportPageData() {
        excel = null;
        List<T> pageDatas = (List<T>) getDataModel().getWrappedData();
        if (null != pageDatas && pageDatas.size() > 0) {
            exportXLS(pageDatas);
        } else {
            MessageBundle.showError("Current Page is emptys.");
        }

    }

    public void exportSelectData() {
        excel = null;
        if (null != this.entitys && entitys.size() > 0) {
            exportXLS(entitys);
        } else {
            MessageBundle.showError("No select Item.");
        }

    }

    private Integer freezeColumns = 1;

    public Integer getFreezeColumns() {
        return freezeColumns;
    }

    public void setFreezeColumns(Integer freezeColumns) {
        this.freezeColumns = freezeColumns;
    }

    public int getRows(int rows) {
        return rows;
    }

    public void postExportExcel(Object doc) {
        HSSFWorkbook wb = (HSSFWorkbook) doc;
        HSSFSheet sheet = wb.getSheetAt(0);

        HSSFCellStyle styleHeader = wb.createCellStyle();
        styleHeader.setFillPattern(CellStyle.SOLID_FOREGROUND);
        styleHeader.setFillForegroundColor(HSSFColor.GREY_40_PERCENT.index);
        styleHeader.setAlignment(CellStyle.ALIGN_CENTER);
        styleHeader.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        styleHeader.setWrapText(true);

        HSSFCellStyle sheetStyle = wb.createCellStyle();
        sheetStyle.setAlignment(CellStyle.ALIGN_CENTER);
        sheetStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

        HSSFRow row0 = sheet.getRow(0);
        row0.setHeight((short) 600);
        for (int c = 0; c < row0.getPhysicalNumberOfCells(); c++) {
            sheet.autoSizeColumn(c, true);
            HSSFCell header = row0.getCell(c);
            if (header.getStringCellValue().contains("<br>")) {
                header.setCellValue(new HSSFRichTextString(row0.getCell(c).getStringCellValue().replaceAll("<br>", "\r\n")));
            }
            header.setCellStyle(styleHeader);
        }

    }

    public void removeColumn(String entityId) {
        dao().update(entityId, BaseColumnModel_.visible, Boolean.FALSE);
        //TODO: update current columns
    }
}