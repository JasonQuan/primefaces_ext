package com.primefaces.ext.base.web;

import javax.faces.context.ExceptionHandlerFactory;

import com.primefaces.ext.base.exception.ViewExpiredExceptionHandlerFactory;

public class ViewExpriedExceptionHandlerImpl extends ViewExpiredExceptionHandlerFactory {

    public ViewExpriedExceptionHandlerImpl(ExceptionHandlerFactory parent) {
        super(parent);
    }

    @Override
    public String redirectPage() {
        return "/page/expried.xhtml";
    }

}
