package com.primefaces.ext.base.ejb;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.LockTimeoutException;
import javax.persistence.ManyToMany;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.OneToMany;
import javax.persistence.PersistenceException;
import javax.persistence.PessimisticLockException;
import javax.persistence.Query;
import javax.persistence.QueryTimeoutException;
import javax.persistence.Table;
import javax.persistence.TemporalType;
import javax.persistence.TransactionRequiredException;
import javax.persistence.TypedQuery;
import javax.persistence.UniqueConstraint;
import javax.persistence.metamodel.SingularAttribute;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.SortOrder;

import com.primefaces.ext.base.entity.AbstractEntity;
import com.primefaces.ext.base.exception.BaseExceptoin;
import com.primefaces.ext.base.util.BaseLogger;
import com.primefaces.ext.base.util.DateUitl;
import com.primefaces.ext.base.util.MessageBundle;
import com.primefaces.ext.base.util.ObjectUtil;
import com.primefaces.ext.base.util.VOHelper;
import com.primefaces.ext.base.web.LazyEntityDataModel;
import com.primefaces.ext.base.web.view.entity.BaseColumnModel;
import com.primefaces.ext.base.web.view.entity.BaseColumnModel_;

/**
 *
 * @author Jason
 * @TODO: 参数说明
 * @param <T> DataTable data type
 * @param <E> DataTable 数据类型，开发测试中，目前跟T保持一致
 */
// TODO: detailed exception log
@SuppressWarnings({"unchecked", "rawtypes", "serial"})
public abstract class BaseEJB<T extends AbstractEntity, E extends AbstractEntity> implements Serializable {

    protected final BaseLogger logger = new BaseLogger(this.getClass());
    private final Class<T> entityClass;
    private final Class<E> voClass;

    public BaseEJB() {
        entityClass = (Class<T>) ObjectUtil.getSuperClassGenricType(getClass(), 0);
        voClass = (Class<E>) ObjectUtil.getSuperClassGenricType(getClass(), 1);
    }

    /**
     *
     * @return EntityManager, Entity class, primaryKey
     */
    // protected abstract BaseDaoParam<T> getBaseDaoParam();
    public Class<T> getEntityClass() {
        return entityClass;
    }

    public Class<E> getVOClass() {
        return voClass;
    }

    protected abstract EntityManager getEntityManager();

    /**
     * check unique column
     *
     * entity eg:
     *
     * <code>@Table(name = "account", uniqueConstraints = {
     *
     * @UniqueConstraint(columnNames = {"name"})})</code>
     *
     * @param t entity
     * @return String column data
     */
    private String validationUniqueColumn(T t) {
        if (t == null) {
            return null;
        }
        String message = null;
        Table tableAnnotation;
        try {
            tableAnnotation = t.getClass().getAnnotation(Table.class);
            if (tableAnnotation == null) {
                throw new NullPointerException("without Table Annotaion on entity " + getEntityClass());
            }
        } catch (NullPointerException e) {
            logger.warn(e);
            return message;
        }

        UniqueConstraint[] uniqueConstraints = tableAnnotation.uniqueConstraints();
        if (uniqueConstraints.length != 0) {
            for (UniqueConstraint uniqueConstraint : uniqueConstraints) {
                String[] columnNames = uniqueConstraint.columnNames();
                for (String column : columnNames) {
                    Class<? extends AbstractEntity> cls = t.getClass();
                    try {
                        Method method = cls.getMethod(ObjectUtil.columnNameToGetMethos(column));
                        Object invoke;
                        invoke = method.invoke(t);
                        String field = ObjectUtil.columnNameToFieldName(column);
                        List<T> entitys = findByField(field, invoke);
                        // if (t.isNew()) {
                        // if (!entitys.isEmpty()) {
                        // return invoke.toString();
                        // }
                        // } else {
                        if (entitys.size() == 1 && entitys.get(0).getId() == t.getId()) {
                            // return null;
                            break;
                        } else if (entitys.size() > 0) {
                            return invoke.toString();
                        }
                        // }

                    } catch (Exception ex) {
                        logger.error(ex);
                    }
                }
            }
        }
        return message;
    }

    /**
     * TODO: add bath function
     *
     * @param t
     * @return
     */
    public FacesMessage create(T t) {
        FacesMessage outMessage = createCondition(t);
        if (outMessage != null) {
            return outMessage;
        }
        // Annotation[] annotations = t.getClass().getAnnotations();outMessage =
        // new FacesMessage(
        outMessage = new FacesMessage(FacesMessage.SEVERITY_INFO, MessageBundle.CREATE, MessageBundle.SUCCESS);
        String message = validationUniqueColumn(t);
        if (message != null) {
            outMessage.setSeverity(FacesMessage.SEVERITY_WARN);
            outMessage.setSummary(MessageBundle.DUPLICATION_OF_DATA);
            outMessage.setDetail(message);
            return outMessage;
        }
        try {
            // if (t.isNew()) { // TODO: set id

            // t.setId(UUID.randomUUID().toString().replaceAll("-",
            // "").toUpperCase());
            // }
            // t.setSort(getNextSort());
            getEntityManager().persist(t);
            logger.info("[create]:" + t);
        } catch (Exception e) {
            logger.error(e);
            outMessage.setDetail(MessageBundle.EXCEPTION);
            outMessage.setSeverity(FacesMessage.SEVERITY_WARN);
        }
        return outMessage;
    }

    /**
     * native query (important)the native query does not refresh cache
     *
     * @param sql native sql
     * @return the number of entities updated or deleted
     */
    public int excuteUpdateNativeSql(String sql) {
        int outcome = 0;
        try {

            Query q = getEntityManager().createNativeQuery(sql);
            logger.debug("excuteUpdateNativeSql " + sql);
            outcome = q.executeUpdate();

            return outcome;
        } catch (IllegalStateException e) {
            // TODO: details
            logger.error(e);
        } catch (QueryTimeoutException e) {
            logger.error(e);
        } catch (TransactionRequiredException e) {
            logger.error(e);
        } catch (PersistenceException e) {
            logger.error(e);
        } catch (Exception e) {
            logger.error(e);
        } finally {
            // getEntityManager().close();
        }

        return outcome;
    }

    public int queryIntNativeSql(String sql) {// TODO:try
        return ((Long) excuteQueryNativeSql(sql)).byteValue();
    }

    public String queryStringNativeSql(String sql) {// TODO:try
        Object result = excuteQueryNativeSql(sql);
        if (result != null) {
            return excuteQueryNativeSql(sql).toString();
        }
        return "";
    }

    /**
     * get int ((Long) q.getSingleResult()).byteValue(); get other ... (important)the native query does not
     * refresh cache
     *
     * @param sql
     * @return
     */
    public Object excuteQueryNativeSql(String sql) {
        Object outcome = 0;
        try {

            Query q = getEntityManager().createNativeQuery(sql);
            outcome = q.getSingleResult();
        } catch (Exception e) {
            logger.error(e);
        }

        return outcome;
    }
 
