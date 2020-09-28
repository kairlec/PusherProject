package com.kairlec.pusher.receiver.dsl

import com.kairlec.pusher.receiver.msg.ReceiveMsg


/*
 * 将 internal 移出 MessageSubscribersBuilder.kt 以减小其体积
 */


@MessageDsl
internal fun <M : ReceiveMsg, Ret, R : RR, RR> MessageSubscribersBuilder<M, Ret, R, RR>.content(
        filter: M.(String) -> Boolean,
        onEvent: MessageListener<M, RR>
): Ret = subscriber(filter) { onEvent(this, it) }


internal fun <M : ReceiveMsg, Ret, R : RR, RR> MessageSubscribersBuilder<M, Ret, R, RR>.endsWithImpl(
        suffix: String,
        removeSuffix: Boolean = true,
        trim: Boolean = true,
        onEvent: @MessageDsl M.(String) -> R
): Ret {
    return if (trim) {
        val toCheck = suffix.trim()
        content({ it.trimEnd().endsWith(toCheck) }) {
            if (removeSuffix) this.onEvent(this.contentToString().removeSuffix(toCheck).trim())
            else onEvent(this, this.contentToString().trim())
        }
    } else {
        content({ it.endsWith(suffix) }) {
            if (removeSuffix) this.onEvent(this.contentToString().removeSuffix(suffix))
            else onEvent(this, this.contentToString())
        }
    }
}

internal fun <M : ReceiveMsg, Ret, R : RR, RR> MessageSubscribersBuilder<M, Ret, R, RR>.startsWithImpl(
        prefix: String,
        removePrefix: Boolean = true,
        trim: Boolean = true,
        onEvent: @MessageDsl M.(String) -> R
): Ret {
    return if (trim) {
        val toCheck = prefix.trim()
        content({ it.trimStart().startsWith(toCheck) }) {
            if (removePrefix) this.onEvent(this.contentToString().substringAfter(toCheck).trim())
            else onEvent(this, this.contentToString().trim())
        }
    } else content({ it.startsWith(prefix) }) {
        if (removePrefix) this.onEvent(this.contentToString().removePrefix(prefix))
        else onEvent(this, this.contentToString())
    }
}

internal fun <M : ReceiveMsg, Ret, R : RR, RR> MessageSubscribersBuilder<M, Ret, R, RR>.containsAllImpl(
        sub: Array<out String>,
        ignoreCase: Boolean = false,
        trim: Boolean = true
): MessageSubscribersBuilder<M, Ret, R, RR>.ListeningFilter =
        if (trim) {
            val list = sub.map { it.trim() }
            content { list.all { toCheck -> it.contains(toCheck, ignoreCase = ignoreCase) } }
        } else {
            content { sub.all { toCheck -> it.contains(toCheck, ignoreCase = ignoreCase) } }
        }

internal fun <M : ReceiveMsg, Ret, R : RR, RR> MessageSubscribersBuilder<M, Ret, R, RR>.containsAnyImpl(
        vararg sub: String,
        ignoreCase: Boolean = false,
        trim: Boolean = true
): MessageSubscribersBuilder<M, Ret, R, RR>.ListeningFilter =
        if (trim) {
            val list = sub.map { it.trim() }
            content { list.any { toCheck -> it.contains(toCheck, ignoreCase = ignoreCase) } }
        } else content { sub.any { toCheck -> it.contains(toCheck, ignoreCase = ignoreCase) } }

internal fun <M : ReceiveMsg, Ret, R : RR, RR> MessageSubscribersBuilder<M, Ret, R, RR>.caseImpl(
        equals: String,
        ignoreCase: Boolean = false,
        trim: Boolean = true
): MessageSubscribersBuilder<M, Ret, R, RR>.ListeningFilter {
    return if (trim) {
        val toCheck = equals.trim()
        content { it.trim().equals(toCheck, ignoreCase = ignoreCase) }
    } else {
        content { it.equals(equals, ignoreCase = ignoreCase) }
    }
}

internal fun <M : ReceiveMsg, Ret, R : RR, RR> MessageSubscribersBuilder<M, Ret, R, RR>.containsImpl(
        sub: String,
        ignoreCase: Boolean = false,
        trim: Boolean = true,
        onEvent: MessageListener<M, R>
): Ret {
    return if (trim) {
        val toCheck = sub.trim()
        content({ it.contains(toCheck, ignoreCase = ignoreCase) }) {
            onEvent(this, this.contentToString().trim())
        }
    } else {
        content({ it.contains(sub, ignoreCase = ignoreCase) }) {
            onEvent(this, this.contentToString())
        }
    }
}