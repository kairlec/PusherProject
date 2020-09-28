package com.kairlec.pusher.receiver.msg

import com.kairlec.pusher.receiver.CommandHelper
import com.kairlec.pusher.receiver.reply.Message
import com.kairlec.pusher.receiver.reply.ReplyService
import com.kairlec.pusher.receiver.reply.TextMessage
import org.w3c.dom.Element
import org.xml.sax.InputSource
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory

open class ReceiveMsg(
        open val replyService: ReplyService,
        open val agentID: Long,
        open val createTime: Long,
        open val fromUserName: String,
        open val msgId: Long,
        open val msgType: ReceiveMsgType,
        open val toUserName: String
) {
    private var closed: Boolean = false

    fun closeReply() {
        closed = true
    }

    fun reopenReply() {
        closed = false
    }

    var tagObject: Any? = null

    fun closeStatus() = closed

    open fun contentToString(): String {
        return "$fromUserName:[$msgType]"
    }

    constructor(replyService: ReplyService, receiveMsg: ReceiveMsg) :
            this(replyService,
                    receiveMsg.agentID,
                    receiveMsg.createTime,
                    receiveMsg.fromUserName,
                    receiveMsg.msgId,
                    receiveMsg.msgType,
                    receiveMsg.toUserName
            )

    companion object {
        fun parse(element: Element, replyService: ReplyService): ReceiveMsg? {
            val toUsername = element["ToUserName"] ?: return null
            val fromUserName = element["FromUserName"] ?: return null
            val createTime = element["CreateTime"]?.toLong() ?: return null
            val msgType = element["MsgType"]?.let { ReceiveMsgType.parse(it) } ?: return null
            val msgId = element["MsgId"]?.toLong() ?: return null
            val agentID = element["AgentID"]?.toLong() ?: return null
            val base = ReceiveMsg(replyService, agentID, createTime, fromUserName, msgId, msgType, toUsername)
            return when (msgType) {
                ReceiveMsgType.TEXT -> ReceiveTextMsg.parse(element, base, replyService)
                ReceiveMsgType.IMAGE -> ReceiveImageMsg.parse(element, replyService, base)
                ReceiveMsgType.VOICE -> ReceiveVoiceMsg.parse(element, replyService, base)
                ReceiveMsgType.VIDEO -> ReceiveVideoMsg.parse(element, replyService, base)
                ReceiveMsgType.LOCATION -> ReceiveLocationMsg.parse(element, replyService, base)
                ReceiveMsgType.LINK -> ReceiveLinkMsg.parse(element, replyService, base)
                ReceiveMsgType.UNKNOWN -> base
            }
        }

        fun parse(rawString: String): Element {
            val documentBuilderFactory = DocumentBuilderFactory.newInstance()
            val documentBuilder = documentBuilderFactory.newDocumentBuilder()
            StringReader(rawString).use {
                val inputSource = InputSource(it)
                val document = documentBuilder.parse(inputSource)
                return document.documentElement
            }
        }

        fun parse(rawString: String, replyService: ReplyService): ReceiveMsg? {
            return parse(parse(rawString), replyService)
        }
    }

    /**
     * 给这个消息事件的主体发送消息
     */
    @JvmSynthetic
    internal fun CommandHelper.directReply(message: Message) = replyService.reply(message)


    @JvmSynthetic
    internal fun CommandHelper.directReply(plain: String) = replyService.reply(TextMessage(plain, fromUserName))

}