    /**
     * id type only: Integer,String,Long
     *
     *
     * @param id entity prime key
     * @return entity
     */
    public T find(String id) {
        // TODO: lock result
        try {
            if (id != null && !"".equals(id)) {

                T t = getEntityManager().find(entityClass, id);

                return t;
            }
        } catch (Exception e) {
            logger.error(e);
        }
        return null;
    }

    /**
     *
     * @param first first row
     * @param pageSize row count
     * @param order order by
     * @return SELECT o FROM T o ORDER BY o.order
     */
    public List<T> findAll(int first, int pageSize, String order) {
        // getEntityManager().getEntityManager().getCache().evictAll();
        // TODO: named query
        String sql = "SELECT o FROM " + entityClass.getSimpleName() + " o";

        if (order != null) {
            sql = sql + " ORDER BY o." + order;
        }

        try {
            TypedQuery<T> q = createQuery(sql, entityClass);
            q.setFirstResult(first);
            q.setMaxResults(pageSize);

            return q.getResultList();
        } catch (IllegalArgumentException e) {
            logger.error(e);
        }

        return new ArrayList<>();
    }

    public List<T> find(int first, int pageSize, Map<String, Object> filters, String sort) {
        String sql = "SELECT o FROM " + entityClass.getSimpleName() + " o";
        sql = formatSql(filters, sql, null);

        if (sort != null) {
            sql = sql + " ORDER BY o." + sort;
        }

        try {
            TypedQuery<T> q = createQuery(sql, entityClass);
            q.setFirstResult(first);
            q.setMaxResults(pageSize);

            return q.getResultList();
        } catch (IllegalArgumentException e) {
            logger.error(e);

        }
        return new ArrayList<>();
    }

    private String formatSql(Map<String, String> filters, String sql) {
        StringBuilder outcome = new StringBuilder(sql);

        if (filters != null && !filters.isEmpty()) {
            outcome.append(" WHERE");

            for (String key : filters.keySet()) {
                String value = filters.get(key);
                outcome.append(" AND o.").append(key).append("='");
                outcome.append(value).append("'");
            }
        }

        return outcome.toString().replace("WHERE AND", "WHERE");
    }

    /**
     *
     * @param filters key=entity field, value = data
     * @return
     */
    public T findSingle(Map<String, String> filters) {
        String sql = "SELECT o FROM " + entityClass.getSimpleName() + " o";
        sql = formatSql(filters, sql);

        try {
            TypedQuery<T> q = createQuery(sql, entityClass);

            return q.getSingleResult();
        } catch (IllegalArgumentException e) {
            logger.error(e);
        } catch (Exception e) {
            logger.error(e);
        }
        return null;
    }

    private String exportAllLazyDataModelJpql;
    private Map<String, Object> exportAllJpqlParametersKeyValue;

    public List<E> getExportAllLazyDataModelData() {
        try {
            TypedQuery q = createVOQuery(exportAllLazyDataModelJpql, voClass);
            if (null != exportAllJpqlParametersKeyValue && exportAllJpqlParametersKeyValue.size() > 0) {
                Iterator<Entry<String, Object>> paras = exportAllJpqlParametersKeyValue.entrySet().iterator();
                while (paras.hasNext()) {
                    Entry<String, Object> e = paras.next();
                    String key = e.getKey();
                    Object value = e.getValue();
                    q.setParameter(key, value);
                }
            }
            return q.getResultList();
        } catch (Exception e) {
            logger.error(e);
        }
        return new ArrayList<E>(0);
    }

    /**
     * for primeface dataTable lazy model bug 如果filter包含order by，则sort失效，
     *
     * @param first this paramter from primefaces datatable
     * @param pageSize this paramter from primefaces datatable
     * @param filters this paramter from primefaces datatable
     * @param sort this paramter from primefaces datatable
     * @param sortOrder this paramter from primefaces datatable
     * @param jpql jpql muse end with where condation, eg: 'select o from entity o where 1=1 '
     * @param columnModels
     * @param jpqlParametersKeyValue jqpl condation Key,Value mapping eg: Jpql:(.... and x.name = :name)
     * map:("name","China")
     * @return Entity List
     * @exception sort can not work on Boolean Type
     */
    // BUG 001 : sort can not work on Boolean Type,
    public List<E> findByCondition(int first, int pageSize, Map<String, Object> filters, String sort, SortOrder sortOrder, String jpql,
            Map<String, BaseColumnModel> columnModels, Map<String, Object> jpqlParametersKeyValue) {
        try {
            if (entityClass.getClass().equals(voClass.getClass())) {
                sort = StringUtils.isBlank(sort) ? null : sort + (sortOrder.equals(SortOrder.ASCENDING) ? " ASC" : " DESC");
                jpql = formatJPQL(filters, jpql);

                if (sort != null) {
                    jpql = jpql + " ORDER BY o." + sort;
                }
                this.exportAllLazyDataModelJpql = jpql;
                this.exportAllJpqlParametersKeyValue = jpqlParametersKeyValue;
                TypedQuery<E> q = createVOQuery(jpql, voClass);

                if (null != jpqlParametersKeyValue && jpqlParametersKeyValue.size() > 0) {
                    Iterator<Entry<String, Object>> paras = jpqlParametersKeyValue.entrySet().iterator();
                    while (paras.hasNext()) {
                        Entry<String, Object> e = paras.next();
                        String key = e.getKey();
                        Object value = e.getValue();
                        q.setParameter(key, value);
                    }
                }
                q.setFirstResult(first);
                q.setMaxResults(pageSize);
                logger.debug("findByCondition sql: " + jpql);
                return q.getResultList();
            } else {
                String returnColumns = getReturnColumns(columnModels.keySet());
                String sql = formatVOSql(filters, jpql).insert(0, "select " + returnColumns).toString();
                Query q = getEntityManager().createNamedQuery(sql);
                q.setFirstResult(first);
                q.setMaxResults(pageSize);
                logger.debug("findByCondition sql: " + sql);
                List<Object[]> objects = q.getResultList();
                List<E> outcome = copyFields(objects, returnColumns.split(","), columnModels, voClass);
                return outcome;
            }
        } catch (Exception e) {
            logger.error(e);
        }

        return new ArrayList<>(0);
    }

    private String getReturnColumns(Set<String> columns) {
        StringBuilder outcome = new StringBuilder();

        for (String column : columns) {
            outcome.append(column).append(",");
        }
        outcome.delete(outcome.length() - 1, outcome.length());
        return outcome.toString();
    }

