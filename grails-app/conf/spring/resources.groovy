import com.nippon.export.export.DefaultExcelReportExporter
import com.nippon.export.export.DefaultNpExporterFactory
import com.nippon.export.export.DefaultRTFReportExporter
import com.nippon.export.export.NpExportService

beans = {
    npExportService(NpExportService) {
        npExporterFactory = ref('npExporterFactory')
    }
    npExporterFactory(DefaultNpExporterFactory)

    rtfReportExporter(DefaultRTFReportExporter){
        grailsResourceLocator = ref('grailsResourceLocator')
    }

    excelReportExporter(DefaultExcelReportExporter){
        grailsResourceLocator = ref('grailsResourceLocator')
    }

}
