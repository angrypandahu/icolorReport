package com.nippon.export.export

import grails.core.GrailsApplication
import grails.plugins.export.exporter.Exporter
import grails.plugins.export.exporter.ExportingException

class  NpExportService{

    boolean transactional = false

    def npExporterFactory

    GrailsApplication grailsApplication

    public void export(String type, OutputStream outputStream,Object domain, List objects, List fields, Map labels, Map formatters, Map parameters) throws ExportingException {
        Exporter exporter = npExporterFactory.createExporter(type,domain, fields, labels, formatters, parameters)
        exporter.export(outputStream, objects)
    }
    public void export(String type, OutputStream outputStream, List objects, List fields, Map labels, Map formatters, Map parameters) throws ExportingException {
        Exporter exporter = npExporterFactory.createExporter(type, fields, labels, formatters, parameters)
        exporter.export(outputStream, objects)
    }

}