    /**
     *
     * @param filters from primefaces datatable
     * @param jpql the 'FROM' in jpql must be upper case, and jpql must be end with a blank space
     * @param jpqlParametersKeyValue map<key,value> for jpql setParameter(key, value)
     * @return
     */
    public Integer findByConditionRowCount(Map<String, Object> filters, String jpql, Map<String, Object> jpqlParametersKeyValue) {
        // getEntityManager().getEntityManager().getCache().evictAll();
        // TODO: remove order by code
        try {
            if (entityClass.getClass().equals(voClass.getClass())) {
                jpql = "SELECT COUNT(1) FROM " + jpql.split("FROM")[1];
                jpql = formatJPQL(filters, jpql);
                Query q = createQuery(jpql);
                logger.debug("jpql find by condition row count====>>>>" + jpql.toString());
//                if (null != jpqlParametersKeyValue && jpqlParametersKeyValue.size() > 0) {
//                    Iterator<Entry<String, Object>> paras = jpqlParametersKeyValue.entrySet().iterator();
//                    while (paras.hasNext()) {
//                        Entry<String, Object> e = paras.next();
//                        String key = e.getKey();
//                        Object value = e.getValue();
//                        q.setParameter(key, value);
//                    }
//                }
                return Integer.parseInt(q.getSingleResult().toString());
            } else {
                String sql = formatVOSql(filters, jpql).insert(0, "select count(*) ").toString();

                return Integer.parseInt(createQuery(sql).getSingleResult().toString());
            }
        } catch (IndexOutOfBoundsException e) {
            logger.error("findByConditionRowCount IndexOutOfBoundsException please check jpql(the FROM key must be supper case): " + jpql);
        } catch (NumberFormatException e) {
            logger.error(e);
        } catch (PessimisticLockException e) {
            logger.error(e);
        } catch (TransactionRequiredException e) {
            logger.error(e);
        } catch (LockTimeoutException e) {
            logger.error(e);
        } catch (PersistenceException e) {
            logger.error(e);
        } catch (Exception e) {
            logger.error(e);
        }
        return 0;
    }

    /**
     *
     * @param filters 查 询 条 件
     * @return total count
     */
    protected Number findCount(Map<String, Object> filters) {
        String sql = "SELECT COUNT(o) FROM " + entityClass.getSimpleName() + " o";

        if (!filters.isEmpty()) {
            sql = formatSql(filters, sql, null);
        }

        try {
            Query q = createQuery(sql);
            return (Number) q.getSingleResult();
        } catch (IllegalArgumentException e) {
            logger.error(e);
        }

        return 0;
    }

    /**
     * 多条件查询
     *
     * eg: name=jaspn,job=staff
     *
     * @param filters key=entity field, value= field data
     * @return entity list
     */
    protected List<T> findByMultiColumn(Map<String, Object> filters) {
        String sql = "SELECT o FROM " + entityClass.getSimpleName() + " o";

        if (!filters.isEmpty()) {
            sql = formatSql(filters, sql, null);
        }

        try {
            TypedQuery<T> q = createQuery(sql, entityClass);
            return q.getResultList();
        } catch (IllegalArgumentException e) {
            logger.error(e);
        }

        return new ArrayList<T>();
    }

    /**
     *
     * @param filter is Entity Column attribute
     * @return rows count
     */
    protected Integer findCount(String filter) {
        String sql = "SELECT COUNT(o) FROM " + entityClass.getSimpleName() + " o Where o." + filter;
        Number count = null;
        try {
            Query q = createQuery(sql);
            count = (Number) q.getSingleResult();
        } catch (IllegalArgumentException e) {
            logger.error(e);

        }

        return count == null ? 0 : count.intValue();
    }

    public List<T> findAll() {
        // CriteriaQuery cq =
        // getEntityManager().getCriteriaBuilder().createQuery();
        // cq.select(cq.from(entityClass));
        try {
            // TODO nulls as last
            // String sql = "SELECT o FROM " + entityClass.getSimpleName() +
            // " o order by o.createTime desc";
//            String sql = "SELECT o FROM " + entityClass.getSimpleName() + " o order by o.sort desc";
            String sql = "SELECT o FROM " + entityClass.getSimpleName() + " o ";
            TypedQuery<T> q = createQuery(sql, entityClass);

            return q.getResultList();
        } catch (Exception e) {
            logger.error(e);
        }
        return new ArrayList<T>();
    }

    /**
     * find entity list by sql
     *
     * @param sql native query
     * @return Entity List
     */
    protected List<T> findByNativeSql(String sql) {
        Query q = getEntityManager().createNativeQuery(sql, entityClass);
        List<T> list = new ArrayList<T>();
        try {
            list = q.getResultList();
        } catch (Exception e) {
            logger.error(e);
        }

        return list;
    }

    /**
     * get entity row count
     *
     * @return row count
     */
    public int getCount() {
        try {
            // CriteriaQuery<T> cq = (CriteriaQuery<T>)
            // getEntityManager().getCriteriaBuilder().createQuery();
            // cq.from(entityClass);
            //
            // cq.select(cq.getSelection());
            // Query q = createQuery(cq);
            //
            // return ((Long) q.getSingleResult()).intValue();

            String sql = "select count(id) from " + getTableName();
            // long start = SystgetEntityManager().currentTimeMillis();

            Query q = getEntityManager().createNativeQuery(sql);

            int p = ((Long) q.getSingleResult()).byteValue();
            // long end = SystgetEntityManager().currentTimeMillis();
            // logger.out("getCount using time: " + (end - start) +
            // " \n[ sql ] \n" + sql);
            return p;
        } catch (QueryTimeoutException e) {
            logger.error(e);
        } catch (Exception e) {
            logger.error(e);
        } finally {
            // getEntityManager().close();
        }
        return 0;
    }

    /**
     * @deprecated @param tablename
     * @return all entity count
     */
    public int getCount(String tablename) {
        String sql = "select count(id) from " + tablename;

        Query q = getEntityManager().createNativeQuery(sql);

        return ((Long) q.getSingleResult()).byteValue();
    }

    /**
     * 添加删除条件
     *
     * @param t entity
     * @return FacesMessage
     */
    protected FacesMessage removeCondition(T t) {
        return null;
    }

    /**
     * 添加更新条件
     *
     * @param t entity
     * @return FacesMessage
     */
    protected FacesMessage updateCondition(T t) {
        return null;
    }

    /**
     * 添加新增条件
     *
     * @param t
     * @return FacesMessage
     */
    protected FacesMessage createCondition(T t) {
        return null;
    }

