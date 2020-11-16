@file:Suppress(
        "unused", "DSL_SCOPE_VIOLATION_WARNING", "INAPPLICABLE_JVM_NAME", "INVALID_CHARACTERS",
        "NAME_CONTAINS_ILLEGAL_CHARS", "FunctionName"
)

/**
 * 请参阅mirai相关:
 *
 * https://github.com/mamoe/mirai
 */
package com.kairlec.pusher.receiver.dsl

import com.kairlec.pusher.receiver.CommandHelper
import com.kairlec.pusher.receiver.msg.ReceiveMsg
import com.kairlec.pusher.receiver.reply.Message
import java.io.File
import java.io.InputStream
import java.time.LocalDateTime
import java.time.ZoneOffset


/**
 * 消息事件的处理器.
 *
 * 注:
 * 接受者 T 为 [ReceiveMsg]
 * 参数 String 为 转为字符串了的消息 ([Message.toString])
 */
typealias MessageListener<T, R> = @MessageDsl T.(String) -> R


/**
 * 消息订阅构造器
 *
 * @param M 消息类型
 * @param R 消息监听器内部的返回值
 * @param Ret 每个 DSL 函数创建监听器之后的返回值
 *
 */
@MessageDsl
open class MessageSubscribersBuilder<M : ReceiveMsg, out Ret, R : RR, RR>(
        /**
         * 用于 [MessageListener] 无返回值的替代.
         */
        @PublishedApi
        internal val stub: RR,
        /**
         * invoke 这个 lambda 时, 它将会把 [消息事件的处理器][MessageListener] 注册给事件, 并返回注册完成返回的监听器.
         */
        val subscriber: (M.(String) -> Boolean, MessageListener<M, RR>) -> Ret
) {
    @Suppress("DEPRECATION_ERROR")
    open fun newListeningFilter(filter: M.(String) -> Boolean): ListeningFilter = ListeningFilter(filter)

    /**
     * 由 [contains], [startsWith] 等 DSL 创建出的监听条件, 使用 [invoke] 将其注册给事件
     */
    open inner class ListeningFilter @Deprecated( // keep it for development warning
            "use newListeningFilter instead",
            ReplaceWith("newListeningFilter(filter)"),
            level = DeprecationLevel.ERROR
    ) constructor(
            val filter: M.(String) -> Boolean
    ) {
        /** 进行逻辑 `or`. */
        infix fun or(another: ListeningFilter): ListeningFilter =
                newListeningFilter { filter.invoke(this, it) || another.filter.invoke(this, it) }

        /** 进行逻辑 `and`. */
        infix fun and(another: ListeningFilter): ListeningFilter =
                newListeningFilter { filter.invoke(this, it) && another.filter.invoke(this, it) }

        /** 进行逻辑 `xor`. */
        infix fun xor(another: ListeningFilter): ListeningFilter =
                newListeningFilter { filter.invoke(this, it) xor another.filter.invoke(this, it) }

        /** 进行逻辑 `nand`, 即 `not and`. */
        infix fun nand(another: ListeningFilter): ListeningFilter =
                newListeningFilter { !filter.invoke(this, it) || !another.filter.invoke(this, it) }

        /** 进行逻辑 `not` */
        fun not(): ListeningFilter = newListeningFilter { !filter.invoke(this, it) }

        /** 启动事件监听. */
        // do not inline due to kotlin (1.3.61) bug: java.lang.IllegalAccessError
        operator fun invoke(onEvent: MessageListener<M, R>): Ret = content(filter, onEvent)
    }

    /** 启动监听器, 在消息满足条件 [this] 时回复原消息 */
    @MessageDsl
    open infix fun ListeningFilter.reply(toReply: String): Ret =
            content(filter) { reply(toReply);this@MessageSubscribersBuilder.stub }


    /** 启动监听器, 在消息满足条件 [this] 时回复原消息 */
    @MessageDsl
    open infix fun ListeningFilter.reply(message: Message): Ret =
            content(filter) { reply(message);this@MessageSubscribersBuilder.stub }

    /**
     * 启动监听器, 在消息满足条件 [this] 时执行 [replier] 并以其返回值回复.
     * 返回值 [Unit]或null 将被忽略, [Message] 将被直接回复, 其他内容将会 [Any.toString] 后发送.
     */
    @MessageDsl
    open infix fun ListeningFilter.reply(
            replier: (@MessageDsl M.(String) -> Any?)
    ): Ret =
            content(filter) {
                this@MessageSubscribersBuilder.executeAndReply(this, replier)
            }

    /** 无触发条件, 每次收到消息都执行 [onEvent] */
    @MessageDsl
    open fun always(onEvent: MessageListener<M, RR>): Ret = subscriber({ true }, onEvent)

    /** [消息内容][ReceiveMsg.contentToString] `==` [equals] */
    @MessageDsl
    fun case(equals: String, ignoreCase: Boolean = false, trim: Boolean = true): ListeningFilter =
            caseImpl(equals, ignoreCase, trim)

    /** 如果[消息内容][ReceiveMsg.contentToString]  `==` [equals] */
    @MessageDsl
    operator fun String.invoke(block: MessageListener<M, R>): Ret = case(this, onEvent = block)

    /** 如果[消息内容][ReceiveMsg.contentToString]  [matches] */
    @MessageDsl
    @JvmSynthetic
    @JvmName("matchingExtension")
    infix fun Regex.matching(block: MessageListener<M, R>): Ret = content({ it matches this@matching }, block)

    /** 如果[消息内容][ReceiveMsg.contentToString] [Regex.find] 不为空 */
    @MessageDsl
    @JvmSynthetic
    @JvmName("findingExtension")
    infix fun Regex.finding(block: @MessageDsl M.(MatchResult) -> R): Ret =
            always { content ->
                this@finding.find(content)?.let { block(this, it) } ?: this@MessageSubscribersBuilder.stub
            }

    /**
     * [消息内容][ReceiveMsg.contentToString] `==` [equals]
     * @param trim `true` 则删除首尾空格后比较
     * @param ignoreCase `true` 则不区分大小写
     */
    @MessageDsl
    fun case(
            equals: String, ignoreCase: Boolean = false, trim: Boolean = true,
            onEvent: MessageListener<M, R>
    ): Ret = (if (trim) equals.trim() else equals).let { toCheck ->
        content({ (if (trim) it.trim() else it).equals(toCheck, ignoreCase = ignoreCase) }) {
            onEvent(this, this.contentToString())
        }
    }

    /** [消息内容][ReceiveMsg.contentToString]包含 [sub] */
    @MessageDsl
    @JvmOverloads // bin comp
    fun contains(sub: String, ignoreCase: Boolean = false): ListeningFilter =
            content { it.contains(sub, ignoreCase) }

    /**
     * [消息内容][ReceiveMsg.contentToString]包含 [sub] 中的任意一个元素
     */
    @MessageDsl
    fun contains(
            sub: String, ignoreCase: Boolean = false, trim: Boolean = true,
            onEvent: MessageListener<M, R>
    ): Ret = containsImpl(sub, ignoreCase, trim, onEvent)

    /** [消息内容][ReceiveMsg.contentToString]包含 [sub] */
    @JvmOverloads
    @MessageDsl
    fun containsAny(vararg sub: String, ignoreCase: Boolean = false, trim: Boolean = true): ListeningFilter =
            containsAnyImpl(*sub, ignoreCase = ignoreCase, trim = trim)

    /** [消息内容][ReceiveMsg.contentToString]包含 [sub] */
    @JvmOverloads
    @MessageDsl
    fun containsAll(vararg sub: String, ignoreCase: Boolean = false, trim: Boolean = true): ListeningFilter =
            containsAllImpl(sub, ignoreCase = ignoreCase, trim = trim)


    /** 如果消息的前缀是 [prefix] */
    @MessageDsl
    fun startsWith(prefix: String, trim: Boolean = true): ListeningFilter {
        val toCheck = if (trim) prefix.trim() else prefix
        return content { (if (trim) it.trim() else it).startsWith(toCheck) }
    }

    /** 如果消息的前缀是 [prefix] */
    @MessageDsl
    fun startsWith(
            prefix: String, removePrefix: Boolean = true, trim: Boolean = true,
            onEvent: @MessageDsl M.(String) -> R
    ): Ret = startsWithImpl(prefix, removePrefix, trim, onEvent)

    /** 如果消息的结尾是 [suffix] */
    @MessageDsl
    @JvmOverloads // for binary compatibility
    fun endsWith(suffix: String, trim: Boolean = true): ListeningFilter =
            content { if (trim) it.trimEnd().endsWith(suffix) else it.endsWith(suffix) }

    /** 如果消息的结尾是 [suffix] */
    @MessageDsl
    fun endsWith(
            suffix: String, removeSuffix: Boolean = true, trim: Boolean = true,
            onEvent: @MessageDsl M.(String) -> R
    ): Ret = endsWithImpl(suffix, removeSuffix, trim, onEvent)

    /** 如果是这个人发的消息 */
    @MessageDsl
    fun sentBy(name: String): ListeningFilter = content { this.fromUserName == name }

    /** 如果是这个人发的消息 */
    @MessageDsl
    fun sentBy(name: String, onEvent: MessageListener<M, R>): Ret = content { this.fromUserName == name }.invoke(onEvent)

    /** 如果 [mapper] 返回值非空, 就执行 [onEvent] */
    @MessageDsl
    open fun <N : Any> mapping(mapper: M.(String) -> N?, onEvent: @MessageDsl M.(N) -> R): Ret =
            always {
                onEvent.invoke(
                        this,
                        mapper(this, contentToString()) ?: return@always this@MessageSubscribersBuilder.stub
                )
            }

    /** 如果 [filter] 返回 `true` */
    @MessageDsl
    fun content(filter: M.(String) -> Boolean): ListeningFilter = newListeningFilter(filter)

    /** [消息内容][ReceiveMsg.contentToString]可由正则表达式匹配([Regex.matchEntire]) */
    @MessageDsl
    fun matching(regex: Regex, trim: Boolean = true): ListeningFilter = content { regex.matchEntire(if (trim) it.trim() else it) != null }


    /** [消息内容][ReceiveMsg.contentToString]可由正则表达式匹配([Regex.matchEntire]), 就执行 `onEvent` */
    @MessageDsl
    fun matching(regex: Regex, trim: Boolean = true, onEvent: @MessageDsl M.(MatchResult) -> Unit): Ret =
            always {
                this@MessageSubscribersBuilder.executeAndReply(this) {
                    onEvent.invoke(this, regex.matchEntire(if (trim) it.trim() else it)
                            ?: return@always this@MessageSubscribersBuilder.stub
                    )
                }
            }

    /** [消息内容][ReceiveMsg.contentToString]可由正则表达式查找([Regex.find]) */
    @MessageDsl
    fun finding(regex: Regex, trim: Boolean = true): ListeningFilter = content { regex.find(if (trim) it.trim() else it) != null }

    /** [消息内容][ReceiveMsg.contentToString]可由正则表达式查找([Regex.find]), 就执行 `onEvent` */
    @MessageDsl
    fun finding(regex: Regex, trim: Boolean = true, onEvent: @MessageDsl M.(MatchResult) -> Unit): Ret =
            always {
                this@MessageSubscribersBuilder.executeAndReply(this) {
                    onEvent.invoke(this, regex.find(if (trim) it.trim() else it)
                            ?: return@always this@MessageSubscribersBuilder.stub
                    )
                }
            }


    /** [消息内容][ReceiveMsg.contentToString]包含 [this] 则回复 [reply] */
    @MessageDsl
    open infix fun String.containsReply(reply: String): Ret =
            content({ this@containsReply in it }) { this.replyText(reply); this@MessageSubscribersBuilder.stub }

    /**
     * [消息内容][ReceiveMsg.contentToString]包含 [this] 则执行 [replier] 并将其返回值回复给发信对象.
     *
     * [replier] 的 `it` 将会是消息内容 string.
     *
     * @param replier 若返回 [Message] 则直接发送; 若返回 [Unit] 则不回复; 其他情况则 [Any.toString] 后回复
     */
    @MessageDsl
    open infix fun String.containsReply(replier: @MessageDsl M.(String) -> Any?): Ret =
            content({ this@containsReply in it }) { this@MessageSubscribersBuilder.executeAndReply(this, replier) }

    /**
     * [消息内容][ReceiveMsg.contentToString]可由正则表达式匹配([Regex.matchEntire]), 则执行 [replier] 并将其返回值回复给发信对象.
     *
     * [replier] 的 `it` 将会是消息内容 string.
     *
     * @param replier 若返回 [Message] 则直接发送; 若返回 [Unit] 则不回复; 其他情况则 [Any.toString] 后回复
     */
    @MessageDsl
    open infix fun Regex.matchingReply(replier: @MessageDsl M.(MatchResult) -> Any?): Ret =
            always {
                this@MessageSubscribersBuilder.executeAndReply(this) {
                    replier.invoke(
                            this,
                            matchEntire(it) ?: return@always this@MessageSubscribersBuilder.stub
                    )
                }
            }

    /**
     * [消息内容][ReceiveMsg.contentToString]可由正则表达式查找([Regex.find]), 则执行 [replier] 并将其返回值回复给发信对象.
     *
     * [replier] 的 `it` 将会是消息内容 string.
     *
     * @param replier 若返回 [Message] 则直接发送; 若返回 [Unit]或null 则不回复; 其他情况则 [Any.toString] 后回复
     */
    @MessageDsl
    open infix fun Regex.findingReply(replier: @MessageDsl M.(MatchResult) -> Any?): Ret =
            always {
                this@MessageSubscribersBuilder.executeAndReply(this) {
                    replier.invoke(
                            this,
                            this@findingReply.find(it) ?: return@always this@MessageSubscribersBuilder.stub
                    )
                }
            }


    /**
     * 不考虑空格, [消息内容][ReceiveMsg.contentToString]以 [this] 结尾则执行 [replier] 并将其返回值回复给发信对象.
     * @param replier 若返回 [Message] 则直接发送; 若返回 [Unit]或null 则不回复; 其他情况则 [Any.toString] 后回复
     */
    @MessageDsl
    open infix fun String.endsWithReply(replier: @MessageDsl M.(String) -> Any?): Ret {
        val toCheck = this.trimEnd()
        return content({ it.trim().endsWith(toCheck) }) {
            this@MessageSubscribersBuilder.executeAndReply(this) { replier(this, it.trim().removeSuffix(toCheck)) }
        }
    }

    /** 当发送的消息内容为 [this] 就回复 [reply] */
    @MessageDsl
    open infix fun String.reply(reply: String): Ret {
        val toCheck = this.trim()
        return content({ it.trim() == toCheck }) { reply(reply);this@MessageSubscribersBuilder.stub }
    }

    /** 当发送的消息内容为 [this] 就回复 [reply] */
    @MessageDsl
    open infix fun String.reply(reply: Message): Ret {
        val toCheck = this.trim()
        return content({ it.trim() == toCheck }) { reply(reply);this@MessageSubscribersBuilder.stub }
    }

    /** 当发送的消息内容为 [this] 就执行并回复 [replier] 的返回值 */
    @MessageDsl
    open infix fun String.reply(replier: @MessageDsl M.(String) -> Any?): Ret {
        val toCheck = this.trim()
        return content({ it.trim() == toCheck }) {
            @Suppress("DSL_SCOPE_VIOLATION_WARNING")
            this@MessageSubscribersBuilder.executeAndReply(this) { replier(this, it.trim()) }
        }
    }

    @Suppress("REDUNDANT_INLINE_SUSPEND_FUNCTION_TYPE", "UNCHECKED_CAST") // false positive
    internal inline fun executeAndReply(m: M, replier: M.(String) -> Any?): RR {
        when (val message = replier(m, m.contentToString())) {
            is Message -> m.reply(message)
            is Unit, null -> Unit
            else -> m.reply(message.toString())
        }
        return stub
    }

    /**
     * 当消息是来自[id]应用的时候就执行并回复 [replier] 的返回值
     */
    @MessageDsl
    fun fromApplication(id: Long, replier: @MessageDsl (M.(Long) -> Any?)): Ret {
        return content({ agentID == id }) {
            this@MessageSubscribersBuilder.executeAndReply(this) { replier(this, agentID) }
        }
    }

    /**
     *
     * 当消息是来自[id]应用的时候
     */
    @MessageDsl
    fun fromApplication(id: Long): ListeningFilter {
        return content { agentID == id }
    }

    /**
     * 当消息创建时间早于[timestamp]的时候就执行并回复 [replier] 的返回值
     */
    @MessageDsl
    fun before(timestamp: Long, replier: @MessageDsl (M.(Long) -> Any?)): Ret {
        return content({ createTime > timestamp }) {
            this@MessageSubscribersBuilder.executeAndReply(this) { replier(this, createTime) }
        }
    }

    /**
     * 当消息创建时间早于[timestamp]的时候就执行并回复 [replier] 的返回值
     */
    @MessageDsl
    fun before(timestamp: LocalDateTime, replier: @MessageDsl (M.(LocalDateTime) -> Any?)): Ret {
        return content({ LocalDateTime.ofEpochSecond(createTime / 1000, 0, ZoneOffset.ofHours(8)).isBefore(timestamp) }) {
            this@MessageSubscribersBuilder.executeAndReply(this) { replier(this, LocalDateTime.ofEpochSecond(createTime / 1000, 0, ZoneOffset.ofHours(8))) }
        }
    }

    /**
     * 当消息创建时间早于[timestamp]的时候
     */
    @MessageDsl
    fun before(timestamp: Long): ListeningFilter {
        return content { createTime > timestamp }
    }

    /**
     * 当消息创建时间早于[timestamp]的时候
     */
    @MessageDsl
    fun before(timestamp: LocalDateTime): ListeningFilter {
        return content { LocalDateTime.ofEpochSecond(createTime / 1000, 0, ZoneOffset.ofHours(8)).isBefore(timestamp) }
    }

    /**
     * 当消息创建时间晚于[timestamp]的时候就执行并回复 [replier] 的返回值
     */
    @MessageDsl
    fun after(timestamp: Long, replier: @MessageDsl (M.(Long) -> Any?)): Ret {
        return content({ createTime < timestamp }) {
            this@MessageSubscribersBuilder.executeAndReply(this) { replier(this, createTime) }
        }
    }

    /**
     * 当消息创建时间晚于[timestamp]的时候就执行并回复 [replier] 的返回值
     */
    @MessageDsl
    fun after(timestamp: LocalDateTime, replier: @MessageDsl (M.(LocalDateTime) -> Any?)): Ret {
        return content({ LocalDateTime.ofEpochSecond(createTime / 1000, 0, ZoneOffset.ofHours(8)).isAfter(timestamp) }) {
            this@MessageSubscribersBuilder.executeAndReply(this) { replier(this, LocalDateTime.ofEpochSecond(createTime / 1000, 0, ZoneOffset.ofHours(8))) }
        }
    }

    /**
     * 当消息创建时间晚于[timestamp]的时候
     */
    @MessageDsl
    fun after(timestamp: Long): ListeningFilter {
        return content { createTime < timestamp }
    }

    /**
     * 当消息创建时间晚于[timestamp]的时候
     */
    @MessageDsl
    fun after(timestamp: LocalDateTime): ListeningFilter {
        return content { LocalDateTime.ofEpochSecond(createTime / 1000, 0, ZoneOffset.ofHours(8)).isAfter(timestamp) }
    }

    /**
     * 当消息content内容是命令行的时候就执行并回复 [replier] 的返回值
     */
    @MessageDsl
    fun command(baseCommand: String? = null, ignoreCase: Boolean = false, replier: @MessageDsl (CommandHelper.(String) -> Any?)): Ret {
        return content({
            val helper = CommandHelper(contentToString(), this)
            if (helper.isValidCommand()) {
                if (baseCommand != null) {
                    helper.baseCommand().equals(baseCommand, ignoreCase = ignoreCase)
                } else {
                    true
                }
            } else {
                false
            }
        }) {
            this@MessageSubscribersBuilder.executeAndReply(this) {
                replier(CommandHelper(contentToString(), this).apply {
                    if (hasNext()) {
                        next()
                    }
                }, it)
            }
        }
    }

    /**
     * 直接回复这个主体
     */
    @MessageDsl
    fun directReply(plain: String) = content({ true }) { reply(plain); this@MessageSubscribersBuilder.stub }


    /**
     * 直接回复这个主体
     */
    @MessageDsl
    fun directReply(message: Message) = content({ true }) { reply(message); this@MessageSubscribersBuilder.stub }


    /**
     * 直接回复这个主体
     */
    @MessageDsl
    fun directReplyText(text: String) = content({ true }) { replyText(text); this@MessageSubscribersBuilder.stub }


    /**
     * 直接回复这个主体
     */
    @MessageDsl
    fun directReplyMarkdown(markdown: String) = content({ true }) { replyMarkdown(markdown); this@MessageSubscribersBuilder.stub }


    /**
     * 直接回复这个主体
     */
    @MessageDsl
    fun directReplyTextCard(title: String, description: String, url: String, btntxt: String = "详情") = content({ true }) { replyTextCard(title, description, url, btntxt); this@MessageSubscribersBuilder.stub }


    /**
     * 直接回复这个主体
     */
    @MessageDsl
    fun directReplyImage(image: ByteArray, filename: String) = content({ true }) { replyImage(image, filename); this@MessageSubscribersBuilder.stub }


    /**
     * 直接回复这个主体
     */
    @MessageDsl
    fun directReplyImage(image: InputStream, filename: String) = content({ true }) { replyImage(image, filename); this@MessageSubscribersBuilder.stub }


    /**
     * 直接回复这个主体
     */
    @MessageDsl
    fun directReplyImage(image: File, filename: String = image.name) = content({ true }) { replyImage(image, filename); this@MessageSubscribersBuilder.stub }


    /**
     * 直接回复这个主体
     */
    @MessageDsl
    fun directReplyVoice(voice: ByteArray, filename: String) = content({ true }) { replyVoice(voice, filename); this@MessageSubscribersBuilder.stub }


    /**
     * 直接回复这个主体
     */
    @MessageDsl
    fun directReplyVoice(voice: InputStream, filename: String) = content({ true }) { replyVoice(voice, filename); this@MessageSubscribersBuilder.stub }


    /**
     * 直接回复这个主体
     */
    @MessageDsl
    fun directReplyVoice(voice: File, filename: String = voice.name) = content({ true }) { replyVoice(voice, filename); this@MessageSubscribersBuilder.stub }


    /**
     * 直接回复这个主体
     */
    @MessageDsl
    fun directReplyVideo(video: ByteArray, filename: String) = content({ true }) { replyVideo(video, filename); this@MessageSubscribersBuilder.stub }


    /**
     * 直接回复这个主体
     */
    @MessageDsl
    fun directReplyVideo(video: InputStream, filename: String) = content({ true }) { replyVideo(video, filename); this@MessageSubscribersBuilder.stub }


    /**
     * 直接回复这个主体
     */
    @MessageDsl
    fun directReplyVideo(video: File, filename: String = video.name) = content({ true }) { replyVideo(video, filename); this@MessageSubscribersBuilder.stub }


    /**
     * 直接回复这个主体
     */
    @MessageDsl
    fun directReplyFile(file: ByteArray, filename: String) = content({ true }) { replyFile(file, filename); this@MessageSubscribersBuilder.stub }


    /**
     * 直接回复这个主体
     */
    @MessageDsl
    fun directReplyFile(file: InputStream, filename: String) = content({ true }) { replyFile(file, filename); this@MessageSubscribersBuilder.stub }


    /**
     * 直接回复这个主体
     */
    @MessageDsl
    fun directReplyFile(file: File, filename: String = file.name) = content({ true }) { replyFile(file, filename); this@MessageSubscribersBuilder.stub }


    /**
     * 直接回复这个主体
     */
    @MessageDsl
    fun directReplyNewsMessage(title: String,
                               url: String,
                               description: String? = null,
                               picurl: String? = null
    ) = content({ true }) { replyNewsMessage(title, url, description, picurl); this@MessageSubscribersBuilder.stub }

    /**
     * 直接回复这个主体
     */
    @MessageDsl
    fun directReplyMpNewsMessage(title: String,
                                 thumbMediaID: String,
                                 content: String,
                                 author: String? = null,
                                 contentSourceUrl: String? = null,
                                 digest: String? = null
    ) = content({ true }) { replyMpNewsMessage(title, thumbMediaID, content, author, contentSourceUrl, digest); this@MessageSubscribersBuilder.stub }


    /**
     * 关闭消息体(不再处理)
     */
    @MessageDsl
    fun closeMsgReply() = content({ true }) { closeReply(); this@MessageSubscribersBuilder.stub }


    /**
     * 重新打开消息体(继续处理)
     */
    @MessageDsl
    fun reopenMsgReply() = content({ true }) { reopenReply(); this@MessageSubscribersBuilder.stub }

}