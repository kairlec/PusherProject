package com.kairlec.pusher.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.kairlec.utils.ResponseDataUtil.responseError
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import javax.servlet.http.HttpServletResponse

/**
 * 重写Springboot对未处理异常的处理
 */
@ControllerAdvice
class CustomExceptionHandler {
    @Autowired
    private lateinit var objectMapper: ObjectMapper

    /**
     * 这里不做返回值,直接设置contentType并写入responseBody以防止做为XML处理
     */
    @ExceptionHandler
    @ResponseBody
    fun exception(e: Exception, response: HttpServletResponse){
        val serviceError = e.responseError
        response.status = serviceError.status.value()
        response.contentType = "application/json; charset=UTF-8"
        response.writer.write(objectMapper.writeValueAsString(serviceError))
    }

}
