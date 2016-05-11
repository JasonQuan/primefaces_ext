package com.primefaces.ext.base.web;

import com.primefaces.ext.base.web.view.dao.DashboardSB;
import com.primefaces.ext.base.web.view.dao.PanelModelSB;
import com.primefaces.ext.base.web.view.entity.AjaxModel;
import com.primefaces.ext.base.web.view.entity.BaseDashboardModel;
import com.primefaces.ext.base.web.view.entity.PanelModel;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.faces.view.facelets.FaceletContext;
import org.primefaces.behavior.ajax.AjaxBehavior;
import org.primefaces.behavior.ajax.AjaxBehaviorListenerImpl;
import org.primefaces.component.dashboard.Dashboard;
import org.primefaces.component.panel.Panel;
import org.primefaces.event.ToggleEvent;

/**
 *
 * @author 041863
 */

public abstract class BaseDashboardMB {

    private Dashboard dashboard;
    private List<PanelModel> panels;
    private BaseDashboardModel basedashboard;
    @EJB
    private DashboardSB dashboardSB;
    @EJB
    private PanelModelSB panelModelSB;

    public void ajaxListener() {
    }

    protected String getCustomeKey() {
        return "default";
    }

    public void restoreDashboard() {
        StringBuilder sb = new StringBuilder();
        for (PanelModel p : panels) {
            sb.append(p.getColumnIndex()).append(",").append(p.getId()).append(",").append(p.getItemIndex()).append("|");
        }
        sb.replace(sb.length() - 1, sb.length(), "");

        dashboardSB.update(basedashboard.getId(), "details", sb.toString());
        basedashboard = dashboardSB.findAll().get(0);
    }

    @PostConstruct
    public void init() {
        panels = panelModelSB.findByField("customeKey", getCustomeKey());
        basedashboard = dashboardSB.findByField("customeKey", getCustomeKey()).get(0);
        if (basedashboard.getDetails() == null) {
            restoreDashboard();
        }
        dashboard = new Dashboard();
        dashboard.setModel(basedashboard);
        dashboard.setStyle(basedashboard.getStyle());
        dashboard.setStyleClass(basedashboard.getStyleClass());
        dashboard.setDisabled(basedashboard.getDisabled());
        FacesContext fc = FacesContext.getCurrentInstance();
        Application application = fc.getApplication();
        ELContext elContext = fc.getELContext();
        FaceletContext faceletContext = (FaceletContext) FacesContext.getCurrentInstance().getAttributes().get(FaceletContext.FACELET_CONTEXT_KEY);
        for (PanelModel p : panels) {
            Panel panel = (Panel) application.createComponent(fc, "org.primefaces.component.Panel", "org.primefaces.component.PanelRenderer");
            panel.setId(p.getId());
            panel.setHeader(p.getHeader());
            panel.setClosable(p.getClosable());
            panel.setToggleable(p.getToggleable());
            panel.setStyle(p.getStyle());
            panel.setToggleOrientation(p.getToggleOrientation());
            panel.setCollapsed(p.getCollapsed());
            panel.setStyleClass(p.getStyleClass());//TODO: other fields

            ExpressionFactory ef = fc.getApplication().getExpressionFactory();
            for (AjaxModel a : p.getAjaxModels()) {
                AjaxBehavior behavior = new AjaxBehavior();
                behavior.setOncomplete(a.getOncomplete());
                behavior.setOnsuccess(a.getOnsuccess());
                MethodExpression ex = ef.createMethodExpression(elContext, a.getListener() != null ? a.getListener() : "#{request.removeAttribute('no')}", null, new Class[]{});
                behavior.addAjaxBehaviorListener(new AjaxBehaviorListenerImpl(ex, ex));
                panel.addClientBehavior(a.getEvent(), behavior);
            }
            getDashboard().getChildren().add(panel);
            if (p.getInclude() != null) {
                try {
                    faceletContext.includeFacelet(panel, p.getInclude());
                } catch (IOException ex) {
                    Logger.getLogger(BaseDashboardMB.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public Dashboard getDashboard() {
        return dashboard;
    }

    public void setDashboard(Dashboard dashboard) {
        this.dashboard = dashboard;
    }

    public List<PanelModel> getPanels() {
        return panels;
    }

    public void setPanels(List<PanelModel> panels) {
        this.panels = panels;
    }

    public void updateOrder() {
        String dashboardOrder = FacesContext.getCurrentInstance().
                getExternalContext().getRequestParameterMap().get("newDashboardOrder");
        basedashboard = dashboardSB.find(basedashboard.getId());
        dashboardSB.update(basedashboard.getId(), "details", dashboardOrder);
    }

    public void onToggle(ToggleEvent event) {
        panelModelSB.update(event.getComponent().getId(), "collapsed", event.getVisibility().name().equals("VISIBLE"));
    }
}