    /**
     * delete entity 可自动级联删除 如果级联删除为false，则删除失败
     *
     * 如果需要添加删除条件，请重写removeCondition方法
     *
     * 修复级联删除bug201210181400
     *
     * @param t entity
     * @return FacesMessage
     */
    public FacesMessage remove(T t) {
        FacesMessage facesMessage = removeCondition(t);
        if (facesMessage != null) {
            return facesMessage;
        }
        facesMessage = new FacesMessage(MessageBundle.REMOVE);
        try {
            // TODO:SELECT LAST_INSERT_ID();
            Class<T> entityClass = getEntityClass();

            Field[] fields = entityClass.getDeclaredFields();

            for (Field f : fields) {
                // f.getType().isAssignableFrom(BaseEntity.class);
                Class<?> type = f.getType();
                if (type.isAssignableFrom(List.class)) {
                    // -- start process cascading deletes
                    OneToMany oneToMany = f.getAnnotation(OneToMany.class);
                    ManyToMany manyToMany = f.getAnnotation(ManyToMany.class);

                    if (oneToMany != null && oneToMany.orphanRemoval()) {
                        continue;
                    }
                    if (manyToMany != null) {
                        continue;
                    }
                    // -- end process cascading deletes
                    String getMethod = ObjectUtil.getGetMethodNameByFiledName(f.getName());
                    Method method = entityClass.getMethod(getMethod);
                    Object obj = method.invoke(t);
                    if ((obj != null && !((List) obj).isEmpty())) {
                        facesMessage.setDetail(MessageBundle.IN_USING_CAN_NOT_REMOVE);
                        facesMessage.setSeverity(FacesMessage.SEVERITY_WARN);
                        return facesMessage;
                    }
                }
            }

            // for (Method method : methods) {
            // String methodName = method.getName();
            // if (methodName.contains("_")) {
            // continue;
            // }
            // Class<?> returnType = method.getReturnType();
            // if (returnType.equals(List.class)) {
            // Object obj = method.invoke(t);
            // //-- start process cascading deletes
            // String fildeName =
            // ObjectUtil.getEntityFildeNameByEntityMethodName(methodName);
            //
            //
            //
            // Field field = entityClass.getDeclaredField(fildeName);
            // OneToMany oneToMany = field.getAnnotation(OneToMany.class);
            // ManyToMany manyToMany = field.getAnnotation(ManyToMany.class);
            //
            // if (oneToMany != null && oneToMany.orphanRemoval()) {
            // continue;
            // }
            // if (manyToMany != null) {
            // continue;
            // }
            // //-- end process cascading deletes
            //
            // if ((obj != null && !((List) obj).isEmpty())) {
            // facesMessage.setDetail(MessageBundle.IN_USING_CAN_NOT_REMOVE);
            // facesMessage.setSeverity(FacesMessage.SEVERITY_WARN);
            // return facesMessage;
            // }
            // }
            // }
            getEntityManager().remove(getEntityManager().merge(t));

            logger.info("[remove]:" + t);

            facesMessage.setSeverity(FacesMessage.SEVERITY_INFO);
            facesMessage.setDetail(MessageBundle.SUCCESS);
            return facesMessage;
        } catch (Exception e) {
            // TODO:other exception

            logger.error(e);
            facesMessage.setSummary(MessageBundle.EXCEPTION);
            // TODO：提示可能出现错误的原因
            logger.debug("排错提示：有外键关联|懒加载，");
        }
        facesMessage.setSeverity(FacesMessage.SEVERITY_WARN);
        return facesMessage;
    }

    public void afterUpdate(T t) {
    }

    /**
     * 更 新 table
     *
     * @param id table id
     * @param field entitiy field
     * @param value new value
     * @return FacesMessage
     */
    public FacesMessage update(String id, SingularAttribute field, Object value) {
        return update(id, field.getName(), value);
    }

    public FacesMessage update(String id, String field, Object value) {
        FacesMessage outMessage = new FacesMessage("update");
        // if (null != value) {
        // / value = value.replaceAll("'", "\\\\'").replaceAll("\"", "\\\\\"");
        // value = ObjectUtil.escape(value);
        // // }
        // String sql = "UPDATE " + getTableName() + " SET " + column + " = '" +
        // value + "' WHERE ID = '" + id + "'";
        // int count = excuteNativeSql(sql);

        Query query = getEntityManager().createQuery("UPDATE " + getEntityClass().getSimpleName() + " o SET o." + field + " = :field WHERE o.id = :id ");
        query.setParameter("field", value);
        query.setParameter("id", id);

        int count = query.executeUpdate();

        if (count == 1) {
            outMessage.setDetail(MessageBundle.SUCCESS);
            outMessage.setSeverity(FacesMessage.SEVERITY_INFO);
        } else {
            outMessage.setDetail(MessageBundle.FAILURE);
            outMessage.setSeverity(FacesMessage.SEVERITY_WARN);
        }
        return outMessage;
    }

    public FacesMessage update(T t) {
        FacesMessage outMessage = updateCondition(t);
        if (outMessage != null) {
            return outMessage;
        }
        outMessage = new FacesMessage(MessageBundle.UPDATE);        
        String message = validationUniqueColumn(t);
        if (message != null) {
            outMessage.setSeverity(FacesMessage.SEVERITY_WARN);
            outMessage.setSummary(MessageBundle.DUPLICATION_OF_DATA);
            outMessage.setDetail(message);
            return outMessage;
        }
        // TODO: check why can not catch if instance is not an entity or is a
        // removed entity on getEntityManager().merge(t)
        try {
            // if (t.isNew()) {
            // outMessage.setSeverity(FacesMessage.SEVERITY_WARN);
            // outMessage.setDetail(MessageBundle.OBJECT_DOES_NOT_EXIST);
            // } else {

            // t.setUpdateTime(new Date());
            getEntityManager().merge(t);

            afterUpdate(t);
            logger.info("[update]" + t);
            outMessage.setSeverity(FacesMessage.SEVERITY_INFO);
            outMessage.setDetail(MessageBundle.SUCCESS);
            return outMessage;
            // }
        } catch (IllegalArgumentException e) {
            logger.error(e);
            outMessage.setSeverity(FacesMessage.SEVERITY_WARN);
        } catch (Exception e) {
            outMessage.setSeverity(FacesMessage.SEVERITY_WARN);
            outMessage.setDetail(MessageBundle.EXCEPTION);

            logger.error(e);
        } finally {
            // if (getEntityManager() != null && getEntityManager().isOpen()) {
            // getEntityManager().close();
            // }
        }
        return outMessage;
    }

    /**
     * for primefaces datatable
     *
     * @param filters
     * @param sql
     * @param filter like
     * @return sql
     */
    private String formatSql(Map<String, Object> filters, String sql, String filter) {
        if (!filters.isEmpty() || (filter != null && !filter.equals(""))) {
            sql = sql + " WHERE ";
        }

        if (filter != null && !filter.equals("")) {
            sql = sql + "o." + filter + " AND ";
        }

        if (filters != null && !filters.isEmpty()) {
            Set<String> columns = filters.keySet();
            for (String column : columns) {
                Object condantion = filters.get(column);
                Object fieldType = ObjectUtil.getPropertyType(entityClass, column);
                if (fieldType != null) {
                    if (ObjectUtil.isString(fieldType) || ObjectUtil.isDouble(fieldType)) {
                        if (ObjectUtil.isArray(condantion)) {
                            Object[] strs = (Object[]) condantion;
                            if (ArrayUtils.isNotEmpty(strs)) {
                                sql = sql + "o." + column + " IN (" + parseFilterInCondation(strs, String.class) + ") AND ";
                            }
                        } else {
                            sql = sql + "o." + column + " LIKE '%" + condantion.toString().trim() + "%' AND ";
                        }
                    } else if (ObjectUtil.isInterger(fieldType)) {
                        if (ObjectUtil.isArray(condantion)) {
                            Object[] strs = (Object[]) condantion;
                            if (ArrayUtils.isNotEmpty(strs)) {
                                sql = sql + "o." + column + " IN (" + parseFilterInCondation(strs, Integer.class) + ") AND ";
                            }
                        } else if (ObjectUtil.canConverterToInteger(condantion.toString())) {
                            sql = sql + "o." + column + " = " + condantion + " AND ";
                        }
                    } else if (ObjectUtil.isDate(fieldType)) {
                        if (DateUitl.isYyyyDDddDate(condantion.toString())) {
                            sql = sql + "o." + column + " = '" + condantion + "' AND ";
                        }
                    } else if (ObjectUtil.isShort(fieldType)) {
                        sql = sql + "o." + column + " = '" + condantion + "' AND ";
                    } else if (ObjectUtil.isBigInteger(fieldType)) {
                        sql = sql + "o." + column + " = '" + condantion + "' AND ";
                    } else if (ObjectUtil.isCharacter(fieldType)) {
                        sql = sql + "o." + column + " = '" + condantion + "' AND ";
                    } else if (ObjectUtil.isBoolean(fieldType)) {
                        sql = sql + "o." + column + " = '" + Boolean.valueOf(condantion.toString()) + "' AND ";
                    } else {// other data type
                        logger.warn("datatable column filter has other data type: " + fieldType.getClass());
                    }
                }
            }
        }

        if (sql.endsWith("AND ")) {
            sql = sql.substring(0, sql.length() - 4);
        }

        if (sql.endsWith("WHERE ")) {
            sql = sql.substring(0, sql.length() - 6);
        }
        return sql;
    }

