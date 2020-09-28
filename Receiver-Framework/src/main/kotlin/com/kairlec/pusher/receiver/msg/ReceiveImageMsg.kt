package com.kairlec.pusher.receiver.msg

import com.kairlec.pusher.receiver.reply.ReplyService
import org.w3c.dom.Element

class ReceiveImageMsg private constructor(
        val picUrl: String,
        val mediaID: String,
        replyService: ReplyService,
        baseReceiveMsg: ReceiveMsg
) : ReceiveMsg(replyService, baseReceiveMsg) {

    override fun contentToString(): String {
        return picUrl
    }

    companion object {
        fun parse(element: Element, replyService: ReplyService): ReceiveImageMsg? {
            val baseReceiveMsg = ReceiveMsg.parse(element, replyService) ?: return null
            return parse(element, replyService, baseReceiveMsg)
        }

        fun parse(element: Element, replyService: ReplyService, baseReceiveMsg: ReceiveMsg): ReceiveImageMsg? {
            val picUrl = element["PicUrl"] ?: return null
            val mediaID = element["MediaId"] ?: return null
            return ReceiveImageMsg(picUrl, mediaID, replyService, baseReceiveMsg)
        }
    }
}