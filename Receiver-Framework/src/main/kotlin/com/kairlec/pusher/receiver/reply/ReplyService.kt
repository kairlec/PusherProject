package com.kairlec.pusher.receiver.reply

interface ReplyService {
    fun reply(message: Message)
}