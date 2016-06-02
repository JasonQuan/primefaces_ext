package com.primefaces.ext.base.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.persistence.jaxb.MarshallerProperties;

import com.primefaces.ext.base.entity.AbstractEntity;

/**
 *
 * @author Jason 16-Jul-2011
 */
public class ObjectUtil {

    protected static BaseLogger logger = new BaseLogger(ObjectUtil.class);

    /**
     *
     * @param cls
     * @param propertyName Field Name 增加取父类功能
     * @return
     */
    public static Object getPropertyType(Class<?> cls, String propertyName) {
        Object retvalue = null;
        try {
            if (propertyName.contains(".")) {
                String[] type = propertyName.split("\\.");

                for (int i = 0; i < type.length - 1; i++) {
                    cls = cls.getDeclaredField(type[i]).getType();
                }

                propertyName = type[type.length - 1];
            }
            Field field = cls.getDeclaredField(propertyName);
            // field.setAccessible(true);
            retvalue = field.getType();
        } catch (Exception e) {
            try {
                Field field = cls.getSuperclass().getDeclaredField(propertyName);
                retvalue = field.getType();
            } catch (Exception ex) {
                logger.error(e);
            }
        }
        return retvalue;
    }

    public static boolean isInt(Object o) {
        return o.equals(int.class);
    }
    public static boolean isInterger(Object o) {
        return o.equals(Integer.class);
    }

    public static boolean isArray(Object o) {
        return o.getClass().isArray();
    }

    public static boolean isString(Object o) {
        return o.equals(String.class);
    }

    public static boolean isDouble(Object o) {
        return o.equals(Double.class);
    }

    public static boolean isLong(Object o) {
        return o.equals(Long.class);
    }

    public static boolean islong(Object o) {
        return o.equals(long.class);
    }

    public static boolean isDate(Object o) {
        return o.equals(Date.class);
    }

    public static boolean isBigInteger(Object o) {
        return o.equals(BigInteger.class);
    }

    public static boolean isShort(Object o) {
        return o.equals(Short.class);
    }

    public static boolean isCharacter(Object o) {
        return o.equals(Character.class);
    }

    public static boolean canConverterToInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 首 字 母 转 大 写
     *
     * @param str
     * @return String
     */
    public static String toUpperCase(String str) {
        String[] a = str.split("");
        return str.replaceFirst(a[1], a[1].toUpperCase());
    }

    /**
     * 首 字 母 转 小 写
     *
     * @param str
     * @return String
     */
    public static String toLowerCase(String str) {
        String[] a = str.split("");
        return str.replaceFirst(a[1], a[1].toLowerCase());
    }

    /**
     * 将表名转为实体名
     *
     * @param str table name
     * @return String entity name
     */
    public static String tableNameToEntityName(String str) {
        String[] split = str.split("_");
        StringBuilder entityName = new StringBuilder();
        for (int i = 0; i < split.length; i++) {
            entityName.append(toUpperCase(split[i]));
        }
        return entityName.toString();
    }

    /**
     * 将表列名转为实体属性名
     *
     * @param str table column name
     * @return String entity filed name
     */
    public static String columnNameToFieldName(String str) {
        String tableNameToEntityName = tableNameToEntityName(str);
        return toLowerCase(tableNameToEntityName);
    }

    public static String getEntityFildeNameByEntityMethodName(String methodName) {
        String fildeName = methodName.replace("get", "");
        return toLowerCase(fildeName);
    }

    /**
     *
     * @param filedName entity filed name
     * @return
     */
    public static String getGetMethodNameByFiledName(String filedName) {
        String start = "get";
        if (filedName.startsWith("is")) {
            start = "is";
        }
        String methodName = start + toUpperCase(filedName);
        return methodName;
    }

    /**
     * 通过表列名得到实体方法名
     *
     * @param str table column name
     * @return entity field name
     */
    public static String columnNameToGetMethos(String str) {
        String tableNameToEntityName = tableNameToEntityName(str);
        return "get" + toUpperCase(tableNameToEntityName);
    }

