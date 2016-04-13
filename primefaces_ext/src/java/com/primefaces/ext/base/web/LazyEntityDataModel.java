package com.primefaces.ext.base.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SelectableDataModel;
import org.primefaces.model.SortOrder;

import com.primefaces.ext.base.ejb.BaseEJB;
import com.primefaces.ext.base.entity.AbstractEntity;
import com.primefaces.ext.base.util.BaseLogger;
import com.primefaces.ext.base.util.MessageBundle;
import com.primefaces.ext.base.web.view.entity.BaseColumnModel;

/**
 *
 * @author Jason 2012/06/27
 */
public class LazyEntityDataModel<T extends AbstractEntity, E extends AbstractEntity> extends LazyDataModel<E> implements SelectableDataModel<E> {

    private final BaseLogger logger = new BaseLogger(this.getClass());
    private static final long serialVersionUID = 1L;
    private String filter;
    private final BaseEJB<T, E> baseSB;
    private Map<String, BaseColumnModel> columnModels;
    private Map<String, Object> jpqlCondation;

    /**
     *
     * @param filter
     * @param baseDao
     * @param columnModels
     * @param conditions
     */
    public LazyEntityDataModel(String filter, BaseEJB<T, E> baseDao, List<BaseColumnModel> columnModels, Map<String, Object> conditions) {
        if (filter != null && !"".equals(filter) && (filter.contains("order by") || filter.contains("ORDER BY"))) {
            logger.warn("[LazyEntityDataModel] can not contains order by");
        } else {
            this.filter = filter;
        }

        this.baseSB = baseDao;
        this.columnModels = new HashMap<String, BaseColumnModel>();
        logger.debug("filter====>>>>" + filter);
        logger.debug("conditions====>>>>" + conditions);
        this.jpqlCondation = conditions;
        if (columnModels != null && !columnModels.isEmpty()) {
            for (BaseColumnModel bcm : columnModels) {
                this.columnModels.put(bcm.getTableColumn(), bcm);
            }
        }
    }

    public LazyEntityDataModel(BaseEJB<T, E> baseDao, List<BaseColumnModel> columnModels) {
        this.baseSB = baseDao;
        this.columnModels = new HashMap<String, BaseColumnModel>();
        if (columnModels != null && !columnModels.isEmpty()) {
            for (BaseColumnModel bcm : columnModels) {
                this.columnModels.put(bcm.getTableColumn(), bcm);
            }
        }
    }

    // public LazyEntityDataModel(int rowCount) {
    // super.setRowCount(rowCount);
    // / }
    @Override
    public E getRowData(String rowKey) {
        @SuppressWarnings("unchecked")
        List<E> list = (List<E>) getWrappedData();

        if (list != null && !list.isEmpty()) {
            for (E t : list) {
                // if (t.getId() == id) {
                if ((t != null && t.getId() != null) && t.getId().toString().equals(rowKey)) {
                    return t;
                }
            }
        } else {
            MessageBundle.showWarning("please refresh current page");
        }

        return null;
    }

    @Override
    public List<E> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        setRowCount(baseSB.findByConditionRowCount(filters, filter, jpqlCondation));
        return baseSB.findByCondition(first, pageSize, filters, sortField, sortOrder, filter, columnModels, jpqlCondation);
    }

    @Override
    public void setRowIndex(int rowIndex) {
        if (rowIndex == -1 || getPageSize() == 0) {
            super.setRowIndex(-1);
        } else {
            super.setRowIndex(rowIndex % getPageSize());
        }
    }

    @Override
    public Object getRowKey(E object) {
        return object.getId();
    }

}
