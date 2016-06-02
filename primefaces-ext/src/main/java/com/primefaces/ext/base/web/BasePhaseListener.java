package com.primefaces.ext.base.web;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.primefaces.ext.base.util.BaseObject;

/**
 *
 * @author Jason
 */
public class BasePhaseListener extends BaseObject implements PhaseListener {

	private static final long serialVersionUID = 1L;

	public BasePhaseListener() {
	}

	@Override
	public void afterPhase(PhaseEvent event) {
	}

	@Override
	public void beforePhase(PhaseEvent event) {
	}

	@Override
	public PhaseId getPhaseId() {
		return PhaseId.RENDER_RESPONSE;
	}

	@SuppressWarnings("unused")
	private void distroySession(HttpServletRequest request) {
		HttpSession session = request.getSession();
		session.invalidate();
	}
}
