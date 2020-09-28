package com.kairlec.pusher.config.interceptor.pusher

import com.fasterxml.jackson.databind.ObjectMapper
import com.kairlec.error.SKException
import com.kairlec.utils.check
import org.slf4j.LoggerFactory
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

private val logger = LoggerFactory.getLogger(PusherInterceptor::class.java)

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
        logger.debug("${request.requestURI} is in blackList")
        return true
    }
    return try {
        val userid = request.getParameter("userid") ?: SKException.ServiceErrorEnum.MISSING_REQUEST_PART.throwout()
        val token = request.getParameter("token") ?: SKException.ServiceErrorEnum.MISSING_REQUEST_PART.throwout()
        val touser = request.getParameter("touser") ?: userid
        request.setAttribute("touser", touser)
        check(pusherUsers, userid, token, touser)
        logger.debug("check $userid success")
        true
    } catch (e: SKException) {
        response.contentType = "application/json; charset=UTF-8"
        logger.debug("check failed:${e.getServiceErrorEnum()?.msg}")
        response.writer.write(objectMapper.writeValueAsString(e.getServiceErrorEnum()))
        false
    }
}