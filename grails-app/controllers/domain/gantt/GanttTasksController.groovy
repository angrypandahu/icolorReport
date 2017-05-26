package domain.gantt

import com.domain.auth.Role
import com.domain.auth.User
import com.domain.gantt.GanttTasks
import com.nippon.NpStringUtils
import grails.transaction.Transactional
import org.apache.commons.lang.StringUtils
import org.grails.plugins.filterpane.FilterPaneUtils
import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject
import org.springframework.http.HttpStatus

@Transactional(readOnly = true)
class GanttTasksController {
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]
    def ganttTasksService
    def filterPane7Service
    def exportService

    def filter = {
        if (!params.max) params.max = 10
        authTask()

        if (params?.f && params.f != "html") {
            export();
        } else {
            render(view: 'index',
                    model: [ganttTasksList : filterPane7Service.filter(params, GanttTasks.class),
                            ganttTasksCount: filterPane7Service.count(params, GanttTasks.class),
                            filterParams   : FilterPaneUtils.extractFilterParams(params),
                            params         : params])
        }


    }

    def filterParams() {
        def progress = params.'filter.progress';
        if (progress) {
            params.put('filter.progress', NpStringUtils.toDouble(progress))
        }
    }

    def index() {
        filter()
    }

    def export() {
        params.max = 1000
        response.contentType = grailsApplication.config.grails.mime.types[params.f]
        response.setHeader("Content-disposition", "attachment; filename=Task.${params.extension}")
        List fields = ["text", "progress", "startDate", "endDate", "users.displayName"]
        Map labels = ["users.displayName": "User","progress":"Progress(%)"]
        def toPercentage = { domain, v ->
            return Math.round(v * 100) * 1.0;
        }
        def userFormat = { domain, v ->
            return StringUtils.join(v, "/");

        }
        Map formatters = [progress: toPercentage, "users.displayName": userFormat]
        Map parameters = [content: "GanttTasks", "column.widths": [0.3, 0.1, 0.2, 0.2, 0.2]]
        exportService.export(params.f, response.outputStream, filterPane7Service.filter(params, GanttTasks.class) as List, fields, labels, formatters, parameters)
    }

    private authTask() {
        def user = getAuthenticatedUser();
        def authorities = user.getAuthorities();
        def roleAdmin = Role.findByAuthority("ROLE_ADMIN");
        def roleManager = Role.findByAuthority("ROLE_MANAGER");

        def projects = GanttTasks.findAllByType('project', [sort: 'sorter', order: 'desc'])
        def filterOpRoot = params.'filter.op.root'
        if (projects && !filterOpRoot) {
            def ganttTasks = projects.get(0);
            params.'filter.root' = ganttTasks.text
            params.'filter.op.root' = "Equal"
            params.'filter.type' = 'task'
            params.'filter.op.type' = "Equal"
        }
        if (!authorities.contains(roleAdmin) && !authorities.contains(roleManager)) {
            params.'filter.users.username' = user.username
            params.'filter.op.users.username' = "Equal"
        }
        if (!params.sort) {
            params.sort = "sorter"
            params.order = "asc"
        }

    }

    def taskList() {
        def root = params.root ? Long.parseLong(params.root) : 0L
        def list = ganttTasksService.taskListByRoot(root);
        def tasks = new JSONObject();
        def data = new JSONArray();

        list.each { task ->
            data.add(ganttTasksService.taskToJson(task));
        }
        tasks.put("data", data);
        render tasks
    }


    def task() {
        if (!params.max) params.max = 1000
        authTask()
        def users = User.findAll();
        List<GanttTasks> list = filterPane7Service.filter(params, GanttTasks.class) as List
        Set<GanttTasks> set = new HashSet<>();
        def taskArray = new JSONArray();
        list?.each { task ->
            set.addAll(ganttTasksService.getParents(task));
        }
        def listOfTaskss = set.toList()
        Collections.sort(listOfTaskss, new Comparator<GanttTasks>() {
            @Override
            int compare(GanttTasks o1, GanttTasks o2) {
                return (Integer)(o1.sorter - o2.sorter)
            }
        })
        listOfTaskss.each { task ->
            taskArray.add(ganttTasksService.taskToJson(task));
        }

        def userArray = new JSONArray();
        users.each { user ->
            def jSONObject = new JSONObject();
            jSONObject.put("key", user.id);
            jSONObject.put("label", user.displayName);
            userArray.add(jSONObject)
        }
        def tasks = new JSONObject();
        tasks.put("data", taskArray);
        render(view: 'task',
                model: [ganttTasksList : tasks,
                        ganttTasksCount: filterPane7Service.count(params, GanttTasks.class),
                        filterParams   : FilterPaneUtils.extractFilterParams(params),
                        users          : userArray,
                        params         : params])

    }

    def show(GanttTasks ganttTasks) {
        respond ganttTasks
    }

    def ajaxShow(GanttTasks ganttTasks) {
        def json = ganttTasksService.taskToJson(ganttTasks)
        def textJson = NpStringUtils.ajaxJSONReturnTrue(json, "text");
        log.info(textJson.toString())
        render textJson
    }


    def removeUser() {
        def id = params.id;
        def userId = params.'user.id';
        def task = ganttTasksService.removeUser(id, userId)
        render view: '_editUsers', model: [task: task]
    }

    def create() {
        respond new GanttTasks(params)
    }

    def importTask() {
//        ganttTasksService.importTask('1','iColor买家中心积分页面(MB&PC)','陈纲',2,161)
//        ganttTasksService.importTask('1.1','iColor买家中心积分页面(MB&PC)-页面UI','陈纲',3,161)
//        ganttTasksService.importTask('1.2','iColor买家中心积分页面(MB&PC)-产品兑换功能','陈纲',4,161)
//        ganttTasksService.importTask('2','iColor买家中心积分页面-会员权益(MB&PC)','欧阳辉&刘玲',5,161)
//        ganttTasksService.importTask('2.1','iColor买家中心积分页面-会员权益(MB&PC)-页面UI','欧阳辉&刘玲',6,161)
//        ganttTasksService.importTask('2.2','iColor买家中心积分页面-会员权益(MB&PC)-积分信息/经验值/购买日展示','欧阳辉&刘玲',7,161)
//        ganttTasksService.importTask('2.3','iColor买家中心积分页面-会员权益(MB&PC)-经验结构','欧阳辉&刘玲',8,161)
//        ganttTasksService.importTask('2.4','iColor买家中心积分页面-会员权益(MB&PC)-我的经验值','欧阳辉&刘玲',9,161)
//        ganttTasksService.importTask('3','iColor买家中心积分页面-会员积分(MB&PC)','欧阳辉&刘玲',10,161)
//        ganttTasksService.importTask('3.1','iColor买家中心积分页面-会员积分(MB&PC)-页面UI','欧阳辉&刘玲',11,161)
//        ganttTasksService.importTask('3.2','iColor买家中心积分页面-会员积分(MB&PC)-我的积分','欧阳辉&刘玲',12,161)
//        ganttTasksService.importTask('3.3','iColor买家中心积分页面-会员积分(MB&PC)-积分明细','欧阳辉&刘玲',13,161)
//        ganttTasksService.importTask('3.4','iColor买家中心积分页面-会员积分(MB&PC)-积分收入','欧阳辉&刘玲',14,161)
//        ganttTasksService.importTask('3.5','iColor买家中心积分页面-会员积分(MB&PC)-积分支出','欧阳辉&刘玲',15,161)
//        ganttTasksService.importTask('4','iColor买家中心积分页面-会员抽奖(MB&PC)','胡盼盼',16,161)
//        ganttTasksService.importTask('4.1','iColor买家中心积分页面-会员抽奖(MB&PC)-UI','胡盼盼',17,161)
//        ganttTasksService.importTask('4.2','iColor买家中心积分页面-会员抽奖(MB&PC)-功能','胡盼盼',18,161)
//        ganttTasksService.importTask('4.3','iColor买家中心积分页面-会员抽奖(MB&PC)-活动规则','胡盼盼',19,161)
//        ganttTasksService.importTask('5','iColor商城结算页面(MB&PC)','欧阳辉&刘玲',20,161)
//        ganttTasksService.importTask('5.1','iColor商城结算页面(MB&PC)-旧功能UI业务优化','欧阳辉&刘玲',21,161)
//        ganttTasksService.importTask('5.2','iColor商城结算页面(MB&PC)-积分抵扣','欧阳辉&刘玲',22,161)
//        ganttTasksService.importTask('6','iColor买家中心订单详页面-显示积分抵扣信息','欧阳辉&刘玲',23,161)
//        ganttTasksService.importTask('7','iColor卖家中心订单详页面-显示积分抵扣信息','欧阳辉&刘玲',24,161)
//        ganttTasksService.importTask('8','iColor客户经验值业务','欧阳辉',25,161)
//        ganttTasksService.importTask('9','iColor客户购买日业务','欧阳辉',26,161)
//        ganttTasksService.importTask('10','iColor客户用户等级业务','欧阳辉',27,161)
//        ganttTasksService.importTask('11','iColor客户积分业务','欧阳辉',28,161)
//        ganttTasksService.importTask('12','icolorretailer平台，会员管理','张进松',29,161)
//        ganttTasksService.importTask('12.1','icolorretailer平台，会员管理-显示会员列表','张进松',30,161)
//        ganttTasksService.importTask('12.2','icolorretailer平台，会员管理-导出会员记录','张进松',31,161)
//        ganttTasksService.importTask('12.3','icolorretailer平台，会员管理-新增会员记录','张进松',32,161)
//        ganttTasksService.importTask('13','icolorretailer平台，兑换产品发布管理','张进松',33,161)
//        ganttTasksService.importTask('13.1','icolorretailer平台，兑换产品发布管理-增加','张进松',34,161)
//        ganttTasksService.importTask('13.2','icolorretailer平台，兑换产品发布管理-列表展示','张进松',35,161)
//        ganttTasksService.importTask('13.3','icolorretailer平台，兑换产品发布管理-编辑','张进松',36,161)
//        ganttTasksService.importTask('13.4','icolorretailer平台，兑换产品发布管理-删除','张进松',37,161)
//        ganttTasksService.importTask('14','icolorretailer平台，买家中心页面装修管理(MB&PC)','张进松',38,161)
//        ganttTasksService.importTask('15','icolorretailer平台，兑换订单列表管理','陈纲',39,161)
//        ganttTasksService.importTask('15.1','icolorretailer平台，兑换订单列表管理-列表','陈纲',40,161)
//        ganttTasksService.importTask('15.2','icolorretailer平台，兑换订单列表管理-详情','陈纲',41,161)
//        ganttTasksService.importTask('16','icolorretailer平台，抽奖中奖规则管理','胡盼盼',42,161)
//        ganttTasksService.importTask('16.1','icolorretailer平台，抽奖中奖规则管理-设置','胡盼盼',43,161)
//        ganttTasksService.importTask('16.2','icolorretailer平台，抽奖中奖规则管理-中奖名单','胡盼盼',44,161)
//        ganttTasksService.importTask('16.3','财务对账（影响-沈中杰确认）','田伟',45,161)
//        ganttTasksService.importTask('16.4','退款业务（影响-沈中杰确认）','胡盼盼',46,161)

        ganttTasksService.importTask('17','支付直联功能实现','田伟',47,161)
        ganttTasksService.importTask('17.1','支付宝直联icolor系统','田伟',48,161)
        ganttTasksService.importTask('17.2','微信支付直联icolor系统','田伟',49,161)

        render("import success17!")
    }

    @Transactional
    def save(GanttTasks ganttTasks) {
        if (ganttTasks == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (ganttTasks.hasErrors()) {
            if (ganttTasks.getId()) {
                transactionStatus.setRollbackOnly()
            }
            respond ganttTasks.errors, view: 'create'
            return
        }
        def users = ganttTasks.users;
        users?.each { user ->
            ganttTasks.addToUsers(user);
        }
        ganttTasks.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'ganttTasks.label', default: 'GanttTasks'), ganttTasks.id])
                redirect ganttTasks
            }
            '*' { respond ganttTasks, [status: CREATED] }
        }
    }

    def edit() {
        respond ganttTasksService.getTask(params.id);
    }


    @Transactional
    def delete(GanttTasks ganttTasks) {

        if (ganttTasks == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        ganttTasksService.deleteTask(ganttTasks.id)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'ganttTasks.label', default: 'GanttTasks'), ganttTasks.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: HttpStatus.NO_CONTENT }
        }
    }

    @Transactional
    def ajaxSave(GanttTasks ganttTasks) {
        if (ganttTasks == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (ganttTasks.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond ganttTasks.errors, view: 'create'
            return
        }

        ganttTasks.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'ganttTasks.label', default: 'GanttTasks'), ganttTasks.id])
                redirect ganttTasks
            }
            '*' { respond ganttTasks, [status: CREATED] }
        }
    }

    def ajaxEdit(GanttTasks ganttTasks) {
        respond ganttTasks
    }

    @Transactional
    def update(GanttTasks ganttTasks) {
        if (ganttTasks == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (ganttTasks.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond ganttTasks.errors, view: 'edit'
            return
        }
        def users = ganttTasks.users;
        users?.each { user ->
            ganttTasks.addToUsers(user);
        }
        ganttTasks.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'ganttTasks.label', default: 'GanttTasks'), ganttTasks.id])
                redirect ganttTasks
            }
            '*' { respond ganttTasks, [status: OK] }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'ganttTasks.label', default: 'GanttTasks'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: HttpStatus.NOT_FOUND }
        }
    }
}
