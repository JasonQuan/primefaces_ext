package com.primefaces.datatable.ext.export;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;

import javax.el.MethodExpression;
import javax.faces.component.UIComponent;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.component.api.UIColumn;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.export.Exporter;
import org.primefaces.util.Constants;

/**
 *
 * @author 041863
 */
public class XSSHExcel extends Exporter {

    @Override
    public void export(FacesContext context, DataTable table, String filename, boolean pageOnly, boolean selectionOnly, String encodingType, MethodExpression preProcessor, MethodExpression postProcessor) throws IOException {
        Workbook wb = createWorkBook();
        String sheetName = getSheetName(context, table);
        if (sheetName == null) {
            sheetName = table.getId();
        }

        Sheet sheet = wb.createSheet(sheetName);

        if (preProcessor != null) {
            preProcessor.invoke(context.getELContext(), new Object[]{wb});
        }

        exportTable(context, table, sheet, pageOnly, selectionOnly);

        if (postProcessor != null) {
            postProcessor.invoke(context.getELContext(), new Object[]{wb});
        }

        writeExcelToResponse(context.getExternalContext(), wb, filename);
    }

    @Override
    public void export(FacesContext context, String filename, List<DataTable> tables, boolean pageOnly, boolean selectionOnly, String encodingType, MethodExpression preProcessor, MethodExpression postProcessor) throws IOException {
        Workbook wb = createWorkBook();

        if (preProcessor != null) {
            preProcessor.invoke(context.getELContext(), new Object[]{wb});
        }

        for (DataTable table : tables) {
            String sheetName = getSheetName(context, table);
            if (sheetName == null) {
                sheetName = table.getId();
            }

            Sheet sheet = wb.createSheet(sheetName);
            exportTable(context, table, sheet, pageOnly, selectionOnly);
        }

        if (postProcessor != null) {
            postProcessor.invoke(context.getELContext(), new Object[]{wb});
        }

        writeExcelToResponse(context.getExternalContext(), wb, filename);
    }

    @Override
    public void export(FacesContext context, List<String> clientIds, String filename, boolean pageOnly, boolean selectionOnly, String encodingType, MethodExpression preProcessor, MethodExpression postProcessor) throws IOException {
        Workbook wb = createWorkBook();

        if (preProcessor != null) {
            preProcessor.invoke(context.getELContext(), new Object[]{wb});
        }

        VisitContext visitContext = VisitContext.createVisitContext(context, clientIds, null);
        VisitCallback visitCallback = new XSSHExcelExportVisitCallback(this, wb, pageOnly, selectionOnly);
        context.getViewRoot().visitTree(visitContext, visitCallback);

        if (postProcessor != null) {
            postProcessor.invoke(context.getELContext(), new Object[]{wb});
        }

        writeExcelToResponse(context.getExternalContext(), wb, filename);
    }

    @Override
    protected void exportCells(DataTable table, Object document) {
        Sheet sheet = (Sheet) document;
        int sheetRowIndex = sheet.getLastRowNum() + 1;
        Row row = sheet.createRow(sheetRowIndex);

        for (UIColumn col : table.getColumns()) {
//            if (col instanceof DynamicColumn) {
//                ((DynamicColumn) col).applyStatelessModel();
//            }
            if (col.isRendered() && col.isExportable()) {
                if (col instanceof ExtColumn) {
                    addColumnValue(row, col.getChildren(), ((ExtColumn) col).getExportSort());
                } else {
                    addColumnValue(row, col.getChildren(), 0);
                }
            }
        }
    }

