package com.kairlec.pusher.receiver.msg

import com.kairlec.pusher.receiver.reply.ReplyService
import org.w3c.dom.Element

class ReceiveLinkMsg private constructor(
        val title: String,
        val description: String,
        val url: String,
        val picUrl: String,
        replyService: ReplyService,
        baseReceiveMsg: ReceiveMsg
) : ReceiveMsg(replyService, baseReceiveMsg) {

    override fun contentToString(): String {
        return url
    }

    companion object {
        fun parse(element: Element, replyService: ReplyService): ReceiveLinkMsg? {
            val baseReceiveMsg = ReceiveMsg.parse(element, replyService) ?: return null
            return parse(element, replyService, baseReceiveMsg)
        }

        fun parse(element: Element, replyService: ReplyService, baseReceiveMsg: ReceiveMsg): ReceiveLinkMsg? {
            val title = element["Title"] ?: return null
            val description = element["Description"] ?: return null
            val url = element["Url"] ?: return null
            val picUrl = element["PicUrl"] ?: return null
            return ReceiveLinkMsg(title, description, url, picUrl, replyService, baseReceiveMsg)
        }
    }
}