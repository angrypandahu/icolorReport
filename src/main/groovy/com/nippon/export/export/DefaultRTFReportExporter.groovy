package com.nippon.export.export

import grails.plugins.export.exporter.AbstractExporter
import grails.plugins.export.exporter.ExportingException

/**
 * @author Andreas Schmitt
 *
 */
public class DefaultRTFReportExporter extends AbstractExporter {

    def grailsResourceLocator

    protected void exportData(OutputStream outputStream, List data, List fields) throws ExportingException {
        try {
            def templateFile = grailsResourceLocator.findResourceForURI("classpath:${ReportUtils.REPORT_PATH}${ReportUtils.TEMPLATE_ICOLOR}").getFile();
            ReportUtils.writeReportToDoc(data, outputStream, templateFile);
            outputStream?.flush();
            outputStream?.close();
        }
        catch (Exception e) {
            throw new ExportingException("Error during export", e)
        }
    }


}
