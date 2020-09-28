package com.kairlec.pusher.receiver.msg

import com.kairlec.pusher.receiver.reply.ReplyService
import org.w3c.dom.Element

class ReceiveLocationMsg private constructor(
        val locationX: Double,
        val locationY: Double,
        val scale: Int,
        val label: String,
        val appType: String,
        replyService: ReplyService,
        baseReceiveMsg: ReceiveMsg
) : ReceiveMsg(replyService,baseReceiveMsg) {

    override fun contentToString(): String {
        return "$label ($locationX,$locationY)[x$scale]"
    }

    companion object {
        fun parse(element: Element,replyService: ReplyService): ReceiveLocationMsg? {
            val baseReceiveMsg = ReceiveMsg.parse(element, replyService) ?: return null
            return parse(element, replyService, baseReceiveMsg)
        }

        fun parse(element: Element,replyService: ReplyService, baseReceiveMsg: ReceiveMsg): ReceiveLocationMsg? {
            val locationX = element["Location_X"]?.toDoubleOrNull() ?: return null
            val locationY = element["Location_Y"]?.toDoubleOrNull() ?: return null
            val scale = element["Scale"]?.toIntOrNull() ?: return null
            val label = element["Label"] ?: return null
            val appType = element["AppType"] ?: return null
            return ReceiveLocationMsg(locationX, locationY, scale, label, appType,replyService, baseReceiveMsg)
        }
    }
}