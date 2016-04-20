package demo.ejb;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import com.primefaces.ext.base.ejb.PrimefacesExtEntityManagerProvider;

/**
 *
 * @author Jason
 */
@Stateless
public class PrimefacesExtSPIImpl implements PrimefacesExtEntityManagerProvider {

    @PersistenceContext(unitName = "demo")
    private EntityManager em;

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

}
