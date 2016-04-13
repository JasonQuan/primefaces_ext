package com.primefaces.ext.base.web.view.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.primefaces.ext.base.ejb.BaseEJB;
import com.primefaces.ext.base.entity.AbstractEntity;

public abstract class PrimefacesExtEJB<T extends AbstractEntity, E extends AbstractEntity> extends BaseEJB<T, E> {

    public PrimefacesExtEJB() {
    }

    private static final long serialVersionUID = 1L;

    @PersistenceContext
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
