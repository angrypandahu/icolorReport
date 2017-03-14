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
    def filterPaneService
    def exportService

    def filter = {
        if (!params.max) params.max = 10
        authTask()

        if (params?.f && params.f != "html") {
            export();
        } else {
            render(view: 'index',
                    model: [ganttTasksList : filterPaneService.filter(params, GanttTasks.class),
                            ganttTasksCount: filterPaneService.count(params, GanttTasks.class),
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
        exportService.export(params.f, response.outputStream, filterPaneService.filter(params, GanttTasks.class) as List, fields, labels, formatters, parameters)
    }

    private authTask() {
        def user = getAuthenticatedUser();
        def authorities = user.getAuthorities();
        def roleAdmin = Role.findByAuthority("ROLE_ADMIN");
        def roleManager = Role.findByAuthority("ROLE_MANAGER");

        def projects = GanttTasks.findAllByType('project', [sort: 'sorter', order: 'desc'])
        if (projects) {
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
        List<GanttTasks> list = filterPaneService.filter(params, GanttTasks.class) as List
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
                        ganttTasksCount: filterPaneService.count(params, GanttTasks.class),
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
        ganttTasksService.importTask('1', '刷新家APP', '', 1, 1)
        ganttTasksService.importTask('1.1', '刷新家首页', '孙明有', 2, 1)
        ganttTasksService.importTask('1.2', '刷新家VR列表页', '舒峰峰', 3, 1)
        ganttTasksService.importTask('1.3', '刷新家家品列表页', '孙明有', 4, 1)
        ganttTasksService.importTask('2', '刷新POS付款', '', 5, 1)
        ganttTasksService.importTask('2.1', '与刷新服务系统接口', '张进松', 6, 1)
        ganttTasksService.importTask('2.2', '刷新POS付款接口', '田伟', 7, 1)
        ganttTasksService.importTask('3', '美居美图PC端', '', 8, 1)
        ganttTasksService.importTask('3.1', '图片/文章列表页', '杨竹庆', 9, 1)
        ganttTasksService.importTask('3.1.1', '搜索框', '杨竹庆', 10, 1)
        ganttTasksService.importTask('3.2', '文章详情页', '杨竹庆', 11, 1)
        ganttTasksService.importTask('3.2.1', '显示全文', '杨竹庆', 12, 1)
        ganttTasksService.importTask('3.3', '视频详情页', '杨竹庆', 13, 1)
        ganttTasksService.importTask('3.3.1', '相关产品推荐', '杨竹庆', 14, 1)
        ganttTasksService.importTask('3.4', '文章/图片/专题/视频列表页', '杨竹庆', 15, 1)
        ganttTasksService.importTask('3.4.1', '热门推荐', '杨竹庆', 16, 1)
        ganttTasksService.importTask('3.5', '图片详情页', '杨竹庆', 17, 1)
        ganttTasksService.importTask('3.5.1', '图片切换', '杨竹庆', 18, 1)
        ganttTasksService.importTask('3.5.2', '左上角功能按钮', '杨竹庆', 19, 1)
        ganttTasksService.importTask('3.5.3', '右上角全屏模式按钮', '杨竹庆', 20, 1)
        ganttTasksService.importTask('4', 'PC设计师频道', '', 21, 1)
        ganttTasksService.importTask('4.1', '聚合页', '陈岩', 22, 1)
        ganttTasksService.importTask('4.2', '设计师列表页', '陈岩', 23, 1)
        ganttTasksService.importTask('4.3', '设计师详情页', '陈岩', 24, 1)
        ganttTasksService.importTask('4.4', '案例展详情页', '陈岩', 25, 1)
        ganttTasksService.importTask('4.5', '在建工地列表页', '果建鑫', 26, 1)
        ganttTasksService.importTask('4.6', '在建工地详情页', '果建鑫', 27, 1)
        ganttTasksService.importTask('4.7', '报名页', '果建鑫', 28, 1)
        ganttTasksService.importTask('4.8', '报名浮层', '果建鑫', 29, 1)
        ganttTasksService.importTask('4.9', '点评列表页', '果建鑫', 30, 1)
        ganttTasksService.importTask('4.1', '点评详情页', '果建鑫', 31, 1)
        ganttTasksService.importTask('4.11', '用户点评发布页', '果建鑫', 32, 1)
        ganttTasksService.importTask('4.12', '设计师留言本', '果建鑫', 33, 1)
        ganttTasksService.importTask('4.13', '设计师 商业广告位', '果建鑫', 34, 1)
        ganttTasksService.importTask('4.14', '点赞功能', '果建鑫', 35, 1)
        ganttTasksService.importTask('5', 'PC装修公司频道', '果建鑫', 36, 1)
        ganttTasksService.importTask('5.1', '聚合页', '', 37, 1)
        ganttTasksService.importTask('5.2', '装修公司列表页', '陈岩', 38, 1)
        ganttTasksService.importTask('5.3', '装修公司详情页', '陈岩', 39, 1)
        ganttTasksService.importTask('5.4', '装修公司案例展详情页', '果建鑫', 40, 1)
        ganttTasksService.importTask('5.5', '在建工地列表页', '果建鑫', 41, 1)
        ganttTasksService.importTask('5.6', '在建工地详情页', '果建鑫', 42, 1)
        ganttTasksService.importTask('5.7', '店铺预约页', '果建鑫', 43, 1)
        ganttTasksService.importTask('5.8', '报名浮层', '果建鑫', 44, 1)
        ganttTasksService.importTask('5.9', '设计师介绍页', '果建鑫', 45, 1)
        ganttTasksService.importTask('5.1', '点评列表页', '果建鑫', 46, 1)
        ganttTasksService.importTask('5.11', '点评详情页', '果建鑫', 47, 1)
        ganttTasksService.importTask('5.12', '装修公司留言本', '果建鑫', 48, 1)
        ganttTasksService.importTask('5.13', '点赞功能', '果建鑫', 49, 1)
        ganttTasksService.importTask('5.14', '装修公司商业广告位', '果建鑫', 50, 1)
        ganttTasksService.importTask('5.15', '用户点评发布页', '果建鑫', 51, 1)
        ganttTasksService.importTask('6', '管理模块', '', 52, 1)
        ganttTasksService.importTask('6.1', '案例管理', '果建鑫', 53, 1)
        ganttTasksService.importTask('6.2', '公司管理', '果建鑫', 54, 1)
        ganttTasksService.importTask('6.3', '设计师管理', '果建鑫', 55, 1)
        ganttTasksService.importTask('6.4', '评论管理', '果建鑫', 56, 1)
        ganttTasksService.importTask('6.5', '口碑值计算', '果建鑫', 57, 1)
        ganttTasksService.importTask('6.6', '在建工地管理', '果建鑫', 58, 1)
        ganttTasksService.importTask('6.7', '人工加减分机制', '果建鑫', 59, 1)
        ganttTasksService.importTask('6.8', '留言管理', '果建鑫', 60, 1)
        ganttTasksService.importTask('6.9', '装修排名管理', '果建鑫', 61, 1)
        ganttTasksService.importTask('6.1', '设计师排名管理', '果建鑫', 62, 1)
        ganttTasksService.importTask('6.11', '装修公司标签管理(金牌施工队、绿色环保...,1)', '果建鑫', 63, 1)
        ganttTasksService.importTask('6.12', '设计师标签管理(金牌设计师...)', '果建鑫', 64, 1)
        ganttTasksService.importTask('6.13', '装修公司广告位管理', '果建鑫', 65, 1)
        ganttTasksService.importTask('6.14', '设计师广告位管理', '果建鑫', 66, 1)
        ganttTasksService.importTask('6.15', '统计报表 店铺/设计师 浏览量/预约量', '果建鑫', 67, 1)
        ganttTasksService.importTask('6.16', '报表-每日点评量/上传案例数量/在建工地数量', '果建鑫', 68, 1)
        ganttTasksService.importTask('7', '分阶段付款', '', 69, 1)
        ganttTasksService.importTask('7.1', 'PC 产品详情页面（后台逻辑+ 前台UI）', '陈纲', 70, 1)
        ganttTasksService.importTask('7.2', 'PC 购物车（后台逻辑+ 前台UI）', '陈纲', 71, 1)
        ganttTasksService.importTask('7.2.1', 'PC 商户中心发货', '陈纲', 72, 1)
        ganttTasksService.importTask('7.3', '订单确认页面', '陈纲', 73, 1)
        ganttTasksService.importTask('7.4', '分期产品发布', '张进松', 74, 1)
        ganttTasksService.importTask('7.5', 'MOBILE产品详情页面(前台UI,1)', '张进松', 75, 1)
        ganttTasksService.importTask('7.6', 'MOBILE 购物车（前台UI）', '刘俊伟', 76, 1)
        ganttTasksService.importTask('7.7', '财务对帐', '田伟', 77, 1)
        ganttTasksService.importTask('7.8', '退款 mobile&pc(后台逻辑与前台UI,1)', '胡盼盼', 78, 1)
        ganttTasksService.importTask('7.9', '支付功能更新（支付分期）', '刘俊伟', 79, 1)
        ganttTasksService.importTask('7.10', '个人中心-订单管理（更新功能support 分阶段付款 APP&MOBILE）', '刘俊伟', 80, 1)
        ganttTasksService.importTask('7.11', '给店铺增加属性，标识该店铺可以发布分期付款功能', '果建鑫', 81, 1)
        ganttTasksService.importTask('8', 'iColor商城功能优化', '', 82, 1)
        ganttTasksService.importTask('8.1', '店铺管理模块', '张进松/果建鑫', 83, 1)
        ganttTasksService.importTask('8.1.1', '店铺与一级类目对应关系修改成1对多', '张进松/果建鑫', 84, 1)
        ganttTasksService.importTask('8.1.2', '店铺与品牌的对应关系修改成1对多', '张进松/果建鑫', 85, 1)
        ganttTasksService.importTask('8.2', '家品发布模块', '张进松/果建鑫', 86, 1)
        ganttTasksService.importTask('8.2.1', '用户发布家品时类目选择功能优化', '张进松/果建鑫', 87, 1)
        ganttTasksService.importTask('8.2.2', '用户发布家品时品牌变成可选择', '张进松/果建鑫', 88, 1)
        ganttTasksService.importTask('8.3', '商品模块', '张进松/果建鑫', 89, 1)
        ganttTasksService.importTask('8.3.1', '新增佣金和商品的绑定关系', '张进松', 90, 1)
        ganttTasksService.importTask('8.4', '出售中的家品模块', '张进松', 91, 1)
        ganttTasksService.importTask('8.4.1', '新增批量导出功能', '张进松', 92, 1)
        ganttTasksService.importTask('8.5', '仓库中的家品模块', '张进松', 93, 1)
        ganttTasksService.importTask('8.5.1', '新增批量导出功能', '张进松', 94, 1)
        ganttTasksService.importTask('8.6', '美居家品首页', '张进松', 95, 1)
        ganttTasksService.importTask('8.6.1', '想设计爱生活区域增加广告位', '张进松', 96, 1)
        ganttTasksService.importTask('8.7', '店铺管理模块', '张进松', 97, 1)
        ganttTasksService.importTask('8.7.1', '店铺信息字段新增用户最近一次“登录时间”', '张进松/果建鑫', 98, 1)
        ganttTasksService.importTask('8.8', '家品审核模块', '果建鑫', 99, 1)
        ganttTasksService.importTask('8.8.1', '店铺信息字段新增未审核商品提示', '果建鑫', 100, 1)
        ganttTasksService.importTask('8.9', 'iColor商城', '张进松', 101, 1)
        ganttTasksService.importTask('8.9.1', 'iColor商城顶部购物车Banner固定', '张进松', 102, 1)
        ganttTasksService.importTask('8.9.2', '家品搜索结果页，商品可点击区域优化', '张进松', 103, 1)
        ganttTasksService.importTask('8.10', '店铺卡券模块', '欧阳辉/刘玲', 104, 1)
        ganttTasksService.importTask('8.10.1', '店铺商品优惠券，商品选择功能优化', '欧阳辉/刘玲', 105, 1)
        ganttTasksService.importTask('8.11', '已卖出商品模块', '陈纲', 106, 1)
        ganttTasksService.importTask('8.11.1', '“成交时间”修改成“成交时间段”', '陈纲', 107, 1)
        ganttTasksService.importTask('8.12', '店铺基本资料模块', '陈纲', 108, 1)
        ganttTasksService.importTask('8.12.1', '优化：部分字段用户只能看不能修改', '陈纲', 109, 1)
        ganttTasksService.importTask('8.13', '家品管理模块', '张进松', 110, 1)
        ganttTasksService.importTask('8.13.1', '新增“待审核家品”功能', '张进松', 111, 1)
        ganttTasksService.importTask('8.14', '退款管理模块', '胡盼盼', 112, 1)
        ganttTasksService.importTask('8.14.1', '增加“支付流水号”信息', '胡盼盼', 113, 1)
        ganttTasksService.importTask('8.14.2', '增加“取消退款”功能', '胡盼盼', 114, 1)
        ganttTasksService.importTask('8.15', '个人中心-订单管理', '陈纲', 115, 1)
        ganttTasksService.importTask('8.15.1', '待支付订单增加“取消订单”按钮', '陈纲', 116, 1)
        ganttTasksService.importTask('8.15.2', '已取消订单增加“删除订单”按钮', '陈纲', 117, 1)
        ganttTasksService.importTask('8.15.3', '卖家已发货订单增加“自动确认收货”倒计时信息', '陈纲', 118, 1)
        ganttTasksService.importTask('8.15.4', '已评价订单修“点击评价”修改成“追加评价”', '陈纲', 119, 1)
        ganttTasksService.importTask('8.16', '平台优惠券管理模块', '欧阳辉/刘玲', 120, 1)
        ganttTasksService.importTask('8.16.1', '平台优惠券创建', '欧阳辉/刘玲', 121, 1)
        ganttTasksService.importTask('8.16.2', '平台优惠券管理（修改、删除）', '欧阳辉/刘玲', 122, 1)
        ganttTasksService.importTask('8.16.3', '购物车结算-推荐码-APP端', '刘俊伟', 123, 1)
        ganttTasksService.importTask('8.16.4', '商品详情页-商品加入购物车/立即购买 -APP端', '张进松', 124, 1)
        ganttTasksService.importTask('8.16.5', '商品详情页-购物车页-商品属性修改-APP端', '刘俊伟', 125, 1)
        ganttTasksService.importTask('8.16.6', '商家中心-店铺卡券', '刘玲', 126, 1)
        ganttTasksService.importTask('8.16.7', '平台管理后台-平台优惠券管理', '果建鑫', 127, 1)
        ganttTasksService.importTask('8.16.8', '优惠券领取登录提示页PC', '陈碳', 128, 1)
        ganttTasksService.importTask('8.16.9', '优惠券领取登录提示页APP', '欧麒瑞', 129, 1)
        ganttTasksService.importTask('8.15.5', '从结算页面点击编辑地址到地址页面后，新增加链接返回到结算页面', '陈纲', 130, 1)
        ganttTasksService.importTask('9', '商品销售区域', '陈纲', 131, 1)
        ganttTasksService.importTask('9.1', '家品发布模块', '张进松', 132, 1)
        ganttTasksService.importTask('9.1.1', '增加不可销售区域设置功能', '张进松', 133, 1)
        ganttTasksService.importTask('9.2', '商品详情模块', '张进松', 134, 1)
        ganttTasksService.importTask('9.2.1', '选中不可销售区域，用户无法购买或加入购物车', '张进松', 135, 1)
        ganttTasksService.importTask('9.3', '购物车结算模块', '陈纲', 136, 1)
        ganttTasksService.importTask('9.3.1', '用户选中配送地址在不可销售区域内，弹出提示框', '陈纲', 137, 1)
        ganttTasksService.importTask('10', 'iColor礼品卡', '', 138, 1)
        ganttTasksService.importTask('10.1', 'iColor礼品卡管理（新增模块）', '果建鑫/田伟', 139, 1)
        ganttTasksService.importTask('10.1.1', 'iColor礼品卡创建', '果建鑫/田伟', 140, 1)
        ganttTasksService.importTask('10.1.2', 'iColor礼品卡管理（查询、冻结、解冻、删除）', '果建鑫/田伟', 141, 1)
        ganttTasksService.importTask('10.2', '我的礼品卡（新增模块）', '欧阳辉/刘玲', 142, 1)
        ganttTasksService.importTask('10.2.1', 'iColor礼品卡绑定', '欧阳辉/刘玲', 143, 1)
        ganttTasksService.importTask('10.2.2', 'iColor礼品卡查询', '欧阳辉/刘玲', 144, 1)
        ganttTasksService.importTask('10.2.3', '已绑定iColor礼品卡明细', '欧阳辉/刘玲', 145, 1)
        ganttTasksService.importTask('10.2.4', '已绑定iColor礼品卡解绑', '欧阳辉/刘玲', 146, 1)
        ganttTasksService.importTask('10.3', '我的订单模块', '欧阳辉/刘玲', 147, 1)
        ganttTasksService.importTask('10.3.1', '增加iColor礼品卡抵扣金额信息', '欧阳辉/刘玲', 148, 1)
        ganttTasksService.importTask('10.4', '购物车结算模块', '欧阳辉/刘玲', 149, 1)
        ganttTasksService.importTask('10.4.1', '增加iColor礼品卡抵扣功能', '欧阳辉/刘玲', 150, 1)
        ganttTasksService.importTask('10.4.2', '订单结算页面信息展示优化', '欧阳辉/刘玲', 151, 1)
        ganttTasksService.importTask('10.5', '已卖出商品模块', '欧阳辉/刘玲', 152, 1)
        ganttTasksService.importTask('10.5.1', '增加iColor礼品卡抵扣金额信息', '欧阳辉/刘玲', 153, 1)
        ganttTasksService.importTask('10.6', '礼品卡退货退款', '胡盼盼', 154, 1)
        ganttTasksService.importTask('10.6.1', '退货退货流程更新，支持4.0礼品功能', '胡盼盼', 155, 1)
        ganttTasksService.importTask('10.7', '账务对账', '田伟', 156, 1)
        ganttTasksService.importTask('10.7.1', '产品新增加佣金财务对账', '田伟', 157, 1)
        ganttTasksService.importTask('10.7.2', '新增礼品卡财务对账', '田伟', 158, 1)
        ganttTasksService.importTask('10.7.3', '分阶段付款财务对账', '田伟', 159, 1)
        render("import success!")
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
