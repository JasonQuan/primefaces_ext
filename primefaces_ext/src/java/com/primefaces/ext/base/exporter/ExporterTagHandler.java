package com.primefaces.ext.base.exporter;

import java.io.IOException;

import javax.el.ELException;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.ActionSource;
import javax.faces.component.UIComponent;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.FaceletException;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.TagConfig;
import javax.faces.view.facelets.TagHandler;

public class ExporterTagHandler extends TagHandler {

    private final TagAttribute target;
    private final TagAttribute type;
    private final TagAttribute fileName;
    private final TagAttribute pageOnly;
    private final TagAttribute selectionOnly;
    private final TagAttribute preProcessor;
    private final TagAttribute postProcessor;
    private final TagAttribute encoding;
    private final TagAttribute repeat;

    public ExporterTagHandler(TagConfig tagConfig) {
        super(tagConfig);
        this.target = getRequiredAttribute("target");
        this.type = getRequiredAttribute("type");
        this.fileName = getRequiredAttribute("fileName");
        this.pageOnly = getAttribute("pageOnly");
        this.selectionOnly = getAttribute("selectionOnly");
        this.encoding = getAttribute("encoding");
        this.preProcessor = getAttribute("preProcessor");
        this.postProcessor = getAttribute("postProcessor");
        this.repeat = getAttribute("repeat");
    }

    @Override
    public void apply(FaceletContext faceletContext, UIComponent parent) throws IOException, FacesException, FaceletException, ELException {
        if (ComponentHandler.isNew(parent)) {
            ValueExpression targetVE = target.getValueExpression(faceletContext, Object.class);
            ValueExpression typeVE = type.getValueExpression(faceletContext, Object.class);
            ValueExpression fileNameVE = fileName.getValueExpression(faceletContext, Object.class);
            ValueExpression pageOnlyVE = null;
            ValueExpression selectionOnlyVE = null;
            ValueExpression encodingVE = null;
            MethodExpression preProcessorME = null;
            MethodExpression postProcessorME = null;
            ValueExpression repeatVE = null;

            if (encoding != null) {
                encodingVE = encoding.getValueExpression(faceletContext, Object.class);
            }
            if (pageOnly != null) {
                pageOnlyVE = pageOnly.getValueExpression(faceletContext, Object.class);
            }
            if (selectionOnly != null) {
                selectionOnlyVE = selectionOnly.getValueExpression(faceletContext, Object.class);
            }
            if (preProcessor != null) {
                preProcessorME = preProcessor.getMethodExpression(faceletContext, null, new Class[]{Object.class});
            }
            if (postProcessor != null) {
                postProcessorME = postProcessor.getMethodExpression(faceletContext, null, new Class[]{Object.class});
            }
            if (repeat != null) {
                repeatVE = repeat.getValueExpression(faceletContext, Object.class);
            }

            ActionSource actionSource = (ActionSource) parent;
            ExtDataExporter dataExporter = new ExtDataExporter(targetVE, typeVE, fileNameVE, pageOnlyVE, selectionOnlyVE, encodingVE, preProcessorME, postProcessorME);
            dataExporter.setRepeat(repeatVE);
            actionSource.addActionListener(dataExporter);
        }
    }
}