    private String formatJPQL(Map<String, Object> filters, String jpql) {

        if (filters != null && !filters.isEmpty()) {
            Set<String> fields = filters.keySet();
            for (String fieldName : fields) {
                Object condantion = filters.get(fieldName);
                Object fieldType = ObjectUtil.getPropertyType(voClass, fieldName);
                if (fieldType != null) {
                    if (ObjectUtil.isString(fieldType) || ObjectUtil.isDouble(fieldType)) {
                        if (ObjectUtil.isArray(condantion)) {
                            Object[] strs = (Object[]) condantion;
                            if (ArrayUtils.isNotEmpty(strs)) {
                                jpql = jpql + " and o." + fieldName + " IN (" + parseFilterInCondation(strs, String.class) + ") ";
                            }
                        } else {
                            jpql = jpql + " and o." + fieldName + " LIKE '%" + condantion.toString().trim() + "%' ";
                        }
                    } else if (ObjectUtil.isInterger(fieldType) || ObjectUtil.isInt(fieldType)) {
                        if (ObjectUtil.isArray(condantion)) {
                            Object[] strs = (Object[]) condantion;
                            if (ArrayUtils.isNotEmpty(strs)) {
                                jpql = jpql + " and o." + fieldName + " IN (" + parseFilterInCondation(strs, Integer.class) + ") ";
                            }
                        } else if (ObjectUtil.canConverterToInteger(condantion.toString())) {
                            jpql = jpql + " and o." + fieldName + " = " + condantion + " ";
                        }
                    } else if (ObjectUtil.isDate(fieldType)) {
                        if (DateUitl.isYyyyDDddDate(condantion.toString())) {
                            jpql = jpql + " and o." + fieldName + " = '" + condantion + "' ";
                        }
                    } else if (ObjectUtil.isShort(fieldType)) {
                        jpql = jpql + " and o." + fieldName + " = '" + condantion + "' ";
                    } else if (ObjectUtil.isBigInteger(fieldType)) {
                        jpql = jpql + " and o." + fieldName + " = '" + condantion + "' ";
                    } else if (ObjectUtil.isCharacter(fieldType)) {
                        jpql = jpql + " and o." + fieldName + " = '" + condantion + "' ";
                    } else if (ObjectUtil.isBoolean(fieldType)) {
                        jpql = jpql + " and o." + fieldName + " = '" + Boolean.valueOf(condantion.toString()) + "' ";
                    } else {// other data type
                        logger.warn("datatable column filter has other data type: " + fieldType.getClass());
                    }
                }
            }
        }

        return jpql;
    }

    private String parseFilterInCondation(Object[] condantion, Class<?> clz) {
        StringBuilder outcome = new StringBuilder();
        String[] strs = (String[]) condantion;
        for (String str : strs) {
            if (clz == String.class) {
                outcome.append("'");
            }
            outcome.append(str.trim());
            if (clz == String.class) {
                outcome.append("'");
            }
            outcome.append(",");
        }
        outcome.replace(outcome.length() - 1, outcome.length(), "");

        logger.info(outcome.toString());
        return outcome.toString();
    }

    /**
     * for primefaces datatable
     *
     * @param filters
     * @param sql
     * @param filter like
     * @return sql
     */
    private StringBuffer formatVOSql(Map<String, Object> filters, String filter) {
        StringBuffer sb = new StringBuffer();
        sb.append(voClass.getAnnotation(VOHelper.class).preWhere()).append(filter);

        if (filters != null && !filters.isEmpty()) {
            Set<String> columns = filters.keySet();
            for (String column : columns) {
                String condantion = filters.get(column).toString();
                Object object = ObjectUtil.getPropertyType(voClass, column);
                if (object != null) {
                    if (ObjectUtil.isString(object) || ObjectUtil.isDouble(object)) {
                        sb.append(" AND ").append(column).append(" LIKE '%").append(condantion).append("%' ");
                    } else if (ObjectUtil.isInterger(object)) {
                        if (ObjectUtil.canConverterToInteger(condantion)) {
                            sb.append(" AND ").append(column).append(" = ").append(condantion);
                        }
                    } else if (ObjectUtil.isDate(object)) {
                        if (DateUitl.isYyyyDDddDate(condantion)) {
                            sb.append(" AND ").append(column).append(" = '").append(condantion).append("' AND ");
                        }
                    } else if (ObjectUtil.isShort(object)) {
                        sb.append(" AND ").append(column).append(" = '").append(condantion).append("' ");
                    } else if (ObjectUtil.isBigInteger(object)) {
                        sb.append(" AND ").append(column).append(" = '").append(condantion).append("' ");
                    } else if (ObjectUtil.isCharacter(object)) {
                        sb.append(" AND ").append(column).append(" = '").append(condantion).append("' ");
                    } else if (ObjectUtil.isBoolean(object)) {
                        sb.append(" AND ").append(column).append(" = '").append(Boolean.valueOf(condantion)).append("' ");
                    } else {// other data type
                        logger.warn("datatable column filter has other data type: " + object.getClass());
                    }
                }
            }
        }

        return sb;
    }

    /**
     * find by entity field
     *
     * createQuery
     *
     * @param column Entity attribute
     * @param data condition
     * @return Entity List
     */
    public List<T> findByField(String column, Object data) {
        if (column == null || "".equals(column) || data == null) {
            return new ArrayList<>();
        }
        String sql = "SELECT o FROM " + entityClass.getSimpleName() + " o WHERE o." + column + " = '" + data + "'";
        try {
            TypedQuery<T> q = createQuery(sql, entityClass);
            logger.debug("findByField: " + sql);
            // q.setParameter("data", data);
            return q.getResultList();
        } catch (Exception e) {
            logger.error(sql, e);
            return new ArrayList<>();
        }
    }

    /**
     * createNativeQuery
     *
     *
     * @param column table column
     * @param data
     * @return entity list
     */
    protected List<T> findByTableColumn(String column, String data) {
        String sql = "SELECT * FROM " + getTableName() + " WHERE " + column + " = :data";
        try {

            Query q = getEntityManager().createNativeQuery(sql, entityClass);
            q.setParameter("data", data);

            return q.getResultList();
        } catch (Exception e) {
            logger.error(sql, e);
        }
        return new ArrayList<T>();
    }

