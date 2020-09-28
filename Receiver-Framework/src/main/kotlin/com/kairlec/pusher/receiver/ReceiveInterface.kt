package com.kairlec.pusher.receiver

import com.kairlec.pusher.receiver.dsl.ReceiveDSL
import com.kairlec.pusher.receiver.msg.ReceiveMsg
import org.slf4j.LoggerFactory

interface ReceiveInterface {
    fun onReceive(msg: ReceiveMsg): ReplyMsg {
        val logger = LoggerFactory.getLogger(ReceiveInterface::class.java)
        logger.debug("Default Receive Interface receive msgType:[${msg.msgType}] from `${msg.fromUserName}`")
        return ReplyMsg.replyText(msg, "收到:${msg.contentToString()}")
    }

    fun onReceiveDSL(dsl: ReceiveDSL) {
    }

}