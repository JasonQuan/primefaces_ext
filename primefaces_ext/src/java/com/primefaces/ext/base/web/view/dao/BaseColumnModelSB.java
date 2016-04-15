package com.primefaces.ext.base.web.view.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import javax.faces.model.SelectItem;
import javax.persistence.Query;
import javax.persistence.Transient;
import javax.persistence.metamodel.EntityType;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.sessions.CopyGroup;

import com.primefaces.ext.base.util.BaseKeyValue;
import com.primefaces.ext.base.util.ColumnHelper;
import com.primefaces.ext.base.util.ObjectUtil;
import com.primefaces.ext.base.web.view.entity.BaseColumnModel;

/**
 * TODO: table header show or hide
 *
 * @author Jason
 */
@Stateless
@SuppressWarnings("serial")
public class BaseColumnModelSB extends PrimefacesExtEJB<BaseColumnModel, BaseColumnModel> {

    public void resetColumns(Class<?> cla, String customsKey) {
        // super.removeBatch(getByEntityClass(cla.getName(), customsKey));
        String jpql = "update " + BaseColumnModel.class.getSimpleName() + " o set o.visible = true where o.entity = '" + cla.getSimpleName()
                + "' and o.customsKey = '" + customsKey + "'";
        super.excuteUpdateJPQL(jpql);
    }

    public List<BaseColumnModel> getByEntityClass(String cla, String customsKey) {
        String jpql = "select o from " + BaseColumnModel.class.getSimpleName() + " o where o.customsKey = '" + customsKey + "' and o.entity = '" + cla
                + "' order by  o.sort  ASC";// NULLS LAST";
        return super.findByJPQL(jpql);
    }

    public List<BaseColumnModel> getByEntityClass(String cla, String customsKey, boolean visible) {
        String jpql = "select o from " + BaseColumnModel.class.getSimpleName() + " o where o.visible = '" + visible + "' and  o.customsKey = '" + customsKey
                + "' and o.entity = '" + cla + "' order by  o.sort  ASC";// NULLS
        // LAST";
        return super.findByJPQL(jpql);
    }

    /**
     *
     * @param cla
     * @param key
     * @param visible
     * @return
     */
    public List<BaseColumnModel> getColumnModel(Class<?> cla, String key, String group) {
        if (key.equals("default")) {
            return getDefaultColumnModel(cla);
        } else {
            return getCustomsModel(cla, key);
        }
    }

    public List<BaseColumnModel> getDefaultColumnModel(Class<?> cla) {
        List<BaseColumnModel> outcome = getByEntityClass(cla.getName(), "default");
        if (outcome.isEmpty()) {
            outcome = initDefaultModel(cla);
        } else {
            outcome = getByEntityClass(cla.getName(), "default");
        }
        return outcome;
    }

    private List<BaseColumnModel> getCustomsModel(Class<?> cla, String key) {
        List<BaseColumnModel> outcome = getByEntityClass(cla.getName(), key);
        if (outcome.isEmpty()) {
            List<BaseColumnModel> defaults = getByEntityClass(cla.getName(), "default");
            if (defaults.isEmpty()) {
                defaults = initDefaultModel(cla);
            }
            for (BaseColumnModel d : defaults) {
                // is mapped to a primary key column in the database. Updates
                // are not allowed
                CopyGroup group = new CopyGroup();
                group.setShouldResetPrimaryKey(true);
                BaseColumnModel copy = (BaseColumnModel) getEntityManager().unwrap(JpaEntityManager.class).copy(d, group);
                copy.setId(ObjectUtil.getEntityUUID());
                copy.setCustomsKey(key);

                create(copy);
            }

            outcome = getByEntityClass(cla.getName(), key);
        }
        return outcome;
    }

