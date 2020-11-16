package com.kairlec.pusher.receiver.dsl

import com.kairlec.pusher.receiver.msg.ReceiveMsg
import kotlin.reflect.KClass

/**
 * 事件监听器,需要从企业微信获取发送消息的事件的时候,必须注册一个监听器向监听池内添加
 * @param e 要监听的消息类型,必须要是[ReceiveMsg]或其子类
 * @param name 监听器名称
 * @param onEvent 监听事件
 */
@Suppress("UNCHECKED_CAST")
class Listener internal constructor(
        val e: KClass<ReceiveMsg>,
        val name: String = "Default Listener",
        val onEvent: ReceiveMsg.(ReceiveMsg) -> Unit
) {
    companion object {
        internal inline fun <reified E : ReceiveMsg> createListener(name: String = "Default Listener", noinline onEvent: E.(E) -> Unit): Listener {
            return Listener(E::class as KClass<ReceiveMsg>, name, onEvent as ReceiveMsg.(ReceiveMsg) -> Unit)
        }

        internal fun <E : ReceiveMsg> createListener(name: String = "Default Listener", e: KClass<E>, onEvent: E.(E) -> Unit): Listener {
            return Listener(e as KClass<ReceiveMsg>, name, onEvent as ReceiveMsg.(ReceiveMsg) -> Unit)
        }
    }
}