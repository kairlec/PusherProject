package com.kairlec.pusher.config.interceptor.pusher

import com.fasterxml.jackson.databind.ObjectMapper
import com.kairlec.pusher.annotation.OldPushBackwardsCompatibilityCondition
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Conditional
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
@Conditional(OldPushBackwardsCompatibilityCondition::class)
class OldPusherInterceptor : HandlerInterceptor {
    private val logger = LoggerFactory.getLogger(OldPusherInterceptor::class.java)

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var pusherUsers: Map<String, *>

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        return preHandle(request, response, handler, objectMapper, pusherUsers, blackList)
    }

    companion object {
        val blackList = arrayOf("/doc", "/doc/")
    }
}