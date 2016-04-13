package com.primefaces.ext.base.entity;

import java.io.Serializable;

/**
 *
 * @author Jason Chen 22-May-2012
 */
public interface BaseEntity extends Serializable {

	/**
	 * <br>
	 * get entity primary key<br/>
	 *
	 * @return entity primary key
	 */
	Object getId();


//	String getSort();
//
//	void setSort(String sort);

	/**
	 * Judge whether the entity is new
	 *
	 * @return boolean if id is null return true, else return false
	 */
	//boolean isNew();

	@Override
	boolean equals(Object obj);

	@Override
	int hashCode();

	@Override
	String toString();
}
