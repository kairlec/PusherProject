package com.kairlec.pusher.receiver.msg

import com.kairlec.pusher.receiver.reply.ReplyService
import org.w3c.dom.Element

/**
 * 接受的链接消息
 *
 * API: https://work.weixin.qq.com/api/doc/90001/90143/90373#%E9%93%BE%E6%8E%A5%E6%B6%88%E6%81%AF
 */
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

    override fun toString(): String {
        return contentToString()
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