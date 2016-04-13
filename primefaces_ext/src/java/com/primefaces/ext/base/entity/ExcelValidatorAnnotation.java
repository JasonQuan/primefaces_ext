package com.primefaces.ext.base.entity;

@java.lang.annotation.Target(value = { java.lang.annotation.ElementType.TYPE })
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface ExcelValidatorAnnotation {

	int startRow() default 1;

	int sheetAt() default 0;

	int messageColumn() default 27;
}
