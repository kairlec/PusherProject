package com.kairlec.pusher.receiver.reply

interface Message {
    fun contentToString(): String
}