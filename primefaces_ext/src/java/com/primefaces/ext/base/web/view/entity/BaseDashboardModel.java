package com.primefaces.ext.base.web.view.entity;

import com.primefaces.ext.base.entity.AbstractEntity;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.primefaces.model.DashboardColumn;
import org.primefaces.model.DashboardModel;
import org.primefaces.model.DefaultDashboardColumn;

/**
 *
 * @author Jason
 */
@Table(name = "BASE_DASHBOARD_MODEL")
@Entity
public class BaseDashboardModel extends AbstractEntity implements DashboardModel, Serializable {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @Column(name = "ID", nullable = false, length = 32)
    private String id;

    @Column(name = "widgetVar")
    private String widgetVar;
    @Column(name = "model")
    private String model;
    @Column(name = "disabled")
    private boolean disabled;
    @Column(name = "style")
    private String style;
    @Column(name = "styleClass")
    private String styleClass;
    @Transient
    private List<DashboardColumn> columns;
    @Column(name = "details")
    private String details;
    @Basic
    private String customeKey;

    public String getCustomeKey() {
        return customeKey;
    }

    public void setCustomeKey(String customeKey) {
        this.customeKey = customeKey;
    }

    @Override
    public List<DashboardColumn> getColumns() {
        columns = new ArrayList<DashboardColumn>();
        if (getDetails() != null && !getDetails().equals("")) {
            String[] cols = getDetails().split("\\|");
            for (String col1 : cols) {
                String[] items = col1.split(",");
                int columnIndex = Integer.valueOf(items[0]);
                DashboardColumn col = null;
                if (columns.size() >= columnIndex + 1) {
                    col = columns.get(columnIndex);
                }
                if (col == null) {
                    col = new DefaultDashboardColumn();
                    columns.add(col);
                }
                col.addWidget(items[1]);
            }
        }
        return columns;
    }

    @Override
    public void addColumn(DashboardColumn column) {
        columns.add(column);
    }

    @Override
    public int getColumnCount() {
        return getDetails().split("|").length;
    }

    @Override
    public DashboardColumn getColumn(int index) {
        return getColumns().get(index);
    }

    @Override
    public void transferWidget(DashboardColumn fromColumn, DashboardColumn toColumn, String widgetId, int index) {

    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setColumns(List<DashboardColumn> columns) {
        this.columns = columns;
    }

    public String getWidgetVar() {
        return widgetVar;
    }

    public void setWidgetVar(String widgetVar) {
        this.widgetVar = widgetVar;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getStyleClass() {
        return styleClass;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

}
