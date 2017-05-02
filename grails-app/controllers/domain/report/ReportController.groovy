package domain.report

import com.domain.auth.Role
import com.domain.auth.User
import com.domain.report.Report
import com.nippon.export.export.ReportUtils
import grails.converters.JSON
import grails.core.GrailsApplication
import grails.plugin.springsecurity.annotation.Secured
import grails.plugins.mail.MailService
import grails.transaction.Transactional
import org.apache.commons.lang.StringUtils
import org.grails.plugins.filterpane.FilterPaneUtils
import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.joda.time.LocalDate
import org.springframework.http.HttpStatus
import org.springframework.web.context.request.RequestContextHolder

@Transactional(readOnly = true)
class ReportController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]
    MailService mailService

    def exportService
    def npExportService
    def grailsResourceLocator
    def reportService

    GrailsApplication grailsApplication

    private def authReport() {
        User user = getAuthenticatedUser();
        def group = user.getReportGroup()
        def authorities = user.getAuthorities();
        def roleAdmin = Role.findByAuthority("ROLE_ADMIN");
        if (authorities.contains(roleAdmin)) {
            params.'filter.user.reportGroup.name' = group.name
            params.'filter.op.user.reportGroup.name' = "Equal"
        } else {
            params.'filter.user.username' = user.username
            params.'filter.op.user.username' = "Equal"
        }

        if (!params.sort) {
            params.sort = "reportDate"
            params.order = "desc"
        }

//        if (!params.'filter.op.reportDate') {
//            params.'filter.op.reportDate' = "Equal"
//            def instance = Calendar.getInstance();
//            def year = instance.get(Calendar.YEAR)
//            def month = instance.get(Calendar.MONTH) + 1
//            def day = instance.get(Calendar.DAY_OF_MONTH)
//            params.'filter.reportDate_day' = "${day}"
//            params.'filter.reportDate_month' = "${month}"
//            params.'filter.reportDate_year' = "${year}"
//            params.'filter.reportDate' = "struct"
//        }
    }

    def isRetJson() {
        def contentType = request.getHeader("content-type");
        def jsonRequest = grailsApplication.config.grails.mime.types.json
        return jsonRequest.contains(contentType)
    }

    def filterPane7Service
    def filter = {
        authReport()


        if (!params.max) params.max = 10
        if (params?.f && params.f != "html") {
            params.max = 100
            response.contentType = grailsApplication.config.grails.mime.types[params.f]
            response.setHeader("Content-disposition", "attachment; filename=report.${params.extension}")
            List fields = ["user.displayName", "reportDate", "workHours", "content", "question", "share"]
            Map labels = ["user.displayName": "User", "reportDate": "ReportDate", "workHours": "WorkHours", "content": "Content", "question": "Question", "share": "Share"]
            Map formatters = [:]
            Map parameters = [content: "Report", "column.widths": [0.1, 0.1, 0.1, 0.3, 0.2, 0.2]]
            exportService.export(params.f, response.outputStream, filterPane7Service.filter(params, Report.class), fields, labels, formatters, parameters)
            return
        }
        User user = getAuthenticatedUser();
        if (isRetJson()) {
            def retJson = new JSONObject();
            def filter = filterPane7Service.filter(params, Report.class)
            def array = new JSONArray();
            for (Report report : filter) {
                array.add(ReportUtils.reportToJson(report))
            }
            retJson.put("reportList", array)
            retJson.put("reportCount", filterPane7Service.count(params, Report.class))
            render(retJson)
        } else {
            render(view: 'index',
                    model: [reportList  : filterPane7Service.filter(params, Report.class),
                            reportCount : filterPane7Service.count(params, Report.class),
                            user        : user,
                            filterParams: FilterPaneUtils.extractFilterParams(params),
                            params      : params])
        }

    }

    def index() {
        filter()
    }

    def show() {
        def report = reportService.getReport(params.id);
        User user = getAuthenticatedUser() as User;
        report.getUser();
        if (isRetJson()) {
            render(ReportUtils.reportToJson(report))
        } else {
            respond report, model: [user: user]
        }

    }

    def create() {
        User user = getAuthenticatedUser() as User;
        log.debug("############" + user.getUsername())
        Report report = new Report(params)
        report.setUser(user)
        report.setWorkHours(8)
        report.setReportDate(new LocalDate())
        if (isRetJson()) {
            render(ReportUtils.reportToJson(report))
        } else {
            respond report, model: [user: user]
        }
    }

    def notFiltered() {
        flash.message = "Please search"
        redirect action: "index", method: "GET"
    }

    boolean isNotFiltered() {
        return !params.'filter.op.reportDate'
    }

    @Secured(value = ["hasRole('ROLE_ADMIN')"], httpMethod = 'GET')
    createGroupReport() {
        if (isNotFiltered()) {
            notFiltered()
            return
        }
        GrailsWebRequest webRequest = (GrailsWebRequest) RequestContextHolder.currentRequestAttributes()
        webRequest.setRenderView(false)
        authReport()
        params.max = 100
        response.contentType = grailsApplication.config.grails.mime.types[params.f]
        def day = params.'filter.reportDate_day';
        def year = params.'filter.reportDate_year';
        def month = params.'filter.reportDate_month';
        month = StringUtils.leftPad(month, 2, "0");
        day = StringUtils.leftPad(day, 2, "0");
        List fields = ["user.displayName", "reportDate", "content", "question", "share"]
        Map labels = ["user.displayName": "User", "reportDate": "ReportDate", "content": "Content", "question": "Question", "share": "Share"]
        Map formatters = [:]
        Map parameters = [content: "Report", "column.widths": [0.1, 0.1, 0.4, 0.2, 0.2]]
        def groupName = params.'filter.user.reportGroup.name';
        if (groupName == "GROUP_ICOLOR") {
            params.f = 'doc'
            params.extension = 'docx'
            def encodeFile = URLEncoder.encode("开发工作日报-${year}${month}${day}.${params.extension}", "UTF-8")
            response.setHeader("Content-disposition", "attachment; filename=${encodeFile}")
            npExportService.export('rtf', response.outputStream, Report, filterPane7Service.filter(params, Report.class) as List, fields, labels, formatters, parameters)
        } else if (groupName == "GROUP_WAIBAO") {
            params.f = 'excel'
            params.extension = 'xlsx'
            def filterOpReportDate = params.'filter.op.reportDate';
            def excelName = ReportUtils.getWaiBaoExcelName(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day), filterOpReportDate);
            def encodeFile = URLEncoder.encode("${excelName}.${params.extension}", "UTF-8")
            response.setHeader("Content-disposition", "attachment; filename=${encodeFile}")
            npExportService.export('excel', response.outputStream, Report, filterPane7Service.filter(params, Report.class) as List, fields, labels, formatters, parameters)
        }

    }

    @Secured(value = ["hasRole('ROLE_MENGTUO')"], httpMethod = 'GET')
    createMonthReport() {
        if (isNotFiltered()) {
            notFiltered()
            return
        }
        GrailsWebRequest webRequest = (GrailsWebRequest) RequestContextHolder.currentRequestAttributes()
        webRequest.setRenderView(false)
        authReport()
        params.sort = "reportDate"
        params.order = "asc"
        params.max = 100
        response.contentType = grailsApplication.config.grails.mime.types[params.f]
        def year = params.'filter.reportDate_year';
        def month = params.'filter.reportDate_month';
        if (Integer.parseInt(month) < 10) {
            month = "0" + month;
        }
        params.f = 'excel'
        params.extension = 'xlsx'
        User user = getAuthenticatedUser() as User;
        def excelName = ReportUtils.getMonthMengTuoExcelName(year, month, user.getDisplayName());
        def encodeFile = URLEncoder.encode("${excelName}.${params.extension}", "UTF-8")
        response.setHeader("Content-disposition", "attachment; filename=${encodeFile}")

        def templateFile = grailsResourceLocator.findResourceForURI("classpath:${ReportUtils.REPORT_PATH}${ReportUtils.TEMPLATE_MONTH_MENGTUO}").getFile();
        def stream = new FileInputStream(templateFile);
        ReportUtils.writeMengTuoMonthReportToExcel(user, filterPane7Service.filter(params, Report.class) as List<Report>, response.outputStream, stream);
    }

    @Transactional
    def saveToJson(Report report) {
        if (report.hasErrors()) {
            if (report.getId()) {
                transactionStatus.setRollbackOnly()
            }
            render(report.errors as JSON);
            return
        }
        report.save flush: true
        render(ReportUtils.reportToJson(report))
    }

    @Transactional
    def save(Report report) {
        User user = getAuthenticatedUser() as User;
        if (isRetJson()) {
            saveToJson(report);
        } else {
            if (report == null) {
                transactionStatus.setRollbackOnly()
                notFound()
                return
            }
            if (report.hasErrors()) {
                if (report.getId()) {
                    transactionStatus.setRollbackOnly()
                }
                respond report.errors, view: 'create', model: [user: user]
                return
            }

            report.setUser(user);
            report.save flush: true

            request.withFormat {
                form multipartForm {
                    flash.message = message(code: 'default.created.message', args: [message(code: 'report.label', default: 'Report'), report.id])
                    redirect report
                }
                '*' { respond report, [status: HttpStatus.CREATED] }
            }
        }

    }

    def edit() {
        def report = reportService.getReport(params.id);
        User user = getAuthenticatedUser() as User;
        if (isRetJson()) {
            render(ReportUtils.reportToJson(report))
        } else {
            respond report, model: [user: user]
        }
    }

