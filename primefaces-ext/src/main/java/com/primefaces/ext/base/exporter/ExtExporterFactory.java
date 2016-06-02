package com.primefaces.ext.base.exporter;

import org.primefaces.component.export.Exporter;

public interface ExtExporterFactory {

    Exporter getExporterForType(String type);

}
