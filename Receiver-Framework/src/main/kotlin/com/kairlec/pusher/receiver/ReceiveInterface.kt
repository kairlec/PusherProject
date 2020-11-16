package com.kairlec.pusher.receiver

import com.kairlec.pusher.receiver.dsl.ReceiveDSL
import com.kairlec.pusher.receiver.msg.ReceiveMsg
import org.slf4j.LoggerFactory

/**
 * 消息接收器接口
 */
interface ReceiveInterface {
    /**
     * 是否启用DSL接收器
     */
    val enableDSL: Boolean

    /**
     * DSL接收器(若启用DSL,则应当创建一个DSL,然后在[onReceiveDSL]中进行调用)
     */
    var receiveDSL: ReceiveDSL?

    /**
     * 传统接受事件
     */
    fun onReceive(msg: ReceiveMsg): ReplyMsg {
        val logger = LoggerFactory.getLogger(ReceiveInterface::class.java)
        logger.debug("Default Receive Interface receive msgType:[${msg.msgType}] from `${msg.fromUserName}`")
        return ReplyMsg.replyText(msg, "收到:${msg.contentToString()}")
    }

    /**
     * DSL接收事件
     */
    fun onReceiveDSL() {
    }

}