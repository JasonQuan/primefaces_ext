package com.primefaces.ext.base.exception;

import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;

/**
 *Example
 * <code>public class ViewExceptionHanderImpl extends ViewExpiredExceptionHandlerFactory {</code>
 *
 * <code>      public ViewExceptionHanderImpl(ExceptionHandlerFactory parent) {</code>
 * <code>         super(parent);</code>
 * <code>   }
 *
 * <code>     public String redirectPage() {</code>
 * <code>        return SystemConfig.Exception_SeesionTimeOut;</code>
 * <code>  }</code>
 * <code> }</code>
 *
 * <code> <factory></code>
 * <code><exception-handler-factory>*.ViewExceptionHanderImpl</exception-handler-factory></code>
 * <code></factory></code>
 *
 * @author Jason Jul 4, 2011
 */
public abstract class ViewExpiredExceptionHandlerFactory extends ExceptionHandlerFactory {

    private ExceptionHandlerFactory parent;

    /**
     * 
     * @return view expired error page path
     */
    public abstract String redirectPage(); //="/faces/exception/sessionTimeOut.xhtml";

    public ViewExpiredExceptionHandlerFactory() {
    }

    public ViewExpiredExceptionHandlerFactory(ExceptionHandlerFactory parent) {
        this.parent = parent;
    }

    @Override
    public ExceptionHandler getExceptionHandler() {
        ExceptionHandler result = parent.getExceptionHandler();
        result = new ViewExpiredExceptionHandler(result, redirectPage());
        return result;
    }
}