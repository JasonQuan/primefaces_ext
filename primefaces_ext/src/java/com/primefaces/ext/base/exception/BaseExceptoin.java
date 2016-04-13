package com.primefaces.ext.base.exception;

/**
 *
 * @author Jason
 */
@SuppressWarnings("serial")
public class BaseExceptoin extends Exception {

    public BaseExceptoin() {
    }

    public BaseExceptoin(Throwable t) {
        super(t);
    }

    public BaseExceptoin(String msg) {
        super(msg);
    }

}
