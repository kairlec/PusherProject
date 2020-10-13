package com.kairlec.pusher.config.interceptor.status

import com.kairlec.error.SKException
import com.kairlec.intf.ResponseDataInterface
import com.kairlec.pusher.annotation.StatusCount
import com.kairlec.pusher.annotation.condition.StatusReportCondition
import com.kairlec.pusher.util.StatusCountUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Conditional
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 请求状态拦截器,拦截返回内容
 * 判断返回内容是否是ResponseDataInterface
 * 若是则判断返回值是否为0
 * 若否则判断响应状态是否为200
 */
@Component
@Conditional(StatusReportCondition::class)
class StatusInterceptor : HandlerInterceptor {

    @Autowired
    private lateinit var statusCountUtil: StatusCountUtil

    override fun postHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any, modelAndView: ModelAndView?) {
        if (handler is HandlerMethod) {
            val statusCount = handler.getMethodAnnotation(StatusCount::class.java) ?: return
            val responseBody = request.getAttribute(RESPONSE_BODY)
            if (responseBody is ResponseDataInterface) {
                if (responseBody.code == 0 && statusCount.success) {
                    statusCountUtil.addSuccessCount()
                }
                if (responseBody.code != 0 && statusCount.error) {
                    statusCountUtil.addErrorCount()
                }
            } else {
                if (response.status == 200) {
                    statusCountUtil.addSuccessCount()
                } else {
                    statusCountUtil.addErrorCount()
                }
            }
        }
    }

    override fun afterCompletion(request: HttpServletRequest, response: HttpServletResponse, handler: Any, ex: Exception?) {
        ex?.let { exp ->
            if (handler is HandlerMethod) {
                val statusCount = handler.getMethodAnnotation(StatusCount::class.java) ?: return
                if (exp is SKException) {
                    exp.getServiceErrorEnum()?.let {
                        if (it.ok && statusCount.success) {
                            statusCountUtil.addSuccessCount()
                        }
                        if (!it.ok && statusCount.error) {
                            statusCountUtil.addErrorCount()
                        }
                    } ?: if (statusCount.error) {
                        statusCountUtil.addErrorCount()
                    }
                }
            }
        }
    }
}