    /**
     *
     * @param field entity field
     * @param data field data
     * @return single entity, return null if result is empty
     */
    public T findSingleByField(String field, Object data) {
        String sql = "SELECT o FROM " + entityClass.getSimpleName() + " o WHERE o." + field + " = :data";
        try {
            TypedQuery<T> q = createQuery(sql, entityClass);
            q.setParameter("data", data);
            return q.getSingleResult();
        } catch (NoResultException e) {
            // logger.error(e, sql);
        } catch (NonUniqueResultException e) {
            // TODO: check newInstance
            // return entityClass.newInstance();
            logger.error(sql, e);
        } catch (Exception e) {
            logger.error(sql, e);
        }
        return null;
    }

    /**
     * find by entity class and column field
     *
     * @param c entity class
     * @param field entity attribute, not table column
     * @param data column data
     * @return entity but not T
     */
    public Object findByColumnAndEntityClass(Class<?> c, String field, Object data) {
        String sql = "SELECT o FROM " + c.getSimpleName() + " o WHERE o." + field + " = :data";
        try {
            Query q = createQuery(sql);
            q.setParameter("data", data);
            return q.getSingleResult();
        } catch (NoResultException e) {
        } catch (NonUniqueResultException e) {
            logger.error("\r\n\t findByColumn: SQL:" + sql, e);
        } catch (Exception e) {
            logger.error("\r\n\t findByColumn: SQL:" + sql, e);
        }
        return null;
    }

    /**
     * find entity by JPQL
     *
     * @param jpql SELECT o Object o
     * @return Entity list
     */
    protected List<T> findByJPQL(String jpql) {
        try {
            List<T> outcome = createQuery(jpql, entityClass).getResultList();
            if (outcome == null) {
                outcome = new ArrayList<T>(0);
            }
            return outcome;
        } catch (Exception e) {
            logger.error("\r\n\t findByJPQL: SQL:" + jpql, e);
            return new ArrayList<T>();
        }
    }

    protected List findByJPQL(String jpql, Class cl) {
        try {
            List outcome = createQuery(jpql, cl).getResultList();
            if (outcome == null) {
                outcome = new ArrayList<T>();
            }
            return outcome;
        } catch (Exception e) {
            logger.error("\r\n\t findByJPQL: SQL:" + jpql, e);
            return new ArrayList<T>();
        }
    }

    /**
     * find entity by JPQL
     *
     * @param jpql SELECT o Object o
     * @param limit
     * @return Entity list
     */
    protected List<T> findByJPQL(String jpql, int limit) {
        try {
            TypedQuery query = createQuery(jpql, entityClass);
            if (limit > 0) {
                query.setMaxResults(limit);
            }
            return query.getResultList();
        } catch (Exception e) {
            logger.error("findByJPQL: SQL:" + jpql, e);
            return new ArrayList<T>();
        }
    }

    protected List<T> findByJPQL(Class<T> c, String jpql) {
        try {
            return createQuery(jpql, c).getResultList();
        } catch (Exception e) {
            logger.error("\r\n\t findByJPQL: SQL:" + jpql, e);
            return new ArrayList<T>();
        }
    }

    /**
     * find entity by JPQL
     *
     * @param jpql SELECT o Object o
     * @return Entity list
     */
    protected int findCountByJPQL(String jpql) {
        int outcome = 0;
        try {

            outcome = ((Number) getEntityManager().createQuery(jpql).getSingleResult()).intValue();

        } catch (Exception e) {
            logger.error("\r\n\t findCountByJPQL: SQL:" + jpql, e);
        }
        return outcome;
    }

    /**
     * find Single Entity by JPQL
     *
     * @param jpql eg: SELECT o Object o
     * @return Single Entity
     */
    protected T findSingleByJPQL(String jpql) {
        try {
            return (T) createQuery(jpql, entityClass).getSingleResult();
            // return createQuery(jpql, entityClass).getSingleResult();
        } catch (NoResultException e) {
            logger.warn("no result jpql:(NoResultException) \n" + jpql);
        } catch (NonUniqueResultException e) {
            logger.error("no result jpql(NonUniqueResultException): \n" + jpql);
        } catch (Exception e) {
            logger.error("\r\n\t findSingleByJPQL: SQL:" + jpql, e);
        }
        return null;
    }

    /**
     *
     * @param selectOne multiple or radio
     * @param selectLable which column will show on the view page
     * @param column entity attribute
     * @param o condition
     * @param i18n 是否 i18n
     * @return SelectItem[String]
     * @//deprecated
     */
    public SelectItem[] getSelectItems(SingularAttribute selectLable, String column, Object o, boolean selectOne, boolean i18n) {
        // TODO: condtition
        List<T> entities;
        if (column != null && o != null) {
            entities = findByField(column, o);
        } else {
            entities = findAll();
        }

        int size = selectOne ? entities.size() + 1 : entities.size();
        // int size = entities.size() + 1;
        SelectItem[] items = new SelectItem[size];
        int i = 0;
        if (selectOne) {
            items[0] = new SelectItem("", MessageBundle.getLocalizedString("select"));
            i++;
        }
        try {
            for (T x : entities) {
                Class<?> cla = x.getClass();
                Object oo = x;
                String lab = ObjectUtil.columnNameToGetMethos(selectLable.getName());
                // if (lab.contains(".")) {
                // String[] str = lab.split("\\.");
                // for (int j = 0; j < str.length - 1; j++) {
                // Method m = cla.getMethod(str[j].replace("(", "").replace(")",
                // ""));
                // if (!oo.getClass().equals(String.class)) {
                // oo = m.invoke(oo);
                // lab = str[j + 1];
                // cla = oo.getClass();
                // } else {
                // break;
                // }
                // }
                // }

                Method lable = cla.getMethod(lab);
                String showLable = (String) lable.invoke(oo == null ? x : oo);
                if (i18n) {
                    showLable = MessageBundle.getLocalizedString(showLable);
                }
                items[i++] = new SelectItem(x.getId(), showLable);
            }
        } catch (Exception e) {
            // TODO: logger
            logger.error(e);

        }
        return items;
    }