//    def sendMail(Report report) {
//        def mailText = "开发内容:" + report.content + "</br>" +
//                "遇到问题:" + report.question + "</br>" +
//                "分享内容:" + report.share + "</br>"
//        mailService.sendMail {
//            to report.user.email
//            subject "开发日报-" + report.reportDate
//            html mailText
//        }
//        request.withFormat {
//            form multipartForm {
//                flash.message = message(code: 'default.updated.message', args: [message(code: 'report.label', default: 'Report'), report.id])
//                redirect report
//            }
//            '*' { respond report, [status: OK] }
//        }
//    }

    /*
         response.contentType = grailsApplication.config.grails.mime.types['rtf']
        def format = dateFormat.format(report.reportDate.toDate());
        def fileName = "${report.user.displayName}-开发工作日报-${format}.doc"
        def fileName8859 = new String(fileName.getBytes("gb2312"), "iso8859-1")
        response.setHeader("Content-disposition", "attachment; filename=${fileName8859}")
        List fields = ["user.username", "reportDate", "content", "question", "share"]
        Map labels = ["user.username": "User", "reportDate": "ReportDate", "content": "Content", "question": "Question", "share": "Share"]

        def upperCase = { domain, value ->
            return value.toUpperCase()
        }
        Map formatters = [question: upperCase]
        Map parameters = [content: "Report", "column.widths": [0.1, 0.1, 0.4, 0.2, 0.2]]

        npExportService.export('rtf', response.outputStream, Report, Report.findAllById(report.id), fields, labels, formatters, parameters)





    */

    def changeLine(def content) {
        def retContent = ""
        def defaultIfEmpty = StringUtils.defaultIfEmpty(content, "").split("\r\n")
        defaultIfEmpty.each {
            retContent += "\t" + it + (char) 11;
        }
        return retContent
    }

    def sendMail(Report report) {
        report.setIsSend(true)
        report.save flush: true
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.send.message', args: [message(code: 'report.label', default: 'Report'), report.id])
                redirect report
            }
            '*' { respond report, [status: HttpStatus.OK] }
        }

    }

    def send() {
        def report = Report.findById(params.id);
        report.setIsSend(true)
        report.save flush: true
        render(ReportUtils.reportToJson(report))
    }

    def download(Report report) {
        def format = ReportUtils.dateFormat.format(report.reportDate.toDate());
        def attachName = "${report.user.displayName}-${format}-日报.docx"
        def encodeFile = URLEncoder.encode(attachName, "UTF-8")
        response.contentType = grailsApplication.config.grails.mime.types['doc']
        response.setHeader("Content-disposition", "attachment; filename=${encodeFile}")
        def templateFile = grailsResourceLocator.findResourceForURI("classpath:${ReportUtils.REPORT_PATH}${ReportUtils.TEMPLATE_ICOLOR}").getFile();
        ReportUtils.writeReportToDoc(report, response.outputStream, templateFile);
    }

    @Transactional
    def update(Report report) {
        if (report == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (report.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond report.errors, view: 'edit'
            return
        }
        report.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'report.label', default: 'Report'), report.id])
                redirect report
            }
            '*' { respond report, [status: HttpStatus.OK] }
        }
    }

    @Transactional
    def delete(Report report) {

        if (report == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        report.delete flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'report.label', default: 'Report'), report.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: HttpStatus.NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'report.label', default: 'Report'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: HttpStatus.NOT_FOUND }
        }
    }

}
