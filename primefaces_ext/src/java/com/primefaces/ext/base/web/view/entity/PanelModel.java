package com.primefaces.ext.base.web.view.entity;

import com.primefaces.ext.base.entity.AbstractEntity;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author 041863
 */
@Table(name = "Panel_Model")
@Entity
public class PanelModel extends AbstractEntity implements Serializable {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @Column(name = "ID", nullable = false, length = 32)
    private String id;
    private String widgetVar;
    private String header;
    private String footer;
    private boolean toggleable;
    private String toggleSpeed;
    private String style;
    private String styleClass;
    private boolean collapsed;
    private boolean closable;
    private String closeSpeed;
    private boolean visible;
    private String closeTitle;
    private String toggleTitle;
    private String menuTitle;
    private String toggleOrientation;
    private String include;
    private String customeKey;
    private int columnIndex;
    private int itemIndex;
    @OneToMany(mappedBy = "panelModel")
    private List<AjaxModel> ajaxModels;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<AjaxModel> getAjaxModels() {
        return ajaxModels;
    }

    public void setAjaxModels(List<AjaxModel> ajaxModels) {
        this.ajaxModels = ajaxModels;
    }

    public String getInclude() {
        return include;
    }

    public void setInclude(String include) {
        this.include = include;
    }

    public String getWidgetVar() {
        return widgetVar;
    }

    public void setWidgetVar(String widgetVar) {
        this.widgetVar = widgetVar;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getFooter() {
        return footer;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    public boolean getToggleable() {
        return toggleable;
    }

    public void setToggleable(boolean toggleable) {
        this.toggleable = toggleable;
    }

    public String getToggleSpeed() {
        return toggleSpeed;
    }

    public void setToggleSpeed(String toggleSpeed) {
        this.toggleSpeed = toggleSpeed;
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

    public boolean getCollapsed() {
        return collapsed;
    }

    public void setCollapsed(boolean collapsed) {
        this.collapsed = collapsed;
    }

    public boolean getClosable() {
        return closable;
    }

    public void setClosable(boolean closable) {
        this.closable = closable;
    }

    public String getCloseSpeed() {
        return closeSpeed;
    }

    public void setCloseSpeed(String closeSpeed) {
        this.closeSpeed = closeSpeed;
    }

    public boolean getVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getCloseTitle() {
        return closeTitle;
    }

    public void setCloseTitle(String closeTitle) {
        this.closeTitle = closeTitle;
    }

    public String getToggleTitle() {
        return toggleTitle;
    }

    public void setToggleTitle(String toggleTitle) {
        this.toggleTitle = toggleTitle;
    }

    public String getMenuTitle() {
        return menuTitle;
    }

    public void setMenuTitle(String menuTitle) {
        this.menuTitle = menuTitle;
    }

    public String getToggleOrientation() {
        return toggleOrientation;
    }

    public void setToggleOrientation(String toggleOrientation) {
        this.toggleOrientation = toggleOrientation;
    }

    public String getCustomeKey() {
        return customeKey;
    }

    public void setCustomeKey(String customeKey) {
        this.customeKey = customeKey;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public void setColumnIndex(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    public int getItemIndex() {
        return itemIndex;
    }

    public void setItemIndex(int itemIndex) {
        this.itemIndex = itemIndex;
    }

}
