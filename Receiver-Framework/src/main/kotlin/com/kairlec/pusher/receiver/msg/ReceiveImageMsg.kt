package com.kairlec.pusher.receiver.msg

import com.kairlec.pusher.receiver.reply.ReplyService
import org.w3c.dom.Element

/**
 * 接受到的图片消息
 *
 * API: https://work.weixin.qq.com/api/doc/90001/90143/90373#%E5%9B%BE%E7%89%87%E6%B6%88%E6%81%AF
 */
class ReceiveImageMsg private constructor(
        val picUrl: String,
        val mediaID: String,
        replyService: ReplyService,
        baseReceiveMsg: ReceiveMsg
) : ReceiveMsg(replyService, baseReceiveMsg) {

    override fun contentToString(): String {
        return picUrl
    }

    override fun toString(): String {
        return contentToString()
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