package com.kairlec.pusher.receiver.dsl

import com.kairlec.pusher.receiver.CommandHelper
import com.kairlec.pusher.receiver.reply.Message
import java.io.File
import java.io.InputStream

/**
 * 当前命令是否为指定匹配
 * @param command 是否为指定命令
 * @param trim 命令是否需要进行[String.trim]操作
 * @param ignoreCase 是否忽略大小写
 * @param onEvent 或匹配成功的回事件
 */
@MessageDsl
internal fun CommandHelper.currentCommand(
        command: String,
        trim: Boolean,
        ignoreCase: Boolean,
        onEvent: CommandHelper.(String) -> Unit
) {
    if (matchCurrent(command, trim, ignoreCase)) {
        val currentCommand = currentCommand()
        onEvent(this, currentCommand)
    }
}

/**
 * 下一个命令是否为(若不匹配,不进行命令切换)
 * @param command 是否为指定命令
 * @param trim 命令是否需要进行[String.trim]操作
 * @param ignoreCase 是否忽略大小写
 * @param onEvent 或匹配成功的回事件
 */
@MessageDsl
internal fun CommandHelper.nextCommand(
        command: String,
        trim: Boolean,
        ignoreCase: Boolean,
        onEvent: CommandHelper.(String) -> Unit
) {
    if (matchNext(command, trim, ignoreCase)) {
        val nextCommand = nextCommand()
        onEvent(this, nextCommand)
    }
}

/**
 * @see nextCommand
 */
@MessageDsl
fun CommandHelper.next(
        command: String,
        trim: Boolean = true,
        ignoreCase: Boolean = false,
        onEvent: CommandHelper.(String) -> Unit
) = nextCommand(command, trim, ignoreCase, onEvent)

/**
 * @see currentCommand
 */
@MessageDsl
fun CommandHelper.current(
        command: String, trim: Boolean = true,
        ignoreCase: Boolean = false,
        onEvent: CommandHelper.(String) -> Unit
) = currentCommand(command, trim, ignoreCase, onEvent)

/**
 * 直接回复文本消息
 */
@MessageDsl
fun CommandHelper.replyText(text: String) {
    msg.reply(text)
}

/**
 * 直接回复Markdown消息
 */
@MessageDsl
fun CommandHelper.replyMarkdown(markdown: String) {
    msg.replyMarkdown(markdown)
}

/**
 * 直接回复文本卡片消息
 */
@MessageDsl
fun CommandHelper.replyTextCard(title: String, description: String, url: String, btntxt: String = "详情") {
    msg.replyTextCard(title, description, url, btntxt)
}

/**
 * 直接回复图片
 */
@MessageDsl
fun CommandHelper.replyImage(image: ByteArray, filename: String) {
    msg.replyImage(image, filename)
}

/**
 * 直接回复图片
 */
@MessageDsl
fun CommandHelper.replyImage(image: InputStream, filename: String) {
    msg.replyImage(image, filename)
}

/**
 * 直接回复图片
 */
@MessageDsl
fun CommandHelper.replyImage(image: File, filename: String = image.name) {
    msg.replyImage(image, filename)
}

/**
 * 直接回复音频
 */
@MessageDsl
fun CommandHelper.replyVoice(voice: ByteArray, filename: String) {
    msg.replyVoice(voice, filename)
}

/**
 * 直接回复音频
 */
@MessageDsl
fun CommandHelper.replyVoice(voice: InputStream, filename: String) {
    msg.replyVoice(voice, filename)
}

/**
 * 直接回复音频
 */
@MessageDsl
fun CommandHelper.replyVoice(voice: File, filename: String = voice.name) {
    msg.replyVoice(voice, filename)
}

/**
 * 直接回复视频
 */
@MessageDsl
fun CommandHelper.replyVideo(video: ByteArray, filename: String) {
    msg.replyVideo(video, filename)
}

/**
 * 直接回复视频
 */
@MessageDsl
fun CommandHelper.replyVideo(video: InputStream, filename: String) {
    msg.replyVideo(video, filename)
}

/**
 * 直接回复视频
 */
@MessageDsl
fun CommandHelper.replyVideo(video: File, filename: String = video.name) {
    msg.replyVideo(video, filename)
}

/**
 * 直接回复文件
 */
@MessageDsl
fun CommandHelper.replyFile(file: ByteArray, filename: String) {
    msg.replyFile(file, filename)
}

/**
 * 直接回复文件
 */
@MessageDsl
fun CommandHelper.replyFile(file: InputStream, filename: String) {
    msg.replyFile(file, filename)
}

/**
 * 直接回复文件
 */
@MessageDsl
fun CommandHelper.replyFile(file: File, filename: String = file.name) {
    msg.replyFile(file, filename)
}

/**
 * 直接回复News
 */
@MessageDsl
fun CommandHelper.replyNewsMessage(title: String, url: String, description: String? = null, picurl: String? = null) {
    msg.replyNewsMessage(title, url, description, picurl)
}

/**
 * 直接回复MpNews
 */
@MessageDsl
fun CommandHelper.replyMpNewsMessage(title: String, thumbMediaID: String, content: String, author: String? = null, contentSourceUrl: String? = null, digest: String? = null) {
    msg.replyMpNewsMessage(title, thumbMediaID, content, author, contentSourceUrl, digest)
}

/**
 * 给这个消息事件的主体发送消息
 */
@JvmSynthetic
internal fun CommandHelper.reply(message: Message) = msg.reply(message)

/**
 * 给这个消息事件的主体发送消息
 */
@JvmSynthetic
internal fun CommandHelper.reply(plain: String) = msg.reply(plain)
