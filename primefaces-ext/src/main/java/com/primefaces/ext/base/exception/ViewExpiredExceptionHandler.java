package com.primefaces.ext.base.exception;

import java.io.IOException;
import java.util.Iterator;
import javax.faces.FacesException;
import javax.faces.FactoryFinder;
import javax.faces.application.ViewExpiredException;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;


import org.primefaces.context.RequestContext;

/**
 *
 * @author Jason Jul 4, 2011
 */
public class ViewExpiredExceptionHandler extends ExceptionHandlerWrapper {

	private static final Logger logger = Logger.getLogger(ViewExpiredExceptionHandler.class);
	private ExceptionHandler wrapped;
	private String redirectPage;

	public ViewExpiredExceptionHandler(ExceptionHandler wrapped, String redirectPage) {
		this.wrapped = wrapped;
		this.redirectPage = redirectPage;
	}

	@Override
	public ExceptionHandler getWrapped() {
		return this.wrapped;
	}

	@Override
	public void handle() throws FacesException {
		for (Iterator<ExceptionQueuedEvent> i = getUnhandledExceptionQueuedEvents().iterator(); i.hasNext();) {
			ExceptionQueuedEvent event = i.next();
			ExceptionQueuedEventContext context = (ExceptionQueuedEventContext) event.getSource();
			Throwable t = context.getException();

			if (t instanceof ViewExpiredException) {
				ViewExpiredException vee = (ViewExpiredException) t;
				FacesContext fc = FacesContext.getCurrentInstance();
				HttpSession session = (HttpSession) fc.getExternalContext().getSession(true);
				try {
					session.setAttribute("currentViewId", vee.getViewId());
				} finally {
					i.remove();
				}

				try {

					doRedirect(fc, redirectPage);
				} catch (Exception e) {
					logger.error(e);
				}
				break;
			}
		}
		getWrapped().handle();
	}

	public void doRedirect(FacesContext fc, String redirectPage) throws FacesException {
		ExternalContext ec = fc.getExternalContext();

		try {
			// todo: JEE 7
			if (ec.isResponseCommitted()) {
				return;
			}
			if ((RequestContext.getCurrentInstance().isAjaxRequest() || fc.getPartialViewContext().isPartialRequest()) && fc.getResponseWriter() == null
					&& fc.getRenderKit() == null) {
				ServletResponse response = (ServletResponse) ec.getResponse();
				ServletRequest request = (ServletRequest) ec.getRequest();
				response.setCharacterEncoding(request.getCharacterEncoding());

				RenderKitFactory factory = (RenderKitFactory) FactoryFinder.getFactory(FactoryFinder.RENDER_KIT_FACTORY);
				RenderKit renderKit = factory.getRenderKit(fc, fc.getApplication().getViewHandler().calculateRenderKitId(fc));
				ResponseWriter responseWriter = renderKit.createResponseWriter(response.getWriter(), null, request.getCharacterEncoding());

				fc.setResponseWriter(responseWriter);
			}

			ec.redirect(ec.getRequestContextPath() + (redirectPage != null ? redirectPage : ""));
		} catch (NullPointerException e) {
			logger.error(e);
			try {
				ec.redirect(ec.getRequestContextPath() + (redirectPage != null ? redirectPage : ""));
			} catch (IOException ex) {
				logger.error(ex);
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}
}
