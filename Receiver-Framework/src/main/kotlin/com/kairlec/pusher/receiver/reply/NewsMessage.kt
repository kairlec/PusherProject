package com.kairlec.pusher.receiver.reply

class NewsMessage(
        val title: String,
        val url: String,
        val description: String? = null,
        val picurl: String? = null,
        val toUser: String
):Message