package com.kairlec.pusher.receiver.msg

import com.kairlec.pusher.receiver.reply.ReplyService
import org.w3c.dom.Element

/**
 * 接受到的文本消息
 *
 * API: https://work.weixin.qq.com/api/doc/90001/90143/90373#%E6%96%87%E6%9C%AC%E6%B6%88%E6%81%AF
 */
open class ReceiveTextMsg(
        val content: String,
        replyService: ReplyService,
        baseReceiveMsg: ReceiveMsg
) : ReceiveMsg(replyService, baseReceiveMsg) {

    override fun contentToString(): String {
        return content
    }

    override fun toString(): String {
        return contentToString()
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