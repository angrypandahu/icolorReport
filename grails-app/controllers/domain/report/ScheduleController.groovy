package domain.report

import com.domain.report.Schedule
import grails.transaction.Transactional
import org.springframework.http.HttpStatus

@Transactional(readOnly = true)
class ScheduleController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]
    static dayOfWeekList = [1, 2, 3, 4, 5];

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Schedule.list(params), model: [scheduleCount: Schedule.count()]
    }

    def show(Schedule schedule) {
        respond schedule
    }

    def create() {
        def schedule = new Schedule(params)
        def weekly = schedule.getWeekly()
        if (weekly) {
            def schedules = weekly.getSchedules();
            if (schedules) {
                def dayOfWeeks = schedules.dayOfWeek;
                for (int i : dayOfWeekList) {
                    if (!dayOfWeeks.contains(i)) {
                        schedule.setDayOfWeek(i);
                        break
                    }
                }
            }


        } else {
            return
        }
        respond schedule
    }

    @Transactional
    def save(Schedule schedule) {
        if (schedule == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (schedule.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond schedule.errors, view: 'create'
            return
        }

        schedule.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'schedule.label', default: 'Schedule'), schedule.id])
                redirect schedule
            }
            '*' { respond schedule, [status: HttpStatus.CREATED] }
        }
    }

    def edit(Schedule schedule) {
        respond schedule
    }

    @Transactional
    def update(Schedule schedule) {
        if (schedule == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (schedule.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond schedule.errors, view: 'edit'
            return
        }

        schedule.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'schedule.label', default: 'Schedule'), schedule.id])
                redirect schedule
            }
            '*' { respond schedule, [status: HttpStatus.OK] }
        }
    }

    @Transactional
    def delete(Schedule schedule) {

        if (schedule == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        schedule.delete flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'schedule.label', default: 'Schedule'), schedule.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: HttpStatus.NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'schedule.label', default: 'Schedule'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: HttpStatus.NOT_FOUND }
        }
    }
}
