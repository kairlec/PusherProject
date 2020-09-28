package com.kairlec.pusher.controller

import com.kairlec.error.SKException
import com.kairlec.intf.ResponseDataInterface
import com.kairlec.pusher.annotation.OldPushBackwardsCompatibilityCondition
import com.kairlec.pusher.annotation.StatusCount
import com.kairlec.pusher.core.wework.WeWorkSenderHelper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Conditional
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest


@RestController
@RequestMapping(value = [""], produces = ["application/json"])
@Conditional(OldPushBackwardsCompatibilityCondition::class)
class OldPusherController {

    @Autowired
    private lateinit var workSenderHelper: WeWorkSenderHelper

    @RequestMapping(value = ["/doc"], produces = ["text/html"])
    fun doc(request: HttpServletRequest): String {
        val url = request.requestURL.toString()
        return docHtml?.replace("_SERVER_BASE_URL_", url.substring(0, url.length - 4)) ?: run {
            PusherController::class.java.classLoader.getResourceAsStream("templates/old_doc.html")?.reader()?.use {
                docHtml = it.readText()
                docHtml!!.replace("_SERVER_BASE_URL_", url.substring(0, url.length - 4))
            } ?: throw RuntimeException("Cannot load old_doc.html")
        }
    }

    @StatusCount
    @RequestMapping(value = ["/"], method = [RequestMethod.POST])
    fun pushPost(@RequestAttribute(value = "touser") touser: String,
                 @RequestParam(value = "type") type: String,
                 request: HttpServletRequest
    ): ResponseDataInterface {
        when (type) {
            "text" -> {
                val content = request.getParameter("content")
                        ?: SKException.ServiceErrorEnum.MISSING_REQUEST_PART.data("content").throwout()
                workSenderHelper.sendText(content, workSenderHelper.withSettings.toUser(touser))
            }
            "textcard" -> {
                val title = request.getParameter("title")
                        ?: SKException.ServiceErrorEnum.MISSING_REQUEST_PART.data("title").throwout()
                val description = request.getParameter("description")
                        ?: SKException.ServiceErrorEnum.MISSING_REQUEST_PART.data("description").throwout()
                val url = request.getParameter("url")
                        ?: SKException.ServiceErrorEnum.MISSING_REQUEST_PART.data("url").throwout()
                val btntxt = request.getParameter("btntxt") ?: "详情"
                workSenderHelper.sendTextCard(title, description, url, btntxt, workSenderHelper.withSettings.toUser(touser))
            }
            "markdown" -> {
                val content = request.getParameter("content")
                        ?: SKException.ServiceErrorEnum.MISSING_REQUEST_PART.data("content").throwout()
                workSenderHelper.sendMarkdown(content, workSenderHelper.withSettings.toUser(touser))
            }
            else -> {
                SKException.ServiceErrorEnum.NOT_YET_SUPPORTED.data("Http Post Push Type:$type")
            }
        }
        return SKException.ServiceErrorEnum.NO_ERROR_BACKWARDS_COMPATIBILITY_WARN
    }

    @StatusCount
    @RequestMapping(value = ["/"], method = [RequestMethod.GET])
    fun pushGet(@RequestAttribute(value = "touser") touser: String,
                @RequestParam(value = "type") type: String,
                request: HttpServletRequest
    ): ResponseDataInterface {
        when (type) {
            "text" -> {
                val content = request.getParameter("content")
                        ?: SKException.ServiceErrorEnum.MISSING_REQUEST_PART.data("content").throwout()
                workSenderHelper.sendText(content, workSenderHelper.withSettings.toUser(touser))
            }
            else -> {
                SKException.ServiceErrorEnum.NOT_YET_SUPPORTED.data("Http Get Push Type:$type")
            }
        }
        return SKException.ServiceErrorEnum.NO_ERROR_BACKWARDS_COMPATIBILITY_WARN
    }

    companion object {
        private val logger = LoggerFactory.getLogger(OldPusherController::class.java)
        private var docHtml: String? = null
    }
}