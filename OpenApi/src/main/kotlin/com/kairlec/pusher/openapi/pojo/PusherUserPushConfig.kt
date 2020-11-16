package com.kairlec.pusher.openapi.pojo

import com.fasterxml.jackson.module.kotlin.convertValue
import javax.persistence.Column
import javax.persistence.Convert
import javax.persistence.Entity
import javax.persistence.Id


@Entity
class PusherUserPushConfig
/**
 * 有无参构造,但直接调用可能会造成未知的错误
 */
@Deprecated("only for JPA", ReplaceWith("PusherUserPushConfig( openUserId = )"), level = DeprecationLevel.ERROR)
constructor() {
    companion object {
        fun convertConfigToString(config: HashSet<String>): String {
            return objectMapper.writeValueAsString(config)
        }

        fun convertStringToConfig(s: String): HashSet<String> {
            return objectMapper.convertValue(objectMapper.readTree(s))
        }

    }

    /**
     * 订阅级别,只接受小于或等于此级别的日志
     */
    var subscriberLevel: SubscriberLevel = SubscriberLevel.INFO

    /**
     * openUserId为企业内用户唯一标识号
     * @see PusherUser.openUserId
     */
    @Id
    @Column(length = 64)
    var openUserId: String = ""

    /**
     * 白名单过滤器
     *
     * 出现于此过滤器中的规则则直接接受
     *
     * 此过滤器优先级比订阅级别[subscriberLevel]和黑名单过滤器[blockFilter]都高
     * @see blockFilter
     * @see subscriberLevel
     */
    @Suppress("JpaAttributeTypeInspection")
    @Convert(converter = CustomHashSetConverter::class)
    @Column(length = 65535, columnDefinition = "TEXT")
    var allowFilter: HashSet<String> = HashSet()

    /**
     * 黑名单过滤器
     *
     * 出现于此过滤器中的规则则直接过滤
     *
     * 此过滤器也将过滤掉小于或等于订阅级别[subscriberLevel]的内容
     * @see allowFilter
     * @see subscriberLevel
     */
    @Suppress("JpaAttributeTypeInspection")
    @Convert(converter = CustomHashSetConverter::class)
    @Column(length = 65535, columnDefinition = "TEXT")
    var blockFilter: HashSet<String> = HashSet()

    @Suppress("DEPRECATION_ERROR")
    constructor(pusherUser: PusherUser) : this() {
        this.openUserId = pusherUser.openUserId
    }

    override fun toString(): String {
        TODO("to String")
    }

    /**
     * 判断当前是否被过滤
     */
    fun allow(level: SubscriberLevel, keyword: String?): Boolean {
        if (keyword == null) {
            return level.intLevel <= this.subscriberLevel.intLevel
        }
        val aKeyWord = keyword.trim().toLowerCase()
        if (aKeyWord in allowFilter) {
            return true
        }
        if (aKeyWord in blockFilter) {
            return false
        }
        return level.intLevel <= this.subscriberLevel.intLevel
    }
}

/**
 * 消息订阅级别
 *
 * 标识每个用户想要接受到的消息级别,以便于接受过多不必要的消息
 */
enum class SubscriberLevel(var intLevel: Int) {
    /**
     * OFF,最小的消息级别,消息等级为[Int.MIN_VALUE],过滤掉所有内容(关闭通知)
     */
    OFF(Int.MIN_VALUE),

    /**
     * FATAL,严重的消息级别,消息等级为100
     */
    FATAL(100),

    /**
     * ERROR,错误的消息级别,消息等级为200
     */
    ERROR(200),

    /**
     * WARN,警告的消息级别,消息等级为300
     */
    WARN(300),

    /**
     * INFO,信息的消息级别,消息等级为400
     */
    INFO(400),

    /**
     * DEBUG,调试的消息级别,消息等级为500
     */
    DEBUG(500),

    /**
     * TRACE,堆栈的消息级别,消息等级为600
     */
    TRACE(600),

    /**
     * ALL,所有的消息级别,消息等级为[Int.MAX_VALUE],接受所有内容(常开通知)
     */
    ALL(Int.MAX_VALUE),

    /**
     * CUSTOM,自定义的消息级别,可自由更改消息等级值,以便活动接受自定义级别
     */
    CUSTOM(INFO),
    ;

    /**
     * 创建的克隆消息订阅器
     */
    constructor(refLevel: SubscriberLevel) {
        intLevel = refLevel.intLevel
    }

    /**
     * 自定义级别数值
     * @param intLevel 消息订阅级别数值
     */
    private fun level(intLevel: Int) = apply { this.intLevel = intLevel }

    /**
     * 自定义消息级别
     * @param intLevel 消息订阅级别数值
     * @return 消息订阅级别枚举
     * @see Companion.custom
     */
    @Deprecated("Please use the static method", ReplaceWith("SubscriberLevel.custom()"))
    fun custom(intLevel: Int): SubscriberLevel {
        return CUSTOM.level(intLevel)
    }

    override fun toString(): String {
        return "$name[$intLevel]"
    }

    companion object {
        /**
         * 自定义消息级别
         * @param intLevel 消息订阅级别数值
         * @return 消息订阅级别枚举
         */
        fun custom(intLevel: Int): SubscriberLevel {
            return CUSTOM.level(intLevel)
        }

        /**
         * 解析原生字符串为消息订阅级别
         *
         * 支持直接的数字所对应的级别,数字不对应级别,转换为CUSTOM
         *
         * 支持直接的字符串,比如OFF,ERROR,WARN,DEBUG等,不区分大小写
         *
         * 若此返回null,则可尝试使用[similar]进行相似匹配
         * @param raw 原生字符串
         * @return 消息订阅级别枚举,若字符串解析失败,则返回null
         */
        fun parse(raw: String): SubscriberLevel? {
            val level: Int? = raw.toIntOrNull()
            val e = raw.trim().trim('-', '_', '@')
            values().forEach {
                if (it.name.equals(e, ignoreCase = true) || it.intLevel == level) {
                    return it
                }
            }
            return level?.let { CUSTOM.level(it) }
        }

        /**
         * 匹配与str最相似的订阅级别
         *
         * 不区分大小写,使用字符串相似算法寻找与最像的
         * @param str 传入字符串,只能为字符,不能为数字
         * @return 最像的第一个,若都不像则为null
         * @see com.kairlec.pusher.openapi.pojo.similar
         */
        fun similar(str: String, target: Double = 0.6): SubscriberLevel? {
            val e = str.trim().trim('-', '_', '@').toLowerCase()
            return values().map { similar(it.name.toLowerCase(), e).cutOff(target) to it }.maxByOrNull { it.first }?.cutOff(target)
        }
    }
}