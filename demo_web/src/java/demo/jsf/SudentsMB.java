package demo.jsf;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.primefaces.ext.base.ejb.BaseEJB;
import com.primefaces.ext.base.web.BaseMB;

import demo.ejb.StudentsSB;
import demo.entity.Students;

@ManagedBean
@ViewScoped
public class SudentsMB extends BaseMB<Students, Students> {

    @EJB
    private StudentsSB studentsSB;

    @Override
    protected BaseEJB<Students, Students> dao() {
        return studentsSB;
    }
}