    /**
     *
     * @param selectLable
     * @param column 表列名对应实体属性名
     * @param o 属性匹配条件 =
     * @param selectOne 是否显示请选择
     * @return SelectItem[]
     */
    public SelectItem[] getSelectItems(SingularAttribute selectLable, String column, Object o, boolean selectOne) {
        // TODO: condtition
        List<T> entities;
        if (column != null && o != null) {
            entities = findByField(column, o);
        } else {
            entities = findAll();
        }

        int size = selectOne ? entities.size() + 1 : entities.size();
        // int size = entities.size() + 1;
        SelectItem[] items = new SelectItem[size];
        int i = 0;
        if (selectOne) {
            items[0] = new SelectItem("", MessageBundle.getLocalizedString("select"));
            i++;
        }
        try {
            for (T x : entities) {
                Class<?> cla = x.getClass();
                Object oo = x;
                String lab = ObjectUtil.columnNameToGetMethos(selectLable.getName());
                if (lab.contains(".")) {
                    String[] str = lab.split("\\.");
                    for (int j = 0; j < str.length - 1; j++) {
                        Method m = cla.getMethod(str[j].replace("(", "").replace(")", ""));
                        if (!oo.getClass().equals(String.class)) {
                            oo = m.invoke(oo);
                            lab = str[j + 1];
                            cla = oo.getClass();
                        } else {
                            break;
                        }

                    }
                }

                Method lable = cla.getMethod(lab);
                String showLable = (String) lable.invoke(oo == null ? x : oo);
                items[i++] = new SelectItem(x.getId(), showLable);
            }
        } catch (Exception e) {
            // TODO: logger
            logger.error(e);

        }
        return items;
    }

    /**
     * get entity by entity class and id
     *
     * @param c entity class
     * @param id entity id
     * @return Entity , but not T
     */
    protected Object find(Class<?> c, Object id) {
        Object outcome = null;

        outcome = getEntityManager().find(c, id);

        return outcome;
    }

    /**
     * 根 据 实 体 和 列 查 找 最 新 插 入 实 体
     *
     * @param c entity class
     * @param maxTokenColumn entity attribute, eg: name or createTime
     * @return entity
     */
    protected Object findLastByToken(Class<?> c, String maxTokenColumn) {

        try {
            String maxTokenSql = "SELECT max(o." + maxTokenColumn + ") FROM " + c.getSimpleName() + " o";

            Query maxTokenQuery = createQuery(maxTokenSql);

            String maxEntitySql = "SELECT o FROM " + c.getSimpleName() + " o where o." + maxTokenColumn + " = :token";

            Query maxEntityQuery = createQuery(maxEntitySql);
            maxEntityQuery.setParameter("token", maxTokenQuery.getSingleResult());

            return maxEntityQuery.getSingleResult();
        } catch (Exception e) {
            logger.error(e);
            return null;
        }
    }

    /**
     *
     * @return entity table name
     */
    protected String getTableName() {
        return entityClass.getAnnotation(Table.class).name();
    }

