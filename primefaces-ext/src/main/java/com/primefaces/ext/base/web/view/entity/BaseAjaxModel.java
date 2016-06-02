package com.primefaces.ext.base.web.view.entity;

import com.primefaces.ext.base.entity.AbstractEntity;
import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author Jason
 */
@Table(name = "BASE_AJAX_MODEL")
@Entity
public class BaseAjaxModel extends AbstractEntity implements Serializable {

    @ManyToOne
    private BasePanelModel panelModel;

    @Id
    @GeneratedValue(generator = "system-uuid")
    @Column(name = "ID", nullable = false, length = 32)
    @Basic
    private String id;
    @Basic
    private String event;
    @Basic
    private String updates;
    @Basic
    private String process;
    @Basic
    private String globals;
    @Basic
    private String async;
    @Basic
    private String oncomplete;
    @Basic
    private String onerror;
    @Basic
    private String onsuccess;
    @Basic
    private String onstart;
    @Basic
    private String listener;
    @Basic
    private Boolean immediates;
    @Basic
    private Boolean disabled;
    @Basic
    private Boolean partialSubmit;
    @Basic
    private Boolean resetValues;
    @Basic
    private Boolean ignoreAutoUpdate;
    @Basic
    private String delay;
    @Basic
    private Integer timeout;
    @Basic
    private String partialSubmitFilter;
    private String form;
    @Basic
    private String skipChildren;
    @Basic
    private String customeKey;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public BasePanelModel getPanelModel() {
        return panelModel;
    }

    public void setPanelModel(BasePanelModel panelModel) {
        this.panelModel = panelModel;
    }

    public String getCustomeKey() {
        return customeKey;
    }

    public void setCustomeKey(String customeKey) {
        this.customeKey = customeKey;
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public String getAsync() {
        return async;
    }

    public void setAsync(String async) {
        this.async = async;
    }

    public String getOncomplete() {
        return oncomplete;
    }

    public void setOncomplete(String oncomplete) {
        this.oncomplete = oncomplete;
    }

    public String getOnerror() {
        return onerror;
    }

    public void setOnerror(String onerror) {
        this.onerror = onerror;
    }

    public String getOnsuccess() {
        return onsuccess;
    }

    public void setOnsuccess(String onsuccess) {
        this.onsuccess = onsuccess;
    }

    public String getOnstart() {
        return onstart;
    }

    public void setOnstart(String onstart) {
        this.onstart = onstart;
    }

    public String getListener() {
        return listener;
    }

    public void setListener(String listener) {
        this.listener = listener;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public Boolean getPartialSubmit() {
        return partialSubmit;
    }

    public void setPartialSubmit(Boolean partialSubmit) {
        this.partialSubmit = partialSubmit;
    }

    public Boolean getResetValues() {
        return resetValues;
    }

    public void setResetValues(Boolean resetValues) {
        this.resetValues = resetValues;
    }

    public Boolean getIgnoreAutoUpdate() {
        return ignoreAutoUpdate;
    }

    public void setIgnoreAutoUpdate(Boolean ignoreAutoUpdate) {
        this.ignoreAutoUpdate = ignoreAutoUpdate;
    }

    public String getDelay() {
        return delay;
    }

    public void setDelay(String delay) {
        this.delay = delay;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public String getPartialSubmitFilter() {
        return partialSubmitFilter;
    }

    public void setPartialSubmitFilter(String partialSubmitFilter) {
        this.partialSubmitFilter = partialSubmitFilter;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public String getSkipChildren() {
        return skipChildren;
    }

    public void setSkipChildren(String skipChildren) {
        this.skipChildren = skipChildren;
    }

    public String getUpdates() {
        return updates;
    }

    public void setUpdates(String updates) {
        this.updates = updates;
    }

    public String getGlobals() {
        return globals;
    }

    public void setGlobals(String globals) {
        this.globals = globals;
    }

    public Boolean getImmediates() {
        return immediates;
    }

    public void setImmediates(Boolean immediates) {
        this.immediates = immediates;
    }

}
