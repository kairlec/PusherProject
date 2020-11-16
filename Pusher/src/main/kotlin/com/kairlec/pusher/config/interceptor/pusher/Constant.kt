package com.kairlec.pusher.config.interceptor.pusher

import com.fasterxml.jackson.databind.ObjectMapper
import com.kairlec.error.SKException
import com.kairlec.pusher.openapi.pojo.PusherUser
import com.kairlec.pusher.openapi.pojo.SubscriberLevel
import com.kairlec.pusher.service.UserService
import org.slf4j.LoggerFactory
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

private val logger = LoggerFactory.getLogger(PusherInterceptor::class.java)

/**
 * @param request HTTPRequest
 * @param response HTTPResponse
 * @param handler 句柄
 * @param objectMapper ObjectMapper用来序列化结果
 * @param userService 用户服务
 * @param blackList URI白名单列表,既不经过拦截器的URI
 */
@Suppress("UNUSED_PARAMETER")
internal fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        objectMapper: ObjectMapper,
        userService: UserService,
        blackList: Array<String>
): Boolean {
    if (request.requestURI in blackList) {
        if (logger.isDebugEnabled) {
            logger.debug("${request.requestURI} is in blackList")
        }
        return true
    }
    return try {
        val userid = request.getParameter("userid")
        val username = request.getParameter("username")
        val openid = request.getParameter("openid")
        val token = request.getParameter("token") ?: SKException.ServiceErrorEnum.MISSING_REQUEST_PART.throwout()
        val touser = request.getParameter("touser")
        val keyword = request.getParameter("keyword")
        val levelString = request.getParameter("level") ?: "INFO"
        val level = SubscriberLevel.parse(levelString)
        if (level == null) {
            val like = SubscriberLevel.similar(levelString)
            val result = SKException.ServiceErrorEnum.UNKNOWN_PARAMETER
            if (like != null) {
                result.data("Unknown parameter [${levelString}]. Do you mean \"${like.name}\"")
            } else {
                result.data("Unknown parameter [${levelString}].")
            }
            result.throwout()
        }
        request.setAttribute("touser", check(userService, token, touser, userid, username, openid, level, keyword))
        if (logger.isDebugEnabled) {
            logger.debug("check $userid success")
        }
        true
    } catch (e: SKException) {
        response.contentType = "application/json; charset=UTF-8"
        if (logger.isDebugEnabled) {
            logger.debug("check failed:${e.getServiceErrorEnum()?.msg}")
        }
        response.writer.write(objectMapper.writeValueAsString(e.getServiceErrorEnum()))
        false
    }
}

fun check(userService: UserService, token: String, touser: String?, userid: String?, username: String?, openid: String?, level: SubscriberLevel, keyword: String?): String {
    if (userid == null && username == null && openid == null) {
        SKException.ServiceErrorEnum.MISSING_REQUEST_PART.throwout()
    }
    val users: List<PusherUser> = userService.match(userid, username, openid)
    if (users.isEmpty()) {
        SKException.ServiceErrorEnum.NO_SUCH_USER.data(userid).throwout()
    }
    if (users.size > 1) {
        SKException.ServiceErrorEnum.MULTI_USER.data(userid).throwout()
    }
    val user = users[0]
    if (!user.matchToken(token)) {
        SKException.ServiceErrorEnum.VERIFICATION_FAILED.data(userid).throwout()
    }
    if (touser != userid && !user.admin) {
        SKException.ServiceErrorEnum.PERMISSION_DENIED.data(touser).throwout()
    }
    if(logger.isDebugEnabled){
        logger.debug("user sub -> ${user.pushConfig.subscriberLevel}")
        logger.debug("current -> $level")
        logger.debug("keyword:$keyword")
    }
    if (!user.pushConfig.allow(level, keyword)) {
        SKException.ServiceErrorEnum.NO_ERROR_FILTERED.data("{$level} $keyword").throwout()
    }
    return touser ?: user.userid
}