    protected void clearCache() {
        try {
            // getEntityManager().getEntityManager().getCache().evictAll();
            getEntityManager().getEntityManagerFactory().getCache().evictAll();
        } catch (IllegalStateException e) {
            logger.error(e);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private Query createQuery(String jpql) {
        Query outcome = null;
        try {

            outcome = getEntityManager().createQuery(jpql);

        } catch (IllegalArgumentException e) {
            logger.error(e);
        } catch (Exception e) {
            logger.error(e);
        } finally {
            // getEntityManager().close();
        }

        return outcome;
    }

    private TypedQuery<E> createVOQuery(String sql, Class c) {
        TypedQuery<E> outcome = createVOQuery(sql, c, null);
        return outcome;
    }

    private TypedQuery<E> createVOQuery(String sql, Class c, Map<String, Object> parms) {
        TypedQuery<E> outcome = null;
        try {
            outcome = getEntityManager().createQuery(sql, c);
            if (parms != null && parms.size() > 0) {
                for (String key : parms.keySet()) {
                    outcome.setParameter(key, parms.get(key));
                }
            }
        } catch (IllegalArgumentException e) {
            logger.error(e);
        } catch (Exception e) {
            logger.error(e);
        }
        return outcome;
    }

    private TypedQuery<T> createQuery(String sql, Class c) {
        TypedQuery<T> outcome = createQuery(sql, c, null);
        return outcome;
    }

    private TypedQuery<T> createQuery(String sql, Class c, Map<String, Object> parms) {
        TypedQuery<T> outcome = null;
        try {
            outcome = getEntityManager().createQuery(sql, c);
            if (parms != null && parms.size() > 0) {
                for (String key : parms.keySet()) {
                    outcome.setParameter(key, parms.get(key));
                }
            }
        } catch (IllegalArgumentException e) {
            logger.error(e);
        } catch (Exception e) {
            logger.error(e);
        }
        return outcome;
    }

    // @AroundInvoke
    // private Object interceptor(InvocationContext ic) {
    // Object o = null;
    // try {
    // Map<String, Object> contextData = ic.getContextData();
    // Set<String> keySet =
    // contextData.keySet();
    // StringBuilder context = new StringBuilder();
    // for (String key : keySet) {
    // context.append(key)
    // .append(" :")
    // .append(contextData.get(key))
    // .append("\r\n");
    // }
    // Object[] parameters = ic.getParameters();
    // StringBuilder parameter = new StringBuilder();
    // if (parameters != null) {
    // for (int i = 0; i < parameters.length; i++) {
    // parameter.append(parameters[i] != null ? parameters[i].toString()
    // : parameters[i]).append(" ");
    // }
    // }
    // //logger.info(aClass,
    // // "[method]" + methodName + " [parameters] " + parameters + " [context]"
    // + context);
    //
    // o = ic.proceed();
    // } catch (Exception e) { //
    // logger.error(e);
    // GlobalException.setException("001", e);
    // }
    //
    // return o;
    // }
    /**
     * TODO:软 删 除
     *
     * @param id entity id
     * @return
     */
    public FacesMessage virtualDelete(String id) {
        FacesMessage outMessage = new FacesMessage();
        return outMessage;
    }

    public FacesMessage removeBatch(List<T> ts) {
        // TODO:i18n message
        FacesMessage outMessage = new FacesMessage();
        if (ts.size() < 1 || ts.get(0).getId() == null || ObjectUtil.isEmpty(ts.get(0).getId().toString())) {
            outMessage.setSeverity(FacesMessage.SEVERITY_INFO);
            outMessage.setSummary("batch remove");
            outMessage.setDetail("remove records is empty");
        } else {
            String sql = "delete from " + getTableName() + " where ID in (" + getInCondation(ts) + ")";
            int count = excuteUpdateNativeSql(sql);
            if (count == ts.size()) {
                outMessage.setSeverity(FacesMessage.SEVERITY_INFO);
                outMessage.setSummary("batch remove");
                outMessage.setDetail("remove success " + count);
            } else {
                outMessage.setSeverity(FacesMessage.SEVERITY_WARN);
                outMessage.setSummary("batch remove faild");
                outMessage.setDetail("remove success " + count);
            }
        }
        // TODO rowback
        return outMessage;
    }

    public FacesMessage removeBatchByIds(List<String> ts) {
        // TODO:i18n message
        FacesMessage outMessage = new FacesMessage();
        if (ts.size() < 1 || ObjectUtil.isEmpty(ts.get(0))) {
            outMessage.setSeverity(FacesMessage.SEVERITY_INFO);
            outMessage.setSummary("batch remove");
            outMessage.setDetail("remove fales id is empty");
        } else {
            String sql = "delete from " + getTableName() + " where ID in (" + getInCondationIds(ts) + ")";
            int count = excuteUpdateNativeSql(sql);
            if (count == ts.size()) {
                outMessage.setSeverity(FacesMessage.SEVERITY_INFO);
                outMessage.setSummary("batch remove");
                outMessage.setDetail("remove success " + count);
            } else {
                outMessage.setSeverity(FacesMessage.SEVERITY_WARN);
                outMessage.setSummary("batch remove faild");
                outMessage.setDetail("remove success " + count);
            }
        }
        // TODO rowback
        return outMessage;
    }

    private String getInCondation(List<T> ts) {
        StringBuilder outCome = new StringBuilder("");
        for (T t : ts) {
            outCome.append("'").append(t.getId()).append("',");
        }
        outCome.append("''");
        return outCome.toString();
    }

    private String getInCondationIds(List<String> ts) {
        StringBuilder outCome = new StringBuilder("");
        for (String t : ts) {
            outCome.append("'").append(t).append("',");
        }
        outCome.append("''");
        return outCome.toString();
    }

    /**
     * switch sort
     *
     * @param switchSortFromId
     * @param switchSortFrom
     * @param switchSortToId
     * @param switchSortTo
     * @return
     */
    public FacesMessage switchSort(String switchSortFromId, String switchSortFrom, String switchSortToId, String switchSortTo) {
        FacesMessage fmFrom = update(switchSortFromId, BaseColumnModel_.sort, switchSortTo);
        FacesMessage fmTo = update(switchSortToId, BaseColumnModel_.sort, switchSortFrom);
        fmFrom.setSummary("交换排序");
        if (fmFrom.getSeverity().equals(FacesMessage.SEVERITY_INFO) && fmTo.getSeverity().equals(FacesMessage.SEVERITY_INFO)) {
            fmFrom.setDetail("操作成功");
        } else {
            fmFrom.setSeverity(FacesMessage.SEVERITY_WARN);
            fmFrom.setDetail("操作失败");
        }
        return fmFrom;
    }

    public String getNextSort() {
        try {
            // TODO: check error [B cannot be cast to java.lang.String
            // String sql = "SELECT max(concat(o.sort+1,'')) FROM " +
            // getEntityClass().getSimpleName() + " o";
            // TODO: ClassCastException: [B cannot be cast to java.lang.String
            // return findObjectByJPQL(sql, String.class);
        } catch (Exception e) {
            logger.error("[getNextSort]", e);
        }
        return "";
    }

    public T findObjectByJPQL(String sql, Class<T> cl) throws BaseExceptoin {
        try {

            return getEntityManager().createQuery(sql, cl).getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception e) {
            logger.error("findObjectByJPQL \n" + sql + "\n" + e);
            throw new BaseExceptoin(e);
        }
    }

    public List<?> findListByJPQL(String sql, Class<?> cl) {
        try {

            TypedQuery<?> createQuery = getEntityManager().createQuery(sql, cl);
            if (createQuery != null) {
                return createQuery.getResultList();
            }
        } catch (Exception e) {
            logger.error(sql, e);
        }
        return java.util.Collections.EMPTY_LIST;
    }

    public int excuteUpdateJPQL(String sql) {
        int outcome = 0;
        try {
            Query q = getEntityManager().createQuery(sql);
            outcome = q.executeUpdate();
            logger.info("excuteUpdateJPQL: " + sql);
        } catch (Exception e) {
            logger.error("excuteUpdateJPQL error with sql:" + sql);
            logger.error(e);
        }

        return outcome;
    }

    /**
     * 更 新 table
     *
     * @param id table id
     * @param field entity field
     * @param value new value
     * @return FacesMessage
     */
    public FacesMessage updateField(Object id, String field, Object value) {
        // TODO: validation
        FacesMessage outMessage = new FacesMessage("update");
        Object object = ObjectUtil.getPropertyType(entityClass, field);
        if (ObjectUtil.isBoolean(object)) {
            value = Boolean.valueOf(value.toString());
        }
        // TODO: other dataType
        Query query = getEntityManager().createQuery(
                "UPDATE " + getEntityClass().getSimpleName() + " o SET o." + field + " = :field WHERE o." + getIdFieldName() + " = :id");
        if (ObjectUtil.isDate(object)) {
            query.setParameter("field", (Date) value, TemporalType.TIMESTAMP);
        } else {
            query.setParameter("field", value);
        }

        query.setParameter("id", id);
        int count = query.executeUpdate();
        if (count == 1) {
            outMessage.setDetail(MessageBundle.SUCCESS);
            outMessage.setSeverity(FacesMessage.SEVERITY_INFO);
        } else {
            outMessage.setDetail(MessageBundle.FAILURE);
            outMessage.setSeverity(FacesMessage.SEVERITY_WARN);
        }
        return outMessage;
    }

    public FacesMessage validationField(String field, Object value) {
        // TODO
        FacesMessage outcome = new FacesMessage(FacesMessage.SEVERITY_INFO, "input validation", "");
        return outcome;
    }

    /**
     * TODO: in testing
     *
     * @param datas
     * @param columns
     * @param maps
     * <table column,BaseColumnModel>
     * @param voClassz
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws ClassNotFoundException
     * @throws NoSuchFieldException
     * @throws SecurityException
     * @throws IntrospectionException
     */
    private List<E> copyFields(List<Object[]> datas, String[] columns, Map<String, BaseColumnModel> maps, Class<E> voClassz) throws InstantiationException,
            IllegalAccessException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException, NoSuchFieldException,
            SecurityException, IntrospectionException {
        List<E> outcome = new ArrayList<>();

        for (Object[] row : datas) {
            for (int i = 0; i < columns.length; i++) {
                E vo = voClassz.newInstance();
                BaseColumnModel bcm = maps.get(columns[i]);
                String fieldName = bcm.getField();
                PropertyDescriptor pd = new PropertyDescriptor(voClassz.getDeclaredField(fieldName).getName(), voClassz);
                Method setMethod = pd.getWriteMethod();

                if (bcm.getDataType().equals(String.class.getName())) {
                    setMethod.invoke(vo, row[i]);
                } else if (bcm.getDataType().equals(Integer.class.getName())) {
                    setMethod.invoke(vo, Integer.valueOf(row[i].toString()));
                } else if (bcm.getDataType().equals(Boolean.class.getName())) {
                    setMethod.invoke(vo, Boolean.valueOf(row[i].toString()));
                } else if (bcm.getDataType().equals(Boolean.class.getName())) {
                    setMethod.invoke(vo, Boolean.valueOf(row[i].toString()));
                } else {
                    logger.error("TODO: converter other type: " + bcm.getDataType());
                }

                outcome.add(vo);
            }

            // ID
            // o.setId(String.valueOf(row[0]));
            // o.setSort(String.valueOf(row[1]));
        }
        return outcome;
    }

    public String getIdFieldName() {
        String outcome = "";
        Field[] fields = getEntityClass().getDeclaredFields();
        try {
            Field idField = null;
            for (Field field : fields) {
                Annotation ann = field.getAnnotation(Id.class);
                if (ann != null) {
                    idField = field;
                    break;
                }
            }
            if (idField != null) {
                outcome = idField.getName();
            }
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return outcome;
    }
    
    public void updateOtherEntity(Class<? extends AbstractEntity> t){
        getEntityManager().merge(t);
}
}
