package com.primefaces.ext.base.ejb;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import com.primefaces.ext.base.util.BaseLogger;
import com.primefaces.ext.base.web.view.dao.BaseColumnModelSB;

/**
 *
 * @author Jason
 * @date 2015-7-3
 */
//@Startup
//@Singleton
public class AppStartUp {

    @EJB
    private BaseColumnModelSB baseColumnModelSB;
    private BaseLogger LOGGER = new BaseLogger(this.getClass());

    @PostConstruct
    protected void initCache() {
        long start = System.currentTimeMillis();
        LOGGER.info("start init dynamic column configuration");
        baseColumnModelSB.findAll();
        long end = System.currentTimeMillis();
        LOGGER.info("end init dynamic column configuration , using " + (end - start) / 1000 + " ms");

    }
}
