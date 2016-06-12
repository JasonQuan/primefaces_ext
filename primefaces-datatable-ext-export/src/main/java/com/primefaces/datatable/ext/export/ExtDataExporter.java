package com.primefaces.datatable.ext.export;

import javax.el.ELContext;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.faces.component.StateHolder;
import javax.faces.component.UIComponent;
import javax.faces.event.ActionListener;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.export.Exporter;
import org.primefaces.expression.SearchExpressionFacade;

public class ExtDataExporter implements ActionListener, StateHolder {

    private ValueExpression target;

    private ValueExpression type;

    private ValueExpression fileName;

    private ValueExpression encoding;

    private ValueExpression pageOnly;

    private ValueExpression selectionOnly;

    private MethodExpression preProcessor;

    private MethodExpression postProcessor;

    private ValueExpression repeat;

    public ExtDataExporter() {
    }

    public ExtDataExporter(ValueExpression target, ValueExpression type, ValueExpression fileName, ValueExpression pageOnly, ValueExpression selectionOnly, ValueExpression encoding, MethodExpression preProcessor, MethodExpression postProcessor) {
        super();
        this.target = target;
        this.type = type;
        this.fileName = fileName;
        this.pageOnly = pageOnly;
        this.selectionOnly = selectionOnly;
        this.preProcessor = preProcessor;
        this.postProcessor = postProcessor;
        this.encoding = encoding;
    }

    @Override
    public void processAction(ActionEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        ELContext elContext = context.getELContext();

        String tables = (String) target.getValue(elContext);
        String exportAs = (String) type.getValue(elContext);
        String outputFileName = (String) fileName.getValue(elContext);

        String encodingType = "UTF-8";
        if (encoding != null) {
            encodingType = (String) encoding.getValue(elContext);
        }

        boolean repeating = false;
        if (repeat != null) {
            repeating = repeat.isLiteralText() ? Boolean.valueOf(repeat.getValue(context.getELContext()).toString()) : (Boolean) repeat.getValue(context.getELContext());
        }

        boolean isPageOnly = false;
        if (pageOnly != null) {
            isPageOnly = pageOnly.isLiteralText() ? Boolean.valueOf(pageOnly.getValue(context.getELContext()).toString()) : (Boolean) pageOnly.getValue(context.getELContext());
        }

        boolean isSelectionOnly = false;
        if (selectionOnly != null) {
            isSelectionOnly = selectionOnly.isLiteralText() ? Boolean.valueOf(selectionOnly.getValue(context.getELContext()).toString()) : (Boolean) selectionOnly.getValue(context.getELContext());
        }

        try {
            Exporter exporter = ExporterFactoryProvider.getExporterFactory(context).getExporterForType(exportAs);
            if (!repeating) {
                List components = SearchExpressionFacade.resolveComponents(context, event.getComponent(), tables);

                if (components.size() > 1) {
                    exporter.export(context, outputFileName, (List<DataTable>) components, isPageOnly, isSelectionOnly, encodingType, preProcessor, postProcessor);
                } else {
                    UIComponent component = (UIComponent) components.get(0);
                    if (!(component instanceof DataTable)) {
                        throw new FacesException("Unsupported datasource target:\"" + component.getClass().getName() + "\", exporter must target a PrimeFaces DataTable.");
                    }

                    DataTable table = (DataTable) component;
                    exporter.export(context, table, outputFileName, isPageOnly, isSelectionOnly, encodingType, preProcessor, postProcessor);
                }
            } else {
                String[] clientIds = tables.split("\\s+|,");
                exporter.export(context, Arrays.asList(clientIds), outputFileName, isPageOnly, isSelectionOnly, encodingType, preProcessor, postProcessor);
            }

            context.responseComplete();
        } catch (IOException e) {
            throw new FacesException(e);
        }
    }

    public void setRepeat(ValueExpression ve) {
        this.repeat = ve;
    }

    @Override
    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;

        target = (ValueExpression) values[0];
        type = (ValueExpression) values[1];
        fileName = (ValueExpression) values[2];
        pageOnly = (ValueExpression) values[3];
        selectionOnly = (ValueExpression) values[4];
        preProcessor = (MethodExpression) values[5];
        postProcessor = (MethodExpression) values[6];
        encoding = (ValueExpression) values[7];
        repeat = (ValueExpression) values[8];
    }

    @Override
    public Object saveState(FacesContext context) {
        Object values[] = new Object[9];

        values[0] = target;
        values[1] = type;
        values[2] = fileName;
        values[3] = pageOnly;
        values[4] = selectionOnly;
        values[5] = preProcessor;
        values[6] = postProcessor;
        values[7] = encoding;
        values[8] = repeat;

        return ((Object[]) values);
    }

    @Override
    public boolean isTransient() {
        return false;
    }

    @Override
    public void setTransient(boolean value) {
    }
}
