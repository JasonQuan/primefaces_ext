package com.primefaces.ext.base.ejb;

import javax.persistence.EntityManager;

public interface PrimefacesExtEntityManagerProvider {

    EntityManager getEntityManager();
}
