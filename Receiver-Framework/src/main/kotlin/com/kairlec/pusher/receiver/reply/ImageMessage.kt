package com.kairlec.pusher.receiver.reply

import java.io.File
import java.io.InputStream

class ImageMessage :Message{
    val imageByteArray: ByteArray
    val toUser: String
    val filename: String

    constructor(inputStream: InputStream, filename: String, toUser: String) {
        val byteArray = inputStream.use {
            it.readAllBytes()
        }
        this.filename = filename
        this.imageByteArray = byteArray
        this.toUser = toUser
    }

    constructor(imageByteArray: ByteArray, filename: String, toUser: String) {
        this.filename = filename
        this.imageByteArray = imageByteArray
        this.toUser = toUser
    }

    constructor(imageFile: File, filename: String = imageFile.name, toUser: String) {
        this.filename = filename
        this.imageByteArray = imageFile.readBytes()
        this.toUser = toUser
    }
}