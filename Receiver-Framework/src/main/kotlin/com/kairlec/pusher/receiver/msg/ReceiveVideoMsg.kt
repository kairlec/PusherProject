package com.kairlec.pusher.receiver.msg

import com.kairlec.pusher.receiver.reply.ReplyService
import org.w3c.dom.Element

/**
 * 接受到的视频消息
 *
 * API: https://work.weixin.qq.com/api/doc/90001/90143/90373#%E8%A7%86%E9%A2%91%E6%B6%88%E6%81%AF
 */
class ReceiveVideoMsg private constructor(
        val thumbMediaId: String,
        val mediaID: String,
        replyService: ReplyService,
        baseReceiveMsg: ReceiveMsg
) : ReceiveMsg(replyService, baseReceiveMsg) {

    override fun contentToString(): String {
        return "[Video Data]"
    }

    override fun toString(): String {
        return contentToString()
    }

    companion object {
        fun parse(element: Element, replyService: ReplyService): ReceiveVideoMsg? {
            val baseReceiveMsg = ReceiveMsg.parse(element, replyService) ?: return null
            return parse(element, replyService, baseReceiveMsg)
        }

        fun parse(element: Element, replyService: ReplyService, baseReceiveMsg: ReceiveMsg): ReceiveVideoMsg? {
            val thumbMediaId = element["ThumbMediaId"] ?: return null
            val mediaID = element["MediaId"] ?: return null
            return ReceiveVideoMsg(thumbMediaId, mediaID, replyService, baseReceiveMsg)
        }
    }
}