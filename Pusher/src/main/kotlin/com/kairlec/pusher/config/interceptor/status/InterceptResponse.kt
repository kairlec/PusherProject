package com.kairlec.pusher.config.interceptor.status

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.MethodParameter
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.http.server.ServletServerHttpRequest
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice


@ControllerAdvice
@ConditionalOnProperty(prefix = "wework.push", value = ["enabled", "status.enabled"], matchIfMissing = true)
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

