package com.kairlec.pusher.receiver.reply

class MpNewsMessage(
        val title: String,
        val thumbMediaID: String,
        val content: String,
        val author: String? = null,
        val contentSourceUrl: String? = null,
        val digest: String? = null,
        val toUser: String
) : Message