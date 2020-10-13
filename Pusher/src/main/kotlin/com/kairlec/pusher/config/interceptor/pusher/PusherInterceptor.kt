package com.kairlec.pusher.config.interceptor.pusher

import com.fasterxml.jackson.databind.ObjectMapper
import com.kairlec.pusher.annotation.condition.PusherCondition
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Conditional
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Component
@Conditional(PusherCondition::class)
class PusherInterceptor : HandlerInterceptor {
    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var pusherUsers: Map<String, *>


    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        return preHandle(request, response, handler, objectMapper, pusherUsers, blackList)
    }

    companion object {
        val blackList = arrayOf("/push/doc", "/push/doc/")
    }
}