    public static boolean eamilValidation(String email) {
        // TODO:try
        String p = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(p);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * in testing
     *
     * @param <T>
     * @param a
     * @return
     */
    // TODO:分析可变参数
    @SafeVarargs
    public static <T> List<T> asList(T... a) {
        ArrayList<T> arrayList = new ArrayList<T>();
        arrayList.addAll(Arrays.asList(a));
        return arrayList;
    }

    /**
     * in testing
     *
     * @param <T>
     * @param a
     * @return
     */
    public static <T> List<T> removeDuplicate(List<T> a) {
        HashSet<T> hashSet = new HashSet<T>(a);
        a.clear();
        a.addAll(hashSet);
        return a;
    }

    public static boolean isNotEmpty(String str) {
        return (str != null && !"".equals(str) && str.length() > 0);
    }

    public static boolean isEmpty(String str) {
        return (str == null || "".equals(str) || str.length() == 0);
    }

    /**
     * entity name converter to table name
     *
     * @param cla entity class
     * @param filedName entity filedName
     * @return entity field type,table name
     */
    public static BaseKeyValue filedNameToColumnName(Class<?> cla, String filedName) {
        BaseKeyValue outcome = null;
        String tableColumnName = filedName.toLowerCase();
        Field field = null;
        try {
            field = cla.getSuperclass().getDeclaredField(filedName);
        } catch (Exception ex) {
            // logger.error(ex,
            // "[filedNameToColumnName]\ncheck entity filed name\n" + cla +
            // "\n[filed]" + filedName);

            try {
                field = cla.getDeclaredField(filedName);
            } catch (Exception ex1) {
                Logger.getLogger(ObjectUtil.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
        if (field != null) {
            Class<?> fieldType = field.getType();
            if (String.class.equals(fieldType) || Double.class.equals(fieldType) || fieldType.isPrimitive()) {
                tableColumnName = field.getAnnotation(Column.class).name();
            } else if (fieldType.getSuperclass().equals(AbstractEntity.class)) {
                tableColumnName = field.getAnnotation(JoinColumn.class).name();
            }
            outcome = new BaseKeyValue(fieldType.getSimpleName(), tableColumnName);
            // TODO:other type
            // if(field.getType().getSuperclass() == AbstractEntity.class){

            // }
        }
        return outcome;
    }

    public static String getAppPath(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
    }

    /**
     * JSF Download
     *
     * @param path file path
     */
    @SuppressWarnings("unused")
    private void downloadFile(String path) {
        InputStream fis = null;
        try {
            File file = new File(path);
            fis = new FileInputStream(file);
            byte[] buf = new byte[fis.available()];
            int offset = 0;
            int numRead = 0;
            while ((offset < buf.length) && ((numRead = fis.read(buf, offset, buf.length - offset)) >= 0)) {
                offset += numRead;
            }
            fis.close();
            HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
            response.setContentType("application/zip");
            String name = path.substring(path.lastIndexOf("/") + 1);
            logger.debug(name);
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(name, "UTF-8") + "");
            response.getOutputStream().write(buf);
            response.getOutputStream().flush();
            response.getOutputStream().close();
            FacesContext.getCurrentInstance().responseComplete();
        } catch (Exception ex) {
            logger.error(ex);
        } finally {
            try {
                fis.close();
            } catch (Exception ex) {
                logger.error(ex);
            }
        }
    }

    public static String getEntityUUID() {
        return UUID.randomUUID().toString().toUpperCase().replaceAll("-", "");
    }

    public static String getEntityTimeID() {
        return String.valueOf(System.currentTimeMillis());
    }

    public static String escape(String src) {
        int i;
        char j;
        StringBuffer tmp = new StringBuffer();
        tmp.ensureCapacity(src.length() * 6);
        for (i = 0; i < src.length(); i++) {
            j = src.charAt(i);
            if (Character.isDigit(j) || Character.isLowerCase(j) || Character.isUpperCase(j)) {
                tmp.append(j);
            } else if (j < 256) {
                tmp.append("%");
                if (j < 16) {
                    tmp.append("0");
                }
                tmp.append(Integer.toString(j, 16));
            } else {
                tmp.append("%u");
                tmp.append(Integer.toString(j, 16));
            }
        }
        return tmp.toString();
    }

    public static String unescape(String src) {
        StringBuffer tmp = new StringBuffer();
        tmp.ensureCapacity(src.length());
        int lastPos = 0, pos = 0;
        char ch;
        while (lastPos < src.length()) {
            pos = src.indexOf("%", lastPos);
            if (pos == lastPos) {
                if (src.charAt(pos + 1) == 'u') {
                    ch = (char) Integer.parseInt(src.substring(pos + 2, pos + 6), 16);
                    tmp.append(ch);
                    lastPos = pos + 6;
                } else {
                    ch = (char) Integer.parseInt(src.substring(pos + 1, pos + 3), 16);
                    tmp.append(ch);
                    lastPos = pos + 3;
                }
            } else if (pos == -1) {
                tmp.append(src.substring(lastPos));
                lastPos = src.length();
            } else {
                tmp.append(src.substring(lastPos, pos));
                lastPos = pos;
            }
        }
        return tmp.toString();
    }

    /**
     *
     * @param cla entity class
     * @return [field,[column,dataType]]
     */
    public static Map<String, BaseKeyValue> getEntityFieldWithTableColumn(Class<?> cla) {
        return getEntityFieldWithTableColumn("", cla);
    }

    // TODO: list data type
    public static Map<String, BaseKeyValue> getEntityFieldWithTableColumn(String fieldName, Class<?> cla) {
        Field[] fieldses = cla.getDeclaredFields();
        Map<String, BaseKeyValue> outcome = new HashMap<>();
        String field = "";
        String columnName = "";
        for (Field fieldse : fieldses) {
            field = fieldse.getName();
            // System.out.println(fieldse.getType().getName());
            try {
                if ("serialVersionUID".equals(field) || fieldse.getAnnotation(EmbeddedId.class) != null || fieldse.getAnnotation(Id.class) != null
                        || fieldse.getAnnotation(Lob.class) != null || fieldse.getType().getName().equals(List.class.getName())) {
                    continue;
                }
                if (fieldse.getType().getSuperclass() == AbstractEntity.class) {
                    outcome.putAll(getEntityFieldWithTableColumn(field + ".", fieldse.getType()));
                    continue;
                }
                // if (fieldse.getAnnotation(Transient.class) != null) {
                // outcome.put(fieldName + field, new BaseKeyValue(field,
                // fieldse.getType().getName()));
                // continue;
                // }
                Column column = fieldse.getAnnotation(Column.class);

                if (column != null && StringUtils.isNotBlank(column.name())) {
                    columnName = column.name();
                } else {
                    columnName = field.toUpperCase();
                }

                if (isEmpty(columnName)) {
                    columnName = field.toUpperCase();
                }
                outcome.put(fieldName + field, new BaseKeyValue(columnName, fieldse.getType().getName()));
            } catch (Exception e) {
                logger.warn("method getEntityFieldWithTableColumn error:" + e.getMessage());
            }
        }
        return outcome;
    }

    public static Map<String, ColumnHelper> getEntityFieldWithColumnHelper(Class<?> cla) {
        Field[] fieldses = cla.getDeclaredFields();
        Map<String, ColumnHelper> outcome = new HashMap<>();
        String field = "";
        for (Field fieldse : fieldses) {
            field = fieldse.getName();
            try {
                ColumnHelper annotation = fieldse.getAnnotation(ColumnHelper.class);
                outcome.put(field, annotation);
            } catch (Exception e) {
                logger.warn("method getEntityFieldWithTableColumn error:" + e.getMessage());
            }
        }
        return outcome;
    }

    public static boolean isBoolean(Object o) {
        return o.equals(Boolean.class);
    }

    public static String converterListTOString(Object[] toArray) {
        StringBuilder sb = new StringBuilder(",");
        for (Object str : toArray) {
            sb.append(",").append(str);
        }
        sb.append(",");
        return sb.toString().replaceFirst(",,", "");
    }

    /**
     *
     * @param result jpql like ’SELECT o.code,o.name FROM‘
     * @return
     */
    public static List<SelectItem> getSelectItems(List<Object[]> result) {
        if (result == null || result.isEmpty()) {
            return new ArrayList<SelectItem>(0);
        }

        List<SelectItem> outcome = new ArrayList<SelectItem>(result.size());
        for (int i = 0; i < result.size(); i++) {
            outcome.add(new SelectItem(result.get(i)[0], result.get(i)[1].toString()));
        }
        return outcome;
    }

    public static SelectItem[] getSelectItemsAll(List<Object[]> result) {
        if (result == null || result.isEmpty()) {
            return new SelectItem[0];
        }

        SelectItem[] outcome = new SelectItem[result.size() + 1];

        outcome[0] = new SelectItem("ALL", "ALL");
        for (int i = 0; i < result.size(); i++) {
            outcome[i + 1] = new SelectItem(result.get(i)[0], result.get(i)[1].toString());
        }
        return outcome;
    }

    public static SelectItem[] getSelectItemsAll(Object[] result) {
        if (result == null || result.length == 0) {
            return new SelectItem[0];
        }

        SelectItem[] outcome = new SelectItem[result.length + 1];

        outcome[0] = new SelectItem("ALL", "ALL");
        for (int i = 0; i < result.length; i++) {
            outcome[i + 1] = new SelectItem(result[i], result[i].toString());
        }
        return outcome;
    }

    public static Map<String, String> getMapItems(List<Object[]> result) {
        if (result == null || result.isEmpty()) {
            return new HashMap<String, String>(0);
        }

        Map<String, String> outcome = new HashMap<String, String>();
        for (int i = 0; i < result.size(); i++) {
            outcome.put(result.get(i)[0].toString(), result.get(i)[1].toString());
        }
        return outcome;
    }

    @SuppressWarnings("rawtypes")
    public static Class getSuperClassGenricType(Class clazz, int index) throws IndexOutOfBoundsException {

        Type genType = clazz.getGenericSuperclass();

        if (!(genType instanceof ParameterizedType)) {
            return Object.class;
        }

        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

        if (index >= params.length || index < 0) {
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            return Object.class;
        }
        return (Class<?>) params[index];
    }

    public static <T> T getJsonObject(Class<T> class1, String json) throws Exception {
        //TODO: detail exception handle
        try {
            Unmarshaller unmarshaller = JAXBContext.newInstance(class1).createUnmarshaller();
            unmarshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
            unmarshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
            T bean = unmarshaller.unmarshal(new StreamSource(new StringReader(json)), class1).getValue();
            return bean;
        } catch (Exception e) {
            logger.error("getJsonObject error class: " + class1 + " JSON: " + json);
            throw e;
        }
    }
}
