package com.kairlec.pusher.receiver.reply

import java.io.File
import java.io.InputStream


class FileMessage :Message{
    val fileByteArray: ByteArray
    val toUser: String
    val filename: String

    constructor(inputStream: InputStream, filename: String, toUser: String) {
        val byteArray = inputStream.use {
            it.readAllBytes()
        }
        this.filename = filename
        this.fileByteArray = byteArray
        this.toUser = toUser
    }

    constructor(fileByteArray: ByteArray, filename: String, toUser: String) {
        this.filename = filename
        this.fileByteArray = fileByteArray
        this.toUser = toUser
    }

    constructor(fileFile: File, filename: String = fileFile.name, toUser: String) {
        this.filename = filename
        this.fileByteArray = fileFile.readBytes()
        this.toUser = toUser
    }
}