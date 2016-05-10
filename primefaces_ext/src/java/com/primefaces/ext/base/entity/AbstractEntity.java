package com.primefaces.ext.base.entity;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.CacheType;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;

/**
 * TODO:@SqlResultSetMappings<br/>
 * TODO:@ColumnResult <br/>
 * TODO:@Customizer history <br/>
 * TODO: (EntityManager)(new
 * InitialContext()).lookup("java:comp/ejb/EntityManager");<br/>
 * 
 * @AttributeOverride
 * @ExcludeSuperclassListeners
 * @author Jason
 */
@MappedSuperclass
// @EntityListeners(value = { AbstractEntityListeners.class })
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.INVALIDATE, type = CacheType.FULL, size = 999999)
public abstract class AbstractEntity implements Cloneable, BaseEntity, Serializable {

	private static final long serialVersionUID = 1L;
	@Transient
	private Boolean rendered;
 
	@Column(name = "CREATED_BY")
	private String createdBy;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_TIME")
	private Date createdTime;

	@Version
	@Column(name = "\"VERSION\"")
	private Integer version;

	@Override
	public Object getId() {
		Object outcome = "";
		Field[] fields = this.getClass().getDeclaredFields();
		try {
			Field idField = null;
			for (Field field : fields) {
				field.setAccessible(true);
				Id ann = field.getAnnotation(Id.class);
				if (ann != null) {
					idField = field;
					break;
				}
				EmbeddedId eid = field.getAnnotation(EmbeddedId.class);
				if (eid != null) {
					idField = field;
					break;
				}
			}

			if (idField != null) {
				outcome = idField.get(this);
			}
		} catch (Exception e) {
//			Logger logger = LoggerFactory.getLogger(this.getClass());
//			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return outcome;
	}

	public Boolean getRendered() {
		if (rendered == null) {
			rendered = Boolean.TRUE;
		}
		return rendered;
	}

	public void setRendered(Boolean rendered) {
		this.rendered = rendered;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (!(obj instanceof BaseEntity)) {
			return false;
		}

		if (getClass() != obj.getClass()) {
			return false;
		}

		final BaseEntity other = (BaseEntity) obj;
		return getId().equals(other.getId());
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 67 * hash + (this.getId() != null ? this.getId().hashCode() : 0);
		return hash;
	}

	@Override
	public String toString() {
		Class<?> obj = this.getClass();
		Field[] fields = obj.getDeclaredFields();
		StringBuffer sb = new StringBuffer(this.getClass().getName()).append("\n[");
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				sb.append(field.getName()).append(": ");
				if (field.getType().getName().equals("java.util.List")) {
					sb.append("{is list}");
				} else {
                                        for(Annotation a:field.getAnnotations()){
                                            if(a.annotationType().getPackage().getName().startsWith("javax.persistence")){
                                                sb.append(field.get(this));                                        
                                            }
                                        }
				}
				sb.append("\n");
			} catch (Exception e) {
//				Logger logger = LoggerFactory.getLogger(this.getClass());
//				logger.error(e.getMessage());
				e.printStackTrace();
			}
		}
		sb.append("]");
		return sb.toString();
	}

	public Object clone() throws CloneNotSupportedException {
		Object o = null;
		o = super.clone();
		return o;
	}

	public String getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedTime() {
		return this.createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

}
