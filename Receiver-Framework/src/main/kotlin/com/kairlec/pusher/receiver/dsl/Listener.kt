package com.kairlec.pusher.receiver.dsl

import com.kairlec.pusher.receiver.msg.ReceiveMsg
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
class Listener internal constructor(
        val e: KClass<ReceiveMsg>,
        val onEvent: ReceiveMsg.(ReceiveMsg) -> Unit
) {
    companion object {
        internal inline fun <reified E : ReceiveMsg> createListener(noinline onEvent: E.(E) -> Unit): Listener {
            return Listener(E::class as KClass<ReceiveMsg>, onEvent as ReceiveMsg.(ReceiveMsg) -> Unit)
        }

        internal fun <E : ReceiveMsg> createListener(e: KClass<E>, onEvent: E.(E) -> Unit): Listener {
            return Listener(e as KClass<ReceiveMsg>, onEvent as ReceiveMsg.(ReceiveMsg) -> Unit)
        }
    }
}