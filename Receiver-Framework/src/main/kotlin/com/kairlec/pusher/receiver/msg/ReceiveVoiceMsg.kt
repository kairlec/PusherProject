package com.kairlec.pusher.receiver.msg

import com.kairlec.pusher.receiver.reply.ReplyService
import org.w3c.dom.Element


class ReceiveVoiceMsg private constructor(
        val format: String,
        val mediaID: String,
        replyService: ReplyService,
        baseReceiveMsg: ReceiveMsg
) : ReceiveMsg(replyService, baseReceiveMsg) {


    override fun contentToString(): String {
        return "[Voice Data]"
    }

    companion object {
        fun parse(element: Element, replyService: ReplyService): ReceiveVoiceMsg? {
            val baseReceiveMsg = ReceiveMsg.parse(element, replyService) ?: return null
            return parse(element, replyService, baseReceiveMsg)
        }

        fun parse(element: Element, replyService: ReplyService, baseReceiveMsg: ReceiveMsg): ReceiveVoiceMsg? {
            val format = element["Format"] ?: return null
            val mediaID = element["MediaId"] ?: return null
            return ReceiveVoiceMsg(format, mediaID, replyService, baseReceiveMsg)
        }
    }
}