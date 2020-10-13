package com.kairlec.pusher.config.interceptor.status

import com.kairlec.pusher.annotation.condition.StatusReportCondition
import org.springframework.context.annotation.Conditional
import org.springframework.core.MethodParameter
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.http.server.ServletServerHttpRequest
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice

/**
 * 拦截返回的内容
 * 将返回的内容设置到request的属性以便接下来能在拦截器中获取到返回内容
 */
@ControllerAdvice
@Conditional(StatusReportCondition::class)
class InterceptResponse : ResponseBodyAdvice<Any> {

    override fun supports(methodParameter: MethodParameter, p1: Class<out HttpMessageConverter<*>>): Boolean {
        return true
    }

    override fun beforeBodyWrite(body: Any?, methodParameter: MethodParameter, mediaType: MediaType, aClass: Class<out HttpMessageConverter<*>>, serverHttpRequest: ServerHttpRequest, serverHttpResponse: ServerHttpResponse): Any? {
        val request = serverHttpRequest as ServletServerHttpRequest
        val servletRequest = request.servletRequest
        servletRequest.setAttribute(RESPONSE_BODY, body)
        return body
    }
}

