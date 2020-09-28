package com.kairlec.pusher.receiver

import com.kairlec.pusher.receiver.msg.ReceiveMsg

class ReplyMsg private constructor(
        val raw: String
) {
    companion object {
        /**
         * 回复消息
         * @param msg 回复的消息体
         * @param content 回复的消息内容
         * @return 构造的回复消息内容
         */
        fun replyText(msg: ReceiveMsg, content: String): ReplyMsg {
            return ReplyMsg("""
                            <xml>
                                <ToUserName><![CDATA[${msg.fromUserName}]]></ToUserName>
                                <FromUserName><![CDATA[${msg.toUserName}]]></FromUserName>
                                <CreateTime>${msg.createTime}</CreateTime>
                                <MsgType><![CDATA[${msg.msgType}]]></MsgType>
                                <Content><![CDATA[$content]]></Content>
                                <MsgId>${msg.msgId}</MsgId>
                                <AgentID>${msg.agentID}</AgentID>
                            </xml>
                            """.trimIndent())
        }
    }
}

