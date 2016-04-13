package com.primefaces.ext.base.util;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * for dymanic columns on primefaces5.2 datatable<br/>
 * auto set default value for config entity field this function only for easy
 * init data, we can change or install new config after online
 *
 * @author JasonChen
 */
@Documented
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ColumnHelper {

	String header() default "";

	String style() default "text-align: center;";

	boolean exportable() default true;

	boolean editAble() default true;

	boolean visible() default true;

	String footer() default "";

	String filterOptions() default "";

	boolean toggleable() default true;

	boolean filterable() default true;

	String validatorMessage() default "";

	String validateRegex() default "";

	String validatorId() default "";

	String filterPlaceHolder() default "";

	String dropDown() default "";

	String extFunction() default "";

	String tips() default "";

	String width() default "40px";

	String sort() default "999";

	String tableColumn() default "";

	String onupdate() default ":growl,entity_table";

	String oncomplete() default "";

	String onstart() default "";

	String onsuccess() default "";

	String extIcon() default "ui-icon-search";

	String extValue() default "";

	String title() default "";

	String outFormat() default "";

	boolean result() default true;

	boolean isMultiFilter() default false;

	boolean sortable() default true;
}
