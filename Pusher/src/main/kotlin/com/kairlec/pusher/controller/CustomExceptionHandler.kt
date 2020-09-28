package com.kairlec.pusher.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.kairlec.utils.ResponseDataUtil.responseError
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import javax.servlet.http.HttpServletResponse

/**
 *@program: Backend
 *@description: 重写Springboot对未处理异常的处理
 *@author: Kairlec
 *@create: 2020-02-28 16:55
 */

@ControllerAdvice
class CustomExceptionHandler {
    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @ExceptionHandler
    @ResponseBody
    fun exception(e: Exception, response: HttpServletResponse){
        val serviceError = e.responseError
        response.status = serviceError.status.value()
        response.contentType = "application/json; charset=UTF-8"
        response.writer.write(objectMapper.writeValueAsString(serviceError))
    }

    companion object {
        private val logger = LogManager.getLogger(CustomExceptionHandler::class.java)
    }
}
