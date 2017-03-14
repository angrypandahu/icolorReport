package domain.report

import com.domain.auth.Role
import com.domain.auth.User
import com.domain.report.Weekly
import com.nippon.export.export.ReportUtils
import grails.core.GrailsApplication
import grails.plugin.springsecurity.annotation.Secured
import grails.transaction.Transactional
import org.grails.plugins.filterpane.FilterPaneUtils
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.springframework.http.HttpStatus
import org.springframework.web.context.request.RequestContextHolder

@Transactional(readOnly = true)
class WeeklyController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]
    def grailsResourceLocator
    def reportService;
    GrailsApplication grailsApplication

    private authReport() {
        User user = getAuthenticatedUser() as User;
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

    }

    def isRetJson() {
        def contentType = request.getHeader("content-type");
        def jsonRequest = grailsApplication.config.grails.mime.types.json
        return jsonRequest.contains(contentType)
    }

    def filterPaneService
    def filter = {
        authReport()
        if (!params.max) params.max = 10
        def user = getAuthenticatedUser();
        render(view: 'index',
                model: [weeklyList  : filterPaneService.filter(params, Weekly.class),
                        weeklyCount : filterPaneService.count(params, Weekly.class),
                        user        : user,
                        filterParams: FilterPaneUtils.extractFilterParams(params),
                        params      : params])

    }

    def index() {
        filter()
    }

    def show(Weekly weekly) {
        def size = weekly.schedules?.size();
        def hasAddScheduleButton = (size != 5)
        respond weekly, model: [hasAddScheduleButton: hasAddScheduleButton]
    }

    @Secured(value = ["hasRole('ROLE_ADMIN')"], httpMethod = 'GET')
    createGroupWeekly() {
        GrailsWebRequest webRequest = (GrailsWebRequest) RequestContextHolder.currentRequestAttributes()
        webRequest.setRenderView(false)
        authReport()
        params.max = 100
        response.contentType = grailsApplication.config.grails.mime.types[params.f]

        params.f = 'doc'
        params.extension = 'docx'
        def weeklyList = filterPaneService.filter(params, Weekly.class) as List<Weekly>;
        def weeklyFormatName = ReportUtils.getWeeklyFormatName(weeklyList.get(0));
        def encodeFile = URLEncoder.encode("开发工作周报-${weeklyFormatName}.${params.extension}", "UTF-8")
        response.setHeader("Content-disposition", "attachment; filename=${encodeFile}")
        def templateFile = grailsResourceLocator.findResourceForURI("classpath:${ReportUtils.REPORT_PATH}${ReportUtils.TEMPLATE_WEEKLY_ICOLOR}").getFile();
        ReportUtils.writeWeeklyToDoc(weeklyList, response.outputStream, templateFile);


    }

    def download(Weekly weekly) {
        def format = ReportUtils.getWeeklyFormatName(weekly);
        def attachName = "${weekly.user.displayName}-${format}-周报.docx"
        def encodeFile = URLEncoder.encode(attachName, "UTF-8")
        response.contentType = grailsApplication.config.grails.mime.types['doc']
        response.setHeader("Content-disposition", "attachment; filename=${encodeFile}")
        def templateFile = grailsResourceLocator.findResourceForURI("classpath:${ReportUtils.REPORT_PATH}${ReportUtils.TEMPLATE_WEEKLY_ICOLOR}").getFile();
        List<Weekly> weeklyList = new ArrayList<>();
        weeklyList.add(weekly);
        ReportUtils.writeWeeklyToDoc(weeklyList, response.outputStream, templateFile);
    }

    def create() {
        User user = getAuthenticatedUser() as User;
        log.info("############" + user.getUsername())
        Weekly weekly = new Weekly(params)
        def thisWeekReports = reportService.getThisWeekReports(user, weekly);
        def content = "";
        def question = "";
        thisWeekReports?.each { report ->
            def getContent = report.getContent()
            if (getContent)
                content += getContent + "\r\n"
            def getQuestion = report.getQuestion()
            if (getQuestion)
                question += getQuestion + "\r\n"
        }
        weekly.setUser(user)
        weekly.setContent(ReportUtils.get255String(content));
        weekly.setQuestion(ReportUtils.get255String(question));
        respond weekly
    }

    @Transactional
    def save(Weekly weekly) {
        if (weekly == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (weekly.hasErrors()) {
            if (weekly.getId()) {
                transactionStatus.setRollbackOnly()
            }
            respond weekly.errors, view: 'create'
            return
        }

        weekly.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'weekly.label', default: 'Weekly'), weekly.id])
                redirect weekly
            }
            '*' { respond weekly, [status: HttpStatus.CREATED] }
        }
    }

    def edit(Weekly weekly) {
        respond weekly
    }

    @Transactional
    def update(Weekly weekly) {
        if (weekly == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (weekly.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond weekly.errors, view: 'edit'
            return
        }

        weekly.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'weekly.label', default: 'Weekly'), weekly.id])
                redirect weekly
            }
            '*' { respond weekly, [status: HttpStatus.OK] }
        }
    }

    @Transactional
    def delete(Weekly weekly) {

        if (weekly == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        weekly.delete flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'weekly.label', default: 'Weekly'), weekly.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: HttpStatus.NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'weekly.label', default: 'Weekly'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: HttpStatus.NOT_FOUND }
        }
    }
}
