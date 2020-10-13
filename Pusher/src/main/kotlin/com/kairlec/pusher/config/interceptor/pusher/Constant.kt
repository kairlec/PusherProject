package com.kairlec.pusher.config.interceptor.pusher

import com.fasterxml.jackson.databind.ObjectMapper
import com.kairlec.error.SKException
import com.kairlec.pojo.PusherUser
import org.slf4j.LoggerFactory
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

private val logger = LoggerFactory.getLogger(PusherInterceptor::class.java)

/**
 * @param request HTTPRequest
 * @param response HTTPResponse
 * @param handler 句柄
 * @param objectMapper ObjectMapper用来序列化结果
 * @param pusherUsers 用户列表
 * @param blackList URI白名单列表,既不经过拦截器的URI
 */
@Suppress("UNUSED_PARAMETER")
internal fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        objectMapper: ObjectMapper,
        pusherUsers: Map<String, *>,
        blackList: Array<String>
): Boolean {
    if (request.requestURI in blackList) {
        if (logger.isDebugEnabled) {
            logger.debug("${request.requestURI} is in blackList")
        }
        return true
    }
    return try {
        val userid = request.getParameter("userid") ?: SKException.ServiceErrorEnum.MISSING_REQUEST_PART.throwout()
        val token = request.getParameter("token") ?: SKException.ServiceErrorEnum.MISSING_REQUEST_PART.throwout()
        val touser = request.getParameter("touser") ?: userid
        request.setAttribute("touser", touser)
        check(pusherUsers, userid, token, touser)
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

fun check(pusherUsers: Map<String, *>, userid: String, token: String, touser: String) {
    if (userid !in pusherUsers) {
        SKException.ServiceErrorEnum.NO_SUCH_USER.data(userid).throwout()
    }
    val user = pusherUsers[userid] as? PusherUser ?: SKException.ServiceErrorEnum.NO_SUCH_USER.data(userid).throwout()
    if (user.token != token) {
        SKException.ServiceErrorEnum.VERIFICATION_FAILED.data(userid).throwout()
    }
    if (touser != userid && !user.admin) {
        SKException.ServiceErrorEnum.PERMISSION_DENIED.data(touser).throwout()
    }
}
