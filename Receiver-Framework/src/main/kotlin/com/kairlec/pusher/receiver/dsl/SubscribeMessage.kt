@file:JvmMultifileClass
@file:JvmName("SubscribeMessagesKt")
@file:Suppress("EXPERIMENTAL_API_USAGE", "MemberVisibilityCanBePrivate", "unused")

package com.kairlec.pusher.receiver.dsl


import com.kairlec.pusher.receiver.msg.*
import kotlin.reflect.KClass

typealias MessagePacketSubscribersBuilder = MessageSubscribersBuilder<ReceiveMsg, Listener, Unit, Unit>


fun <R> ReceiveDSL.subscribeMessages(
        listeners: MessagePacketSubscribersBuilder.() -> R
): R {
    return MessagePacketSubscribersBuilder(Unit) { filter, messageListener: MessageListener<ReceiveMsg, Unit> ->
        // subscribeAlways 即注册一个监听器. 这个监听器收到消息后就传递给 [messageListener]
        // messageListener 即为 DSL 里 `contains(...) { }`, `startsWith(...) { }` 的代码块.
        subscribeAlways<ReceiveMsg> {
            // this.message.contentToString() 即为 messageListener 中 it 接收到的值
            val toString = this.contentToString()
            if (filter(this, toString))
                messageListener(this, toString)
        }
    }.run(listeners)
}

typealias TextMessageSubscribersBuilder = MessageSubscribersBuilder<ReceiveTextMsg, Listener, Unit, Unit>


fun <R> ReceiveDSL.subscribeTextMessages(
        listeners: TextMessageSubscribersBuilder.() -> R
): R {
    return TextMessageSubscribersBuilder(Unit) { filter, listener ->
        subscribeAlways<ReceiveTextMsg> {
            val toString = this.contentToString()
            if (filter(this, toString))
                listener(this, toString)
        }
    }.run(listeners)
}

typealias ImageMessageSubscribersBuilder = MessageSubscribersBuilder<ReceiveImageMsg, Listener, Unit, Unit>


fun <R> ReceiveDSL.subscribeImageMessages(
        listeners: ImageMessageSubscribersBuilder.() -> R
): R {
    return ImageMessageSubscribersBuilder(Unit) { filter, listener ->
        subscribeAlways<ReceiveImageMsg> {
            val toString = this.contentToString()
            if (filter(this, toString))
                listener(this, toString)
        }
    }.run(listeners)
}

typealias LinkMessageSubscribersBuilder = MessageSubscribersBuilder<ReceiveLinkMsg, Listener, Unit, Unit>


fun <R> ReceiveDSL.subscribeLinkMessages(
        listeners: LinkMessageSubscribersBuilder.() -> R
): R {
    return LinkMessageSubscribersBuilder(Unit) { filter, listener ->
        subscribeAlways<ReceiveLinkMsg> {
            val toString = this.contentToString()
            if (filter(this, toString))
                listener(this, toString)
        }
    }.run(listeners)
}


typealias LocationMessageSubscribersBuilder = MessageSubscribersBuilder<ReceiveLocationMsg, Listener, Unit, Unit>


fun <R> ReceiveDSL.subscribeLocationMessages(
        listeners: LocationMessageSubscribersBuilder.() -> R
): R {
    return LocationMessageSubscribersBuilder(Unit) { filter, listener ->
        subscribeAlways<ReceiveLocationMsg> {
            val toString = this.contentToString()
            if (filter(this, toString))
                listener(this, toString)
        }
    }.run(listeners)
}


typealias VideoMessageSubscribersBuilder = MessageSubscribersBuilder<ReceiveVideoMsg, Listener, Unit, Unit>


fun <R> ReceiveDSL.subscribeVideoMessages(
        listeners: VideoMessageSubscribersBuilder.() -> R
): R {
    return VideoMessageSubscribersBuilder(Unit) { filter, listener ->
        subscribeAlways<ReceiveVideoMsg> {
            val toString = this.contentToString()
            if (filter(this, toString))
                listener(this, toString)
        }
    }.run(listeners)
}


typealias VoiceMessageSubscribersBuilder = MessageSubscribersBuilder<ReceiveVoiceMsg, Listener, Unit, Unit>

fun <R> ReceiveDSL.subscribeVoiceMessages(
        listeners: VoiceMessageSubscribersBuilder.() -> R
): R {
    return VoiceMessageSubscribersBuilder(Unit) { filter, listener ->
        subscribeAlways<ReceiveVoiceMsg> {
            val toString = this.contentToString()
            if (filter(this, toString))
                listener(this, toString)
        }
    }.run(listeners)
}

inline fun <reified E : ReceiveMsg> ReceiveDSL.subscribeAlways(
        noinline handler: E.(E) -> Unit
): Listener = subscribeAlways(E::class, handler)


fun <E : ReceiveMsg> ReceiveDSL.subscribeAlways(
        eventClass: KClass<out E>,
        handler: E.(E) -> Unit
): Listener {
    return Listener.createListener(eventClass, handler).apply {
        this@subscribeAlways.addListener(this)
    }
}

