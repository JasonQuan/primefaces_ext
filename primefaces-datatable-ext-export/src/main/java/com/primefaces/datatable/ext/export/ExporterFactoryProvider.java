package com.primefaces.datatable.ext.export;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;

import java.util.ServiceLoader;
import org.primefaces.component.export.CSVExporter;
import org.primefaces.component.export.Exporter;
import org.primefaces.component.export.PDFExporter;
import org.primefaces.component.export.XMLExporter;

public class ExporterFactoryProvider {

    private static final String KEY = ExporterFactoryProvider.class.getName();

    public static ExtExporterFactory getExporterFactory(FacesContext context) {

        ExtExporterFactory factory = (ExtExporterFactory) context.getExternalContext().getApplicationMap().get(KEY);

        if (factory == null) {
            ServiceLoader<ExtExporterFactory> loader = ServiceLoader.load(ExtExporterFactory.class);
            for (ExtExporterFactory currentFactory : loader) {
                factory = currentFactory;
                break;
            }

            if (factory == null) {
                factory = new DefaultExporterFactory();
            }

            context.getExternalContext().getApplicationMap().put(KEY, factory);
        }

        return factory;
    }
}

class DefaultExporterFactory implements ExtExporterFactory {

    static public enum ExporterType {
        XLS,
        PDF,
        CSV,
        XML,
        XLSX
    }

    @Override
    public Exporter getExporterForType(String type) {

        Exporter exporter = null;

        try {
            ExporterType exporterType = ExporterType.valueOf(type.toUpperCase());

            //TODO: other type
            switch (exporterType) {
                case XLS:
                    exporter = new ExtExcelExporter();
                    break;

                case PDF:
                    exporter = new PDFExporter();
                    break;

                case CSV:
                    exporter = new CSVExporter();
                    break;

                case XML:
                    exporter = new XMLExporter();
                    break;

                case XLSX:
                    exporter = new ExtExcelExporter();
                    break;
                default:
                    exporter = new ExtExcelExporter();
            }
        } catch (IllegalArgumentException e) {
            throw new FacesException(e);
        }

        return exporter;
    }

}
