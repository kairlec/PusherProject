package com.kairlec.pusher.config.interceptor.pusher

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Component
@ConditionalOnProperty(prefix = "wework.push", value = ["enabled"], matchIfMissing = true)
class PusherInterceptor : HandlerInterceptor {
    private val logger = LoggerFactory.getLogger(PusherInterceptor::class.java)

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var pusherUsers: Map<String, *>


    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        return preHandle(request, response, handler, objectMapper, pusherUsers, blackList)
    }

    companion object {
        //URI白名单列表,既不经过拦截器的URI
        val blackList = arrayOf("/push/doc", "/push/doc/")
    }
}