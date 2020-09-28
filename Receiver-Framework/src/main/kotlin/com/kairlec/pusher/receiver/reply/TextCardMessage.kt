package com.kairlec.pusher.receiver.reply

class TextCardMessage(
        val title: String,
        val description: String,
        val url: String,
        val btntxt: String? = null,
        val toUser: String
) : Message