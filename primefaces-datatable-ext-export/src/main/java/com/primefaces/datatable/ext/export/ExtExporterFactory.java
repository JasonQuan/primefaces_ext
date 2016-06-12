package com.primefaces.datatable.ext.export;

import org.primefaces.component.export.Exporter;

public interface ExtExporterFactory {

    Exporter getExporterForType(String type);

}
