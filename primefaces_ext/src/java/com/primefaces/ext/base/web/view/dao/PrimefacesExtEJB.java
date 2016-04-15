package com.primefaces.ext.base.web.view.dao;

import javax.persistence.EntityManager;

import com.primefaces.ext.base.ejb.BaseEJB;
import com.primefaces.ext.base.entity.AbstractEntity;
import javax.inject.Inject;
import com.primefaces.ext.base.ejb.PrimefacesExtEntityManagerProvider;

public abstract class PrimefacesExtEJB<T extends AbstractEntity, E extends AbstractEntity> extends BaseEJB<T, E> {

    public PrimefacesExtEJB() {
    }

    private static final long serialVersionUID = 1L;

    private EntityManager em;
    @Inject
    private javax.enterprise.inject.Instance<PrimefacesExtEntityManagerProvider> services;

    @Override
    protected EntityManager getEntityManager() {
        try {
            em = services.get().getEntityManager();
            if (em == null) {
                throw new Exception("null EntityManager found");
            }
        } catch (Exception e) {
            logger.error("TODO: message");
            logger.error(e);
        }

        return em;
    }
}
