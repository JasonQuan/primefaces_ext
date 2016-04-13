package com.primefaces.ext.base.web;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.persistence.metamodel.SingularAttribute;

import com.primefaces.ext.base.entity.AbstractEntity;
import com.primefaces.ext.base.util.BaseLogger;
import com.primefaces.ext.base.util.BaseObject;
import com.primefaces.ext.base.util.MessageBundle;

/**
 * in testing
 *
 * @author Jason
 */
@SuppressWarnings("rawtypes")
public class ForeignConverter<T extends AbstractEntity, E extends AbstractEntity> extends BaseObject implements Converter {
	private SingularAttribute foreignField;
	private BaseMB<T, E> baseMB;
	private final BaseLogger logger = new BaseLogger(this.getClass());
	public ForeignConverter(SingularAttribute foreignField, BaseMB<T, E> baseController) {
		this.foreignField = foreignField;
		this.baseMB = baseController;
	}

	@Override
	public T getAsObject(FacesContext context, UIComponent toValidate, String value) {
		Object foreginEntity = null;
		String viewId = toValidate.getId();
		if (baseMB.verificationId(viewId) || value == null || "".equals(value)) {
			return null;
		}

		try {
			Class<?> entityClass = baseMB.getEntity().getClass();
			String field = baseMB.getIdByViewId(viewId);
			Field declaredField = entityClass.getDeclaredField(field);
			Class<?> foreignClass = declaredField.getType();
			Method method = entityClass.getMethod(baseMB.getSetForegin(field), foreignClass);
			foreginEntity = baseMB.dao().findByColumnAndEntityClass(foreignClass, foreignField.getName(), value);

			if (foreginEntity != null) {
				method.invoke(baseMB.getEntity(), foreginEntity);
			} else {
				MessageBundle.validatorMessage(FacesMessage.SEVERITY_WARN, toValidate, context, value.toString(), MessageBundle.DOES_NOT_EXIST);
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return null;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent toValidate, Object value) {
		return value.toString();
	}
}
