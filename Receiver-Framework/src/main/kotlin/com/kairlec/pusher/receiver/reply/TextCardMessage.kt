package com.kairlec.pusher.receiver.reply

class TextCardMessage(
        val title: String,
        val description: String,
        val url: String,
        val btntxt: String? = null,
        val toUser: String
) : Message {

    override fun contentToString(): String {
        return description
    }

    override fun toString(): String {
        return contentToString()
    }
}