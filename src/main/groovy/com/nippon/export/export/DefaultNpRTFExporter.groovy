package com.nippon.export.export
import grails.plugins.export.exporter.DefaultRTFExporter
import grails.plugins.export.exporter.ExportingException

public class DefaultNpRTFExporter extends DefaultRTFExporter {

    @Override
    protected void exportData(OutputStream outputStream, List data, List fields) throws ExportingException {
//        super.exportData(outputStream, data, fields)
    }
}