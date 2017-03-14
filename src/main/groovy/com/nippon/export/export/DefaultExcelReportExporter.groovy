package com.nippon.export.export

import grails.plugins.export.exporter.AbstractExporter
import grails.plugins.export.exporter.ExportingException

/**
 * @author Andreas Schmitt
 *
 */
public class DefaultExcelReportExporter extends AbstractExporter {

    def grailsResourceLocator

    protected void exportData(OutputStream outputStream, List data, List fields) throws ExportingException {

        try {
            def templateFile = grailsResourceLocator.findResourceForURI("classpath:${ReportUtils.REPORT_PATH}${ReportUtils.TEMPLATE_WAIBAO}").getFile();
            def stream = new FileInputStream(templateFile);
            ReportUtils.writeReportToExcel(data, outputStream, stream);
            outputStream?.flush();
            outputStream?.close();

        }
        catch (Exception e) {
            throw new ExportingException("Error during export", e)
        }
    }


}
