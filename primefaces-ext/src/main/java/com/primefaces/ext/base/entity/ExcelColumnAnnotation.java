package com.primefaces.ext.base.entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//TODO:delete
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExcelColumnAnnotation {

	int index();

	boolean required() default true;

	String pattern() default "";

	String validatonMessage();
}
