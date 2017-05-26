package domain.report

import com.domain.auth.User
import com.nippon.export.export.ReportUtils
import com.nippon.export.export.WxUtils
import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails

import javax.servlet.http.HttpSession

class WxController {

    final String WX_LOGIN_URL = "https://api.weixin.qq.com/sns/jscode2session";
    final String APP_ID = "wxb5b560b6880f1919";
    final String SECRET = "a4ff6aa465fd52d5790002e6bc58245f";
    def authenticationManager
    def userDetailsService

    def openId() {
        def wxParam = [:]
        wxParam.put("appid", APP_ID);
        wxParam.put("secret", SECRET);
        wxParam.put("js_code", params.code);
        wxParam.put("grant_type", "authorization_code");
        def all = WxUtils.doAll(WX_LOGIN_URL, wxParam, "GET")
        println("openId->${all}")
        render all;
    }

    def associateOpenId() {
        def retJson = new JSONObject();
        Authentication authenticationToken = new UsernamePasswordAuthenticationToken(params.username, params.password);
        try {
            Authentication result = authenticationManager.authenticate(authenticationToken);
            HttpSession session = request.getSession();
            SecurityContextHolder.getContext().setAuthentication(result);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext()); // 这个非常重要，否则验证后将无法登陆
            User user = getAuthenticatedUser();
            user.setWxOpenId(params.openId);
            user.save();
            def sessionId = session.getId();
            retJson.put("sessionId", sessionId)
            retJson.put("hasNoUserByOpenId", false);
            retJson.put("user", ReportUtils.userToJson(user))
            println(retJson)
            render retJson
        } catch (Exception e) {
            def errors = new JSONArray();
            def error = new JSONObject();
            error.put("message", e.getMessage())
            errors.put(error)
            retJson.put("errors", errors);
            render(retJson);
        }


    }

    def login() {
        def userByWx = User.findByWxOpenId(params.openId);
        def retJson = new JSONObject();
        if (!userByWx) {
            retJson.put("hasNoUserByOpenId", true);
            println(retJson)
            render retJson
            return
        }
        UserDetails user = userDetailsService.loadUserByUsername(userByWx.username);
        Authentication auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        HttpSession session = request.getSession();
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext()); // 这个非常重要，否则验证后将无法登陆
        def sessionId = session.getId();
        retJson.put("sessionId", sessionId)
        retJson.put("hasNoUserByOpenId", false);
        retJson.put("user", ReportUtils.userToJson(userByWx))
        println(retJson)
        render retJson
    }

    def ajaxLogin() {
        def retJson = new JSONObject();
        Authentication authenticationToken = new UsernamePasswordAuthenticationToken(params.username, params.password);
        try {
            Authentication result = authenticationManager.authenticate(authenticationToken);
            HttpSession session = request.getSession();
            SecurityContextHolder.getContext().setAuthentication(result);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext()); // 这个非常重要，否则验证后将无法登陆
            def sessionId = session.getId();
            retJson.put("sessionId", sessionId)
            retJson.put("user", ReportUtils.userToJson(result.principal))
            println(retJson)
            render retJson
        } catch (Exception e) {
            def errors = new JSONArray();
            def error = new JSONObject();
            error.put("message", e.getMessage())
            errors.put(error)
            retJson.put("errors", errors);
            render(retJson);
        }
    }


}
