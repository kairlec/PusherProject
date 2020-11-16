package com.kairlec.pusher.openapi

import com.kairlec.pusher.openapi.context.MessageContext
import com.kairlec.pusher.openapi.context.SendStatusContext
import com.kairlec.pusher.receiver.ReplyMsg
import org.springframework.context.ApplicationContext

/**
 * 事件类,插件需要实现此接口,以便于接受事件
 */
interface Event {

    /**
     * 事件被创建时初始化调用
     * @param api [API] 创建时提供的API接口
     */
    fun onCreated(api: API)

    /**
     * 应用启动时调用
     * @param context [ApplicationContext] Spring应用上下文
     */
    fun onStartup(context: ApplicationContext) {}

    /**
     * 应用要发送状态消息时
     * @param sendStatusContext [SendStatusContext] 状态消息上下文
     */
    fun onSendStatus(sendStatusContext: SendStatusContext) {}

    /**
     * 应用收到状态消息时
     * @param messageContext [MessageContext] 消息上下文
     */
    fun onReceiveMessage(messageContext: MessageContext): ReplyMsg? {
        return null
    }

}