package com.primefaces.ext.base.exporter;

import javax.faces.component.UIComponent;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitResult;
import javax.faces.context.FacesContext;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.export.ExcelExporter;

/**
 *
 * @author 041863
 */
public class XSSHExcelExportVisitCallback implements VisitCallback {

    private XSSHExcel exporter;
    private boolean pageOnly;
    private boolean selectionOnly;
    private Workbook workbook;

    public XSSHExcelExportVisitCallback(XSSHExcel exporter, Workbook workbook, boolean pageOnly, boolean selectionOnly) {
        this.exporter = exporter;
        this.pageOnly = pageOnly;
        this.selectionOnly = selectionOnly;
        this.workbook = workbook;
    }

    @Override
    public VisitResult visit(VisitContext context, UIComponent target) {
        DataTable dt = (DataTable) target;
        FacesContext facesContext = context.getFacesContext();
        String sheetName = exporter.getSheetName(facesContext, dt);
        if (sheetName == null) {
            sheetName = dt.getClientId().replaceAll(":", "_");
        }

        Sheet sheet = workbook.createSheet(sheetName);
        exporter.exportTable(facesContext, dt, sheet, pageOnly, selectionOnly);
        return VisitResult.ACCEPT;
    }

}
