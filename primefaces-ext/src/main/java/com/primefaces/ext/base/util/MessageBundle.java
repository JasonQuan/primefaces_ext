package com.primefaces.ext.base.util;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import org.apache.log4j.Logger;

/**
 *
 * @author Jason
 */
public class MessageBundle {
    // private static final String MESSAGE_BUNDLE = "resources.messages";

    private static final Logger LOGGER = Logger.getLogger(MessageBundle.class);

    /**
     * @deprecated @param key
     * @return
     */
    public static String getLocalizedString(String key) {
        // FacesContext ctx = FacesContext.getCurrentInstance();
        String outcome = key;
        /*
		 * try { ResourceBundle bundle =
		 * ResourceBundle.getBundle(MESSAGE_BUNDLE,
		 * ctx.getViewRoot().getLocale());
		 * 
		 * if (bundle.containsKey(key)) { outcome = bundle.getString(key); }
		 * else { outcome = key; logger.debug("NO I18N str: " + key); } } catch
		 * (NullPointerException e) { // return key; // TODO: logger
		 * logger.warn(e); } catch (MissingResourceException e) {
		 * logger.error(e); }
         */
        return outcome;
    }

    /**
     * must set i18n message
     *
     * @param message
     */
    public static void showError(String message) {
        showMessage(message, FacesMessage.SEVERITY_ERROR);
    }

    /**
     * must set i18n message
     *
     * @param message
     */
    public static void showInfo(String message) {
        showMessage(message, FacesMessage.SEVERITY_INFO);
    }

    public static void showInfo(String summary, String message) {
        showMessage(summary, message, FacesMessage.SEVERITY_INFO);
    }

    public static void showMessage(String message, Severity severity) {
        try {
            FacesContext ctx = FacesContext.getCurrentInstance();
            ctx.addMessage(null, new FacesMessage(severity, "Info", message));
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    public static void showMessage(String summary, String message, Severity severity) {
        try {
            FacesContext ctx = FacesContext.getCurrentInstance();
            ctx.addMessage(null, new FacesMessage(severity, summary, message));
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    public static void showWarning(String message) {
        showMessage(message, FacesMessage.SEVERITY_WARN);
    }

    public static void showWarning(String summary, String detail) {
        showMessage(summary, detail, FacesMessage.SEVERITY_WARN);
    }

    public static void showLocalError(String message) {
        showError(getLocalizedString(message));
    }

    public static void showLocalInfo(String message) {
        showInfo(getLocalizedString(message));
    }

    public static void showLocalWarning(String message) {
        showWarning(getLocalizedString(message));
    }

    /**
     * don't send i18n Detail and Summary , it will auto i18n
     *
     * @param message
     */
    public static void autoMessage(FacesMessage message) {
        if (message != null) {
            message.setDetail(getLocalizedString(message.getDetail()));
            message.setSummary(getLocalizedString(message.getSummary()));
            FacesContext ctx = FacesContext.getCurrentInstance();
            ctx.addMessage(null, message);
        } else {
            LOGGER.warn("[autoMessage] null value");
        }
    }

    /**
     *
     * @param severity
     * @param toValidate
     * @param context
     * @param object show object + message
     * @param msg i18n message
     */
    public static void validatorMessage(Severity severity, UIComponent toValidate, FacesContext context, String object, String msg) {
        FacesMessage message = new FacesMessage(object + " " + getLocalizedString(msg));
        message.setSeverity(severity);
        context.addMessage(toValidate.getClientId(context), message);
        ((UIInput) toValidate).setValid(false);
    }

    public static final String ALREADY_EXISTS = "already_exists";
    public static final String FAILURE = "failure";
    public static final String ERROR = "error";
    public static final String REQUIRED = "required";
    public static final String EXCEPTION = "exception";
    public static final String OBJECT_DOES_NOT_EXIST = "object_does_not_exist";
    public static final String DOES_NOT_EXIST = "does_not_exist";
    public static final String IN_USING_CAN_NOT_REMOVE = "in_using_can_not_remove";
    public static final String SUCCESS = "success";
    public static final String DUPLICATION_OF_DATA = "duplication_of_data";
    public static final String UPDATE = "update";
    public static String CREATE = "create";
    public static String REMOVE = "remove";
    public static String ACCOUNT_NAME_AND_PASSWORD_CAN_NOT_BE_EMPTY = "account_name_and_password_can_not_be_empty";
    public static String ACOUNT_AND_PASSWORD_DO_NOT_MATCH = "account_and_password_do_not_match";
}
