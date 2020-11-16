@file:Suppress("unused", "SpellCheckingInspection")

package com.kairlec.pusher.receiver.dsl

import com.kairlec.pusher.receiver.msg.ReceiveMsg
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

/**
 * DSL接收器
 */
open class ReceiveDSL {
    private val channel: Channel<ReceiveMsg> = Channel(Channel.UNLIMITED)
    private val listenerList: MutableList<Listener> = ArrayList()
    private val logger = LoggerFactory.getLogger(ReceiveDSL::class.java)

    init {
        GlobalScope.launch {
            while (true) {
                try {
                    val msg = channel.receive()
                    launch {
                        @Suppress("DeferredResultUnused")
                        async(Dispatchers.IO) {
                            for (listener in listenerList) {
                                try {
                                    if (listener.e == ReceiveMsg::class) {
                                        listener.onEvent(msg, msg)
                                    } else if (msg::class == listener.e) {
                                        listener.onEvent(msg, msg)
                                    }
                                } catch (e: Throwable) {
                                    logger.error("listener event invoke error:${e.message}", e)
                                }
                            }
                        }
                    }.start()
                } catch (e: Exception) {
                    logger.error("listener job error", e)
                }
            }
        }
    }

    /**
     * 注册监听器
     */
    fun addListener(listener: Listener) {
        if (logger.isDebugEnabled) {
            logger.debug("Add listener:${listener.name}")
        }
        listenerList.add(listener)
    }

    /**
     * 移除监听器
     */
    fun removeListener(listener: Listener) {
        listenerList.remove(listener)
    }

    /**
     * 向监听池发送消息
     */
    fun send(msg: ReceiveMsg) {
        GlobalScope.launch {
            channel.send(msg)
        }
    }
}
