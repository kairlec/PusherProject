package com.kairlec.pusher.receiver.reply

import java.io.File
import java.io.InputStream


class VideoMessage : Message {
    val videoByteArray: ByteArray
    val toUser: String
    val filename: String

    constructor(inputStream: InputStream, filename: String, toUser: String) {
        val byteArray = inputStream.use {
            it.readAllBytes()
        }
        this.filename = filename
        this.videoByteArray = byteArray
        this.toUser = toUser
    }

    constructor(videoByteArray: ByteArray, filename: String, toUser: String) {
        this.filename = filename
        this.videoByteArray = videoByteArray
        this.toUser = toUser
    }

    constructor(videoFile: File, filename: String = videoFile.name, toUser: String) {
        this.filename = filename
        this.videoByteArray = videoFile.readBytes()
        this.toUser = toUser
    }

    override fun contentToString(): String {
        return "[Video Data:$filename]"
    }

    override fun toString(): String {
        return contentToString()
    }
}