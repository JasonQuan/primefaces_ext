package com.primefaces.ext.base.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.metamodel.SingularAttribute;

import org.primefaces.context.RequestContext;

import com.primefaces.ext.base.ejb.BaseEJB;
import com.primefaces.ext.base.entity.AbstractEntity;
import com.primefaces.ext.base.util.BaseLogger;
import com.primefaces.ext.base.web.view.dao.BaseColumnModelSB;
import com.primefaces.ext.base.web.view.entity.BaseColumnModel;

/**
 *
 * @author Jason
 *
 * @param <T> DataTable数据类型
 * @param <E> DataTable数据类型，开发测试中，目前跟T保持一致
 */
@SuppressWarnings({"serial", "rawtypes", "unchecked"})
public abstract class BaseMobleMB<T extends AbstractEntity, E extends AbstractEntity> implements Serializable {

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
    public BaseMobleMB() {
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
            dataModel = new LazyEntityDataModel<T, E>(getDataModelJpql(), dao(), allColumns, getJpqlParameters(), getGlobalFilterJpql());
        }

        return dataModel;
    }

    protected String getGlobalFilterJpql() {
        return "";
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
     * clear up
     */
    public void reset() {
        entity = null;
//		this.pageSelectEntitys = null;
    }

  
    protected String getSetForegin(String str) {
        // str = str.replace("add", "").replace("edit", "");
        String[] a = str.split("");
        return "set" + str.replaceFirst(a[1], a[1].toUpperCase());
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
    private List<String> selectColumns = new ArrayList<>();

    public List<String> getSelectColumns() {
        return selectColumns;
    }

    public void setSelectColumns(List<String> selectColumns) {
        this.selectColumns = selectColumns;
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

    public List<BaseColumnModel> defaultColumns;

    public int getRows(int rows) {
        return rows;
    }

}
