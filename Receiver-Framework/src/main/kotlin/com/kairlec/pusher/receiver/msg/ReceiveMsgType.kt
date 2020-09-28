package com.kairlec.pusher.receiver.msg

import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
enum class ReceiveMsgType(val type: String, val clazz: KClass<ReceiveMsg>) {
    TEXT("text", ReceiveTextMsg::class as KClass<ReceiveMsg>),
    IMAGE("image", ReceiveImageMsg::class as KClass<ReceiveMsg>),
    VOICE("voice", ReceiveVoiceMsg::class as KClass<ReceiveMsg>),
    VIDEO("video", ReceiveVideoMsg::class as KClass<ReceiveMsg>),
    LOCATION("location", ReceiveLocationMsg::class as KClass<ReceiveMsg>),
    LINK("link", ReceiveLinkMsg::class as KClass<ReceiveMsg>),
    UNKNOWN("unknown", ReceiveMsg::class)
    ;

    companion object {
        fun parse(type: String): ReceiveMsgType {
            for (receiveMsgType in values()) {
                if (receiveMsgType.type == type) {
                    return receiveMsgType
                }
            }
            return UNKNOWN
        }
    }
}