@file:Suppress("unused", "SpellCheckingInspection")

package com.kairlec.pusher.receiver.dsl

import com.kairlec.pusher.receiver.msg.ReceiveMsg
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory


open class ReceiveDSL {
    private val channel: Channel<ReceiveMsg> = Channel(Channel.UNLIMITED)
    private val listenerList: MutableList<Listener> = ArrayList()
    private val logger = LoggerFactory.getLogger(ReceiveDSL::class.java)

    init {
        GlobalScope.launch {
            while (true) {
                try {
                    val msg = channel.receive()
                    for (listener in listenerList) {
                        if (listener.e == ReceiveMsg::class) {
                            listener.onEvent(msg, msg)
                        } else if (msg::class == listener.e) {
                            listener.onEvent(msg, msg)
                        }
                    }
                } catch (e: Exception) {
                    logger.error("listener error", e)
                }
            }
        }
    }

    fun addListener(listener: Listener) {
        listenerList.add(listener)
    }

    fun removeListener(listener: Listener) {
        listenerList.remove(listener)
    }

    fun send(msg: ReceiveMsg) {
        runBlocking {
            channel.send(msg)
        }
    }
}
