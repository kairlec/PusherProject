package com.kairlec.pusher.receiver.dsl

/**
 * DSL 标记. 将能让 IDE 阻止一些错误的方法调用.
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS, AnnotationTarget.TYPE)
@DslMarker
internal annotation class MessageDsl