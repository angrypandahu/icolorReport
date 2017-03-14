package domain.auth

import com.domain.auth.ReportGroup
import grails.transaction.Transactional
import org.springframework.http.HttpStatus

@Transactional(readOnly = true)
class ReportGroupController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond ReportGroup.list(params), model:[reportGroupCount: ReportGroup.count()]
    }

    def show(ReportGroup reportGroup) {
        respond reportGroup
    }

    def create() {
        respond new ReportGroup(params)
    }

    @Transactional
    def save(ReportGroup reportGroup) {
        if (reportGroup == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (reportGroup.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond reportGroup.errors, view:'create'
            return
        }

        reportGroup.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'reportGroup.label', default: 'ReportGroup'), reportGroup.id])
                redirect reportGroup
            }
            '*' { respond reportGroup, [status: HttpStatus.CREATED] }
        }
    }

    def edit(ReportGroup reportGroup) {
        respond reportGroup
    }

    @Transactional
    def update(ReportGroup reportGroup) {
        if (reportGroup == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (reportGroup.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond reportGroup.errors, view:'edit'
            return
        }

        reportGroup.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'reportGroup.label', default: 'ReportGroup'), reportGroup.id])
                redirect reportGroup
            }
            '*'{ respond reportGroup, [status: HttpStatus.OK] }
        }
    }

    @Transactional
    def delete(ReportGroup reportGroup) {

        if (reportGroup == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        reportGroup.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'reportGroup.label', default: 'ReportGroup'), reportGroup.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: HttpStatus.NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'reportGroup.label', default: 'ReportGroup'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: HttpStatus.NOT_FOUND }
        }
    }
}
