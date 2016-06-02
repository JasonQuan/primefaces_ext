package demo.ejb;

import java.io.Serializable;

import javax.ejb.Stateless;


import com.primefaces.ext.base.web.view.dao.PrimefacesExtEJB;

import demo.entity.Students;

@Stateless
public class StudentsSB extends PrimefacesExtEJB<Students,Students> implements Serializable {
}
