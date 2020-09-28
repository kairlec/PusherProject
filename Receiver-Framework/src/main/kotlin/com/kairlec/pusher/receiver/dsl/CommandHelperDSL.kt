package com.kairlec.pusher.receiver.dsl

import com.kairlec.pusher.receiver.CommandHelper
import com.kairlec.pusher.receiver.reply.Message
import java.io.File
import java.io.InputStream

@MessageDsl
internal fun CommandHelper.currentCommand(
        command: String, trim: Boolean, ignoreCase: Boolean,
        onEvent: CommandHelper.(String) -> Unit
) {
    if (matchCurrent(command, trim, ignoreCase)) {
        val nextCommand = nextCommand()
        onEvent(this, nextCommand)
    }
}

@MessageDsl
internal fun CommandHelper.nextCommand(command: String, trim: Boolean, ignoreCase: Boolean, onEvent: CommandHelper.(String) -> Unit
) {
    if (matchNext(command, trim, ignoreCase)) {
        val nextCommand = nextCommand()
        onEvent(this, nextCommand)
    }
}


@MessageDsl
fun CommandHelper.next(command: String,
                       trim: Boolean = true,
                       ignoreCase: Boolean = false,
                       onEvent: CommandHelper.(String) -> Unit
) = nextCommand(command, trim, ignoreCase, onEvent)


@MessageDsl
fun CommandHelper.current(command: String, trim: Boolean = true,
                          ignoreCase: Boolean = false,
                          onEvent: CommandHelper.(String) -> Unit
) = currentCommand(command, trim, ignoreCase, onEvent)


@MessageDsl
fun CommandHelper.replyText(text: String) {
    msg.reply(text)
}

@MessageDsl
fun CommandHelper.replyMarkdown(markdown: String) {
    msg.replyMarkdown(markdown)
}

@MessageDsl
fun CommandHelper.replyTextCard(title: String, description: String, url: String, btntxt: String = "详情") {
    msg.replyTextCard(title, description, url, btntxt)
}

@MessageDsl
fun CommandHelper.replyImage(image: ByteArray, filename: String) {
    msg.replyImage(image, filename)
}

@MessageDsl
fun CommandHelper.replyImage(image: InputStream, filename: String) {
    msg.replyImage(image, filename)
}

@MessageDsl
fun CommandHelper.replyImage(image: File, filename: String = image.name) {
    msg.replyImage(image, filename)
}

@MessageDsl
fun CommandHelper.replyVoice(voice: ByteArray, filename: String) {
    msg.replyVoice(voice, filename)
}

@MessageDsl
fun CommandHelper.replyVoice(voice: InputStream, filename: String) {
    msg.replyVoice(voice, filename)
}

@MessageDsl
fun CommandHelper.replyVoice(voice: File, filename: String = voice.name) {
    msg.replyVoice(voice, filename)
}

@MessageDsl
fun CommandHelper.replyVideo(video: ByteArray, filename: String) {
    msg.replyVideo(video, filename)
}

@MessageDsl
fun CommandHelper.replyVideo(video: InputStream, filename: String) {
    msg.replyVideo(video, filename)
}

@MessageDsl
fun CommandHelper.replyVideo(video: File, filename: String = video.name) {
    msg.replyVideo(video, filename)
}

@MessageDsl
fun CommandHelper.replyFile(file: ByteArray, filename: String) {
    msg.replyFile(file, filename)
}

@MessageDsl
fun CommandHelper.replyFile(file: InputStream, filename: String) {
    msg.replyFile(file, filename)
}

@MessageDsl
fun CommandHelper.replyFile(file: File, filename: String = file.name) {
    msg.replyFile(file, filename)
}

@MessageDsl
fun CommandHelper.replyNewsMessage(title: String, url: String, description: String? = null, picurl: String? = null) {
    msg.replyNewsMessage(title, url, description, picurl)
}

@MessageDsl
fun CommandHelper.replyMpNewsMessage(title: String, thumbMediaID: String, content: String, author: String? = null, contentSourceUrl: String? = null, digest: String? = null) {
    msg.replyMpNewsMessage(title, thumbMediaID, content, author, contentSourceUrl, digest)
}

/**
 * 给这个消息事件的主体发送消息
 */
@JvmSynthetic
internal fun CommandHelper.reply(message: Message) = msg.reply(message)


@JvmSynthetic
internal fun CommandHelper.reply(plain: String) = msg.reply(plain)
