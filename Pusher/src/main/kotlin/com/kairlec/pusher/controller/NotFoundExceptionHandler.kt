package com.kairlec.pusher.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.kairlec.error.SKException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

/**
 * 重写Springboot对错误请求的处理
 */
@Controller
class NotFoundExceptionHandler : ErrorController {
    override fun getErrorPath(): String {
        return "/error"
    }

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @RequestMapping(value = ["/error"],produces = ["application/json"])
    @ResponseBody
    fun error(): String {
        return objectMapper.writeValueAsString(SKException.ServiceErrorEnum.UNKNOWN_REQUEST)
    }
}