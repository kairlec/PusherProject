package com.kairlec.pusher.controller

import com.kairlec.error.SKException
import com.kairlec.intf.ResponseDataInterface
import com.kairlec.pusher.annotation.StatusCount
import com.kairlec.pusher.core.wework.WeWorkSenderHelper
import com.kairlec.pusher.pojo.wework.MediaTypeEnum
import com.kairlec.utils.ResponseDataUtil.responseOK
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import javax.servlet.http.HttpServletRequest

/**
 * 发送消息Controller
 */
@RestController
@RequestMapping(value = ["/push"], produces = ["application/json"])
class PusherController {

    @Autowired
    private lateinit var workSenderHelper: WeWorkSenderHelper

    /**
     * 获取推送文档
     * @return 推送文档内容
     */
    @RequestMapping(value = ["/doc"], produces = ["text/html"])
    fun doc(request: HttpServletRequest): String {
        val url = request.requestURL
        return docHtml?.replace("_SERVER_BASE_URL_", url.substring(0, url.length - 4)) ?: run {
            PusherController::class.java.classLoader.getResourceAsStream("templates/doc.html")?.reader()?.use {
                docHtml = it.readText()
                docHtml!!.replace("_SERVER_BASE_URL_", url.substring(0, url.length - 4))
            } ?: throw RuntimeException("Cannot load doc.html")
        }
    }

    /**
     * 推送文本消息
     * @param touser 要推送到的人
     * @param content 要推送的内容
     */
    @StatusCount
    @RequestMapping(value = ["/text"])
    fun text(
            @RequestAttribute(value = "touser") touser: String,
            @RequestParam(value = "content") content: String
    ): ResponseDataInterface {
        workSenderHelper.sendText(content, workSenderHelper.withSettings.toUser(touser))
        return null.responseOK
    }

    /**
     * 推送文本卡片
     */
    @StatusCount
    @RequestMapping(value = ["/textcard"], method = [RequestMethod.POST])
    fun textcard(@RequestAttribute(value = "touser") touser: String,
                 @RequestParam(value = "title") title: String,
                 @RequestParam(value = "description") description: String,
                 @RequestParam(value = "url") url: String,
                 @RequestParam(value = "btntxt", required = false, defaultValue = "详情") btntxt: String
    ): ResponseDataInterface {
        workSenderHelper.sendTextCard(title, description, url, btntxt, workSenderHelper.withSettings.toUser(touser))
        return null.responseOK
    }

    @StatusCount
    @RequestMapping(value = ["/markdown"], method = [RequestMethod.POST])
    fun markdown(@RequestAttribute(value = "touser") touser: String,
                 @RequestParam(value = "content") content: String
    ): ResponseDataInterface {
        workSenderHelper.sendMarkdown(content, workSenderHelper.withSettings.toUser(touser))
        return null.responseOK
    }

    @StatusCount
    @RequestMapping(value = ["/image"], method = [RequestMethod.POST])
    fun image(@RequestAttribute(value = "touser") touser: String,
              @RequestParam("file") file: MultipartFile
    ): ResponseDataInterface {
        if (file.isEmpty) {
            SKException.ServiceErrorEnum.EMPTY_DATA.throwout()
        }
        val filename = file.originalFilename ?: "image"
        val mediaID = workSenderHelper.uploadMedia(file.bytes, filename, MediaTypeEnum.IMAGE)
        workSenderHelper.sendImage(mediaID.mediaID, workSenderHelper.withSettings.toUser(touser))
        return null.responseOK
    }

    @StatusCount
    @RequestMapping(value = ["/voice"], method = [RequestMethod.POST])
    fun voice(@RequestAttribute(value = "touser") touser: String,
              @RequestParam("file") file: MultipartFile
    ): ResponseDataInterface {
        if (file.isEmpty) {
            SKException.ServiceErrorEnum.EMPTY_DATA.throwout()
        }
        val filename = file.originalFilename ?: "voice"
        val mediaID = workSenderHelper.uploadMedia(file.bytes, filename, MediaTypeEnum.VOICE)
        workSenderHelper.sendVoice(mediaID.mediaID, workSenderHelper.withSettings.toUser(touser))
        return null.responseOK
    }

    @StatusCount
    @RequestMapping(value = ["/video"], method = [RequestMethod.POST])
    fun video(@RequestAttribute(value = "touser") touser: String,
              @RequestParam(value = "title", required = false) title: String?,
              @RequestParam(value = "description", required = false) description: String?,
              @RequestParam("file") file: MultipartFile
    ): ResponseDataInterface {
        if (file.isEmpty) {
            SKException.ServiceErrorEnum.EMPTY_DATA.throwout()
        }
        val filename = file.originalFilename ?: "video"
        val mediaID = workSenderHelper.uploadMedia(file.bytes, filename, MediaTypeEnum.VIDEO)
        workSenderHelper.sendVideo(mediaID.mediaID, title, description, workSenderHelper.withSettings.toUser(touser))
        return null.responseOK
    }

    @StatusCount
    @RequestMapping(value = ["/file"], method = [RequestMethod.POST])
    fun file(@RequestAttribute(value = "touser") touser: String,
             @RequestParam("file") file: MultipartFile
    ): ResponseDataInterface {
        if (file.isEmpty) {
            SKException.ServiceErrorEnum.EMPTY_DATA.throwout()
        }
        val filename = file.originalFilename ?: "file"
        val mediaID = workSenderHelper.uploadMedia(file.bytes, filename, MediaTypeEnum.FILE)
        workSenderHelper.sendFile(mediaID.mediaID, workSenderHelper.withSettings.toUser(touser))
        return null.responseOK
    }


    companion object {
        private var docHtml: String? = null
    }
}
