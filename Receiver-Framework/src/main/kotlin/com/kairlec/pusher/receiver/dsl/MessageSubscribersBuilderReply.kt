package com.kairlec.pusher.receiver.dsl

import com.kairlec.pusher.receiver.msg.ReceiveMsg
import com.kairlec.pusher.receiver.reply.*
import java.io.File
import java.io.InputStream


@MessageDsl
fun ReceiveMsg.replyText(text: String) {
    if (!closeStatus()) {
        replyService.reply(TextMessage(text, fromUserName))
    }
}

@MessageDsl
fun ReceiveMsg.replyMarkdown(markdown: String) {
    if (!closeStatus()) {
        replyService.reply(MarkdownMessage(markdown, fromUserName))
    }
}

@MessageDsl
fun ReceiveMsg.replyTextCard(title: String, description: String, url: String, btntxt: String = "详情") {
    if (!closeStatus()) {
        replyService.reply(TextCardMessage(title, description, url, btntxt, fromUserName))
    }
}

@MessageDsl
fun ReceiveMsg.replyImage(image: ByteArray, filename: String) {
    if (!closeStatus()) {
        replyService.reply(ImageMessage(image, filename, fromUserName))
    }
}

@MessageDsl
fun ReceiveMsg.replyImage(image: InputStream, filename: String) {
    if (!closeStatus()) {
        replyService.reply(ImageMessage(image, filename, fromUserName))
    }
}

@MessageDsl
fun ReceiveMsg.replyImage(image: File, filename: String = image.name) {
    if (!closeStatus()) {
        replyService.reply(ImageMessage(image, filename, fromUserName))
    }
}

@MessageDsl
fun ReceiveMsg.replyVoice(voice: ByteArray, filename: String) {
    if (!closeStatus()) {
        replyService.reply(VoiceMessage(voice, filename, fromUserName))
    }
}

@MessageDsl
fun ReceiveMsg.replyVoice(voice: InputStream, filename: String) {
    if (!closeStatus()) {
        replyService.reply(VoiceMessage(voice, filename, fromUserName))
    }
}

@MessageDsl
fun ReceiveMsg.replyVoice(voice: File, filename: String = voice.name) {
    if (!closeStatus()) {
        replyService.reply(VoiceMessage(voice, filename, fromUserName))
    }
}

@MessageDsl
fun ReceiveMsg.replyVideo(video: ByteArray, filename: String) {
    if (!closeStatus()) {
        replyService.reply(VideoMessage(video, filename, fromUserName))
    }
}

@MessageDsl
fun ReceiveMsg.replyVideo(video: InputStream, filename: String) {
    if (!closeStatus()) {
        replyService.reply(VideoMessage(video, filename, fromUserName))
    }
}

@MessageDsl
fun ReceiveMsg.replyVideo(video: File, filename: String = video.name) {
    if (!closeStatus()) {
        replyService.reply(VideoMessage(video, filename, fromUserName))
    }
}

@MessageDsl
fun ReceiveMsg.replyFile(file: ByteArray, filename: String) {
    if (!closeStatus()) {
        replyService.reply(FileMessage(file, filename, fromUserName))
    }
}

@MessageDsl
fun ReceiveMsg.replyFile(file: InputStream, filename: String) {
    if (!closeStatus()) {
        replyService.reply(FileMessage(file, filename, fromUserName))
    }
}

@MessageDsl
fun ReceiveMsg.replyFile(file: File, filename: String = file.name) {
    if (!closeStatus()) {
        replyService.reply(FileMessage(file, filename, fromUserName))
    }
}

@MessageDsl
fun ReceiveMsg.replyNewsMessage(title: String, url: String, description: String? = null, picurl: String? = null) {
    if (!closeStatus()) {
        replyService.reply(NewsMessage(title, url, description, picurl, fromUserName))
    }
}

@MessageDsl
fun ReceiveMsg.replyMpNewsMessage(title: String, thumbMediaID: String, content: String, author: String? = null, contentSourceUrl: String? = null, digest: String? = null) {
    if (!closeStatus()) {
        replyService.reply(MpNewsMessage(title, thumbMediaID, content, author, contentSourceUrl, digest, fromUserName))
    }
}

/**
 * 给这个消息事件的主体发送消息
 */
@JvmSynthetic
internal fun ReceiveMsg.reply(message: Message) = replyService.reply(message)


@JvmSynthetic
internal fun ReceiveMsg.reply(plain: String) = replyService.reply(TextMessage(plain, fromUserName))
