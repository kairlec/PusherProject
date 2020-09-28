package com.kairlec.pusher.receiver.msg

import com.kairlec.pusher.receiver.reply.ReplyService
import org.w3c.dom.Element

open class ReceiveTextMsg(
        val content: String,
        replyService: ReplyService,
        baseReceiveMsg: ReceiveMsg
) : ReceiveMsg(replyService, baseReceiveMsg) {

    override fun contentToString(): String {
        return content
    }

    companion object {
        fun parse(element: Element, replyService: ReplyService): ReceiveTextMsg? {
            val baseReceiveMsg = ReceiveMsg.parse(element, replyService) ?: return null
            return parse(element, baseReceiveMsg, replyService)
        }

        fun parse(element: Element, baseReceiveMsg: ReceiveMsg, replyService: ReplyService): ReceiveTextMsg? {
            val content = element["Content"] ?: return null
            return ReceiveTextMsg(content, replyService, baseReceiveMsg)
        }
    }



}