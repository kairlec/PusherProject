package com.kairlec.pusher.config.interceptor.pusher

import com.fasterxml.jackson.databind.ObjectMapper
import com.kairlec.pusher.annotation.condition.OldPusherBackwardsCompatibilityCondition
import com.kairlec.pusher.service.impl.UserServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Conditional
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
@Conditional(OldPusherBackwardsCompatibilityCondition::class)
class OldPusherInterceptor : HandlerInterceptor {
    @Autowired
    private lateinit var objectMapper: ObjectMapper

    //@Autowired
    //private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var userService: UserServiceImpl

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        return preHandle(request, response, handler, objectMapper, userService, blackList)
    }

    companion object {
        val blackList = arrayOf("/doc", "/doc/")
    }
}