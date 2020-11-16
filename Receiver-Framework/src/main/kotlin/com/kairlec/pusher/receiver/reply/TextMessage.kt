package com.kairlec.pusher.receiver.reply

class TextMessage(val content: String, val toUser: String) : Message {
    override fun contentToString(): String {
        return content
    }

    override fun toString(): String {
        return contentToString()
    }
}