    private List<BaseColumnModel> initDefaultModel(Class<?> cla) {
        Map<String, BaseKeyValue> fieldMapping = ObjectUtil.getEntityFieldWithTableColumn(cla);

        Map<String, ColumnHelper> helpers = ObjectUtil.getEntityFieldWithColumnHelper(cla);
        String[] fields = new String[fieldMapping.keySet().size()];
        fieldMapping.keySet().toArray(fields);
        String entity = cla.getName();
        for (int i = 0; i < fields.length; i++) {
            String column = fieldMapping.get(fields[i]).getKey();
            BaseColumnModel vli = findByEntityAndFieldAndCustomKey(entity, fields[i], "default");
            if (vli != null) {
                continue;
            }
            BaseColumnModel cm = new BaseColumnModel();
            cm.setField(fields[i]);
            cm.setDataType(fieldMapping.get(fields[i]).getValue());

            cm.setEntity(entity);
            cm.setHeader(column.replaceAll("_", " ").replace("\"", ""));
            cm.setTableColumn(column);
            cm.setCustomsKey("default");
            cm.setStyle("text-align: center;");
            cm.setSort(String.valueOf(i));
            cm.setWidth("auto;");
            cm.setEdit(Boolean.TRUE);
            cm.setToggleable(Boolean.TRUE);
            cm.setSort("999");

            if (fields[i].contains(".")) {
                cm.setVisible(false);
                cm.setHeader(cm.getField().substring(0, cm.getField().indexOf(".")) + " " + cm.getHeader());
            } else {
                try {
                    boolean isNotTransient = cla.getDeclaredField(fields[i]).getAnnotation(Transient.class) == null;
                    cm.setVisible(isNotTransient);
                    cm.setEdit(isNotTransient);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }
            cm = copyHelper(cm, helpers.get(fields[i]));
            super.create(cm);
        }
        logger.debug("init Default Column Model:" + cla.getName());
        return getByEntityClass(cla.getName(), "default", Boolean.TRUE);
    }

    public FacesMessage updateColumnsVisible(String entity, String customsKey, String ids, Boolean visible) {
        String enVisbleSql = "update BaseColumnModel o set o.visible = " + visible + " where o.id in (" + ids + ")";
        String disVisbleSql = "update BaseColumnModel o set o.visible = " + !visible + " where o.toggleable = true and o.entity = '" + entity
                + "' and o.customsKey = '" + customsKey + "' and o.id not in (" + ids + ")";
        super.excuteUpdateJPQL(enVisbleSql);
        super.excuteUpdateJPQL(disVisbleSql);
        return new FacesMessage(FacesMessage.SEVERITY_INFO, "visible columns", "over");
    }

    @SuppressWarnings("unchecked")
    public SelectItem[] getEntitysItems() {
        String jpql = "select o.entity from BaseColumnModel o where o.entity is not null group by o.entity order by o.entity";
        List<String> bcms = super.findByJPQL(jpql, String.class);
        SelectItem[] outcome = new SelectItem[bcms.size()];
        for (int i = 0; i < bcms.size(); i++) {
            outcome[i] = new SelectItem(bcms.get(i), bcms.get(i));
        }
        return outcome;
    }

    @SuppressWarnings("unchecked")
    public SelectItem[] getCustomerKeyItems() {
        String jpql = "select o.customsKey from BaseColumnModel o where o.customsKey is not null group by o.customsKey order by o.customsKey";
        List<String> bcms = super.findByJPQL(jpql, String.class);
        SelectItem[] outcome = new SelectItem[bcms.size()];
        for (int i = 0; i < bcms.size(); i++) {
            outcome[i] = new SelectItem(bcms.get(i), bcms.get(i));
        }
        return outcome;
    }

    @SuppressWarnings("unchecked")
    public SelectItem[] getDataTypeItems() {
        String jpql = "select o.dataType from BaseColumnModel o where o.dataType is not null group by o.dataType order by o.dataType";
        List<String> bcms = super.findByJPQL(jpql, String.class);
        SelectItem[] outcome = new SelectItem[bcms.size()];
        for (int i = 0; i < bcms.size(); i++) {
            outcome[i] = new SelectItem(bcms.get(i), bcms.get(i));
        }
        return outcome;
    }

    private BaseColumnModel copyHelper(BaseColumnModel cm, ColumnHelper ch) {
        if (ch != null) {
            if (StringUtils.isNotBlank(cm.getHeader()) && StringUtils.isNotBlank(ch.header())) {
                cm.setHeader(ch.header());
            }
            if (StringUtils.isNotBlank(ch.style())) {
                cm.setStyle(ch.style());
            }

            cm.setExportable(ch.exportable());
            cm.setEdit(ch.editAble());
            cm.setVisible(ch.visible());
            cm.setToggleable(ch.toggleable());
            cm.setFilterable(ch.filterable());
            cm.setIsMultiFilter(ch.isMultiFilter());
            cm.setSortable(ch.sortable());

            if (StringUtils.isNotBlank(ch.footer())) {
                cm.setFooter(ch.footer());
            }
            if (StringUtils.isNotBlank(ch.filterOptions())) {
                cm.setFilterOptions(ch.filterOptions());
            }
            if (StringUtils.isNotBlank(ch.validatorMessage())) {
                cm.setValidatorMessage(ch.validatorMessage());
            }
            if (StringUtils.isNotBlank(ch.validateRegex())) {
                cm.setValidateRegex(ch.validateRegex());
            }
            if (StringUtils.isNotBlank(ch.validatorId())) {
                cm.setValidatorId(ch.validatorId());
            }
            if (StringUtils.isNotBlank(ch.filterPlaceHolder())) {
                cm.setFilterPlaceHolder(ch.filterPlaceHolder());
            }

            if (StringUtils.isNotBlank(ch.dropDown())) {
                cm.setDropDown(ch.dropDown());
            }
            if (StringUtils.isNotBlank(ch.extFunction())) {
                cm.setExtFunction(ch.extFunction());
            }
            if (StringUtils.isNotBlank(ch.extIcon())) {
                cm.setExtIcon(ch.extIcon());
            }
            if (StringUtils.isNotBlank(ch.onupdate())) {
                cm.setOnupdate(ch.onupdate());
            }
            if (StringUtils.isNotBlank(ch.tips())) {
                cm.setTips(ch.tips());
            }
            if (StringUtils.isNotBlank(ch.width())) {
                cm.setWidth(ch.width());
            }
            if (StringUtils.isNotBlank(ch.sort())) {
                cm.setSort(ch.sort());
            }

            if (StringUtils.isNotBlank(ch.tableColumn())) {
                cm.setTableColumn(ch.tableColumn());
            }
            if (StringUtils.isNotBlank(ch.oncomplete())) {
                cm.setOncomplete(ch.oncomplete());
            }
            if (StringUtils.isNotBlank(ch.onstart())) {
                cm.setOnstart(ch.onstart());
            }

            if (StringUtils.isNotBlank(ch.extIcon())) {
                cm.setExtIcon(ch.extIcon());
            }
            if (StringUtils.isNotBlank(ch.onsuccess())) {
                cm.setOnsuccess(ch.onsuccess());
            }
            if (StringUtils.isNotBlank(ch.extValue())) {
                cm.setExtValue(ch.extValue());
            }
            if (StringUtils.isNotBlank(ch.title())) {
                cm.setTitle(ch.title());
            }
            if (StringUtils.isNotBlank(ch.outFormat())) {
                cm.setOutFormat(ch.outFormat());
            }

        }
        return cm;
    }

    public void initNewViewColumns() {
        Set<EntityType<?>> entities = getEntityManager().getEntityManagerFactory().getMetamodel().getEntities();
        for (EntityType<?> entity : entities) {
            // if (super.findByField("entity",
            // entity.getJavaType().getName()).isEmpty()) {//TODO: int result
            initDefaultModel(entity.getJavaType());
            // }
        }
    }

    private BaseColumnModel findByEntityAndFieldAndCustomKey(String entity, String field, String customsKey) {
        String jpql = "select o from " + BaseColumnModel.class.getSimpleName() + " o where o.entity = '" + entity + "' and o.field='" + field
                + "' and o.customsKey ='" + customsKey + "'";
        return super.findSingleByJPQL(jpql);
    }

    public List<SelectItem> getFieldsItems(String entity) throws ClassNotFoundException {
        List<SelectItem> items = new ArrayList<SelectItem>();
        try {
            if (StringUtils.isNotBlank(entity)) {
                String jpql = "select o.field from " + BaseColumnModel.class.getSimpleName() + " o where o.entity =　:entity and o.field is not null";
                Query query = super.getEntityManager().createQuery(jpql, String.class);
                query.setParameter("entity", entity);
                List<String> outcome = query.getResultList();
                Class<?> cls = Class.forName(entity);
                Set<String> freeFields = ObjectUtil.getEntityFieldWithTableColumn(cls).keySet();
                freeFields.removeAll(outcome);
                for (String field : freeFields) {
                    items.add(new SelectItem(field, field));
                    // if (!outcome.contains(field)) {
                    // }
                }
                if (items.isEmpty()) {
                    items.add(new SelectItem("", "Selected Entity No Free Field"));
                }
            } else {
                items.add(new SelectItem("", "Please Select Entity"));
            }
        } catch (Exception e) {
            logger.error(e);
        }
        return items;
    }

}