    protected void addColumnFacets(DataTable table, Sheet sheet, Exporter.ColumnType columnType) {
        int sheetRowIndex = columnType.equals(Exporter.ColumnType.HEADER) ? 0 : (sheet.getLastRowNum() + 1);
        Row rowHeader = sheet.createRow(sheetRowIndex);

        for (UIColumn col : table.getColumns()) {
//            if (col instanceof DynamicColumn) {
//                ((DynamicColumn) col).applyStatelessModel();
//            }
            if (col.isRendered() && col.isExportable()) {
                UIComponent facet = col.getFacet(columnType.facet());
                if (facet != null) {
                    addColumnFacetValue(rowHeader, facet, ((ExtColumn) col));
                } else {
                    String textValue;
//                    switch (columnType) {
//                        case HEADER:
                    textValue = col.getHeaderText();
//                            break;

//                        case FOOTER:
//                            textValue = col.getFooterText();
//                            break;
//                        default:
//                            textValue = "";
//                            break;
//                    }
                    if (col instanceof ExtColumn) {
                        addColumnValue(rowHeader, textValue, ((ExtColumn) col).getExportSort());
                    } else {
                        addColumnValue(rowHeader, textValue, 0);
                    }
                }
            }
        }
    }

    protected void addColumnFacetValue(Row row, UIComponent component, ExtColumn co) {
        if (co.getExportHeader() != null && co.getExportHeader().length() > 0) {
            addColumnValue(row, co.getExportHeader(), co.getExportSort());
        } else {
            String value = component == null ? "" : exportValue(FacesContext.getCurrentInstance(), component);
            //TODO: replace all html code
            addColumnValue(row, value.replace("<br/>", ""), co.getExportSort());
        }
    }

    protected void addColumnValue(Row row, String value, int cellIndex) {
        cellIndex = (cellIndex == 0) ? (row.getLastCellNum() == -1 ? 0 : row.getLastCellNum()) : (cellIndex - 1);
        Cell cell = row.createCell(cellIndex);

        cell.setCellValue(createRichTextString(value));
    }

    protected void addColumnValue(Row row, List<UIComponent> components, int cellIndex) {
        cellIndex = (cellIndex == 0) ? (row.getLastCellNum() == -1 ? 0 : row.getLastCellNum()) : cellIndex - 1;
        Cell cell = row.createCell(cellIndex);
        StringBuilder builder = new StringBuilder();
        FacesContext context = FacesContext.getCurrentInstance();

        for (UIComponent component : components) {
            if (component.isRendered()) {
                String value = exportValue(context, component);

                if (value != null) {
                    builder.append(value);
                }
            }
        }

        cell.setCellValue(createRichTextString(builder.toString()));
    }

    protected RichTextString createRichTextString(String value) {
        return new XSSFRichTextString(value);
    }

    protected Workbook createWorkBook() {
        return new XSSFWorkbook();
    }

    protected void writeExcelToResponse(ExternalContext externalContext, Workbook generatedExcel, String filename) throws IOException {
        externalContext.setResponseContentType("application/vnd.ms-excel");
        externalContext.setResponseHeader("Expires", "0");
        externalContext.setResponseHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        externalContext.setResponseHeader("Pragma", "public");
        externalContext.setResponseHeader("Content-disposition", getContentDisposition(filename));
        externalContext.addResponseCookie(Constants.DOWNLOAD_COOKIE, "true", Collections.<String, Object>emptyMap());

        OutputStream out = externalContext.getResponseOutputStream();
        generatedExcel.write(out);
        externalContext.responseFlushBuffer();
    }

    protected String getContentDisposition(String filename) throws UnsupportedEncodingException {
        return "attachment;filename=" + URLEncoder.encode(filename, "UTF-8") + ".xlsx";
    }

    public void exportTable(FacesContext context, DataTable table, Sheet sheet, boolean pageOnly, boolean selectionOnly) {
        addColumnFacets(table, sheet, Exporter.ColumnType.HEADER);

        if (pageOnly) {
            exportPageOnly(context, table, sheet);
        } else if (selectionOnly) {
            exportSelectionOnly(context, table, sheet);
        } else {
            exportAll(context, table, sheet);
        }

//        if (table.hasFooterColumn()) {
//            addColumnFacets(table, sheet, Exporter.ColumnType.FOOTER);
//        }
        table.setRowIndex(-1);
    }
}
