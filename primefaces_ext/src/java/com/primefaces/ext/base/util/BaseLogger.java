package com.primefaces.ext.base.util;

import java.io.Serializable;

import org.jboss.logging.Logger;
 
public class BaseLogger implements Serializable{
	protected static Logger logger;

	public BaseLogger(Class<?> clazz) {
		logger = Logger.getLogger(clazz);
	}

	public void debug(Object o) {
		logger.debug(o);
	}

	public void info(Object o) {
		logger.info(o);
	}

	public void error(Object o, Throwable t) {
		logger.error(o, t);
	}

	public void error(Object o) {
		// TODO: email
		logger.error(o);
	}

	public void fatal(Object o) {
		logger.fatal(o);
	}

	public void fatal(Object o, Throwable t) {
		logger.fatal(o, t);
	}

	public void trace(Object o) {
		logger.trace(o);
	}

	public void warn(Object o) {
		logger.warn(o);
	}
}
