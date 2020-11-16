package com.kairlec.pusher.receiver.reply

import java.io.File
import java.io.InputStream


class VoiceMessage : Message {
    val voiceByteArray: ByteArray
    val toUser: String
    val filename: String

    constructor(inputStream: InputStream, filename: String, toUser: String) {
        val byteArray = inputStream.use {
            it.readAllBytes()
        }
        this.filename = filename
        this.voiceByteArray = byteArray
        this.toUser = toUser
    }

    constructor(voiceByteArray: ByteArray, filename: String, toUser: String) {
        this.filename = filename
        this.voiceByteArray = voiceByteArray
        this.toUser = toUser
    }

    constructor(voiceFile: File, filename: String = voiceFile.name, toUser: String) {
        this.filename = filename
        this.voiceByteArray = voiceFile.readBytes()
        this.toUser = toUser
    }

    override fun contentToString(): String {
        return "[Voice Data:$filename]"
    }

    override fun toString(): String {
        return contentToString()
    }
}