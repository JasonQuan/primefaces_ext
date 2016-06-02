package com.primefaces.ext.base.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.ResourceBundle;

import javax.el.BeanELResolver;
import javax.el.ELContext;

/**
 *
 * @author Jason
 *
 */
public class DynamicDataTableExtendBeanElResolver extends BeanELResolver {

    private final String FIX = "cc.attrs.";

    @Override
    public Object getValue(ELContext context, Object base, Object property) {
        try {
            if (property == null || base == null || base instanceof ResourceBundle || base instanceof Map || base instanceof Collections
                    || base instanceof ArrayList) {
                if (property != null && property.toString().startsWith(FIX)) {
                    //return super.getValue(context, base, property);
                } else {
                    return null;
                }
            }
            String propertyString = property.toString();
            if (propertyString.startsWith("primefaces")) {
                return null;
            }
            if (!propertyString.startsWith(FIX) && propertyString.contains(".")) {
                Object value = base;
                for (String properPart : propertyString.split("\\.")) {
                    value = super.getValue(context, value, properPart);
                }
                return value;
            } else {
                return super.getValue(context, base, property);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return super.getValue(context, base, property);
        }
    }
}
