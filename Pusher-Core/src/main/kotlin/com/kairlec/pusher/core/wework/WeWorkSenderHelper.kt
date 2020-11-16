package com.kairlec.pusher.core.wework

import com.kairlec.pusher.core.PusherExceptions
import com.kairlec.pusher.core.Sender
import com.kairlec.pusher.pojo.wework.MediaID
import com.kairlec.pusher.pojo.wework.MediaTypeEnum
import com.kairlec.pusher.pojo.wework.message.*
import com.kairlec.utils.UrlBuilder
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.declaredMemberProperties


@Suppress("unused", "SpellCheckingInspection", "SameParameterValue")
open class WeWorkSenderHelper(private val validateCertificateChains: Boolean, private val accessTokenHelper: WeWorkAccessTokenHelper, private val agentid: Int) : WeWorkHelper {
    /**
     * Sender的附带设置,这个设置有默认,同时也可以另外传入
     * @param toUser [IToAble.touser]
     * @param toTag [IToAble.totag]
     * @param toParty [IToAble.toparty]
     * @param safe [ISaveAble.safe]
     * @param enableIdTrans [IEnableIdTransAble.enable_id_trans]
     * @param enableDuplicateCheck [IDuplicateCheckAble.enable_duplicate_check]
     * @param duplicateCheckInterval [IDuplicateCheckAble.duplicate_check_interval]
     */
    data class SenderSettings(
            var toUser: String = "@all",
            var toTag: String = "",
            var toParty: String = "",
            var safe: Int = 0,
            var enableIdTrans: Int = 0,
            var enableDuplicateCheck: Int = 0,
            var duplicateCheckInterval: Int = 1800
    ) {
        fun toUser(toUser: String) = apply { this.toUser = toUser }
        fun toTag(toTag: String) = apply { this.toTag = toTag }
        fun toParty(toParty: String) = apply { this.toParty = toParty }
        fun safe(safe: Int) = apply { this.safe = safe }
        fun enableIdTrans(enableIdTrans: Int) = apply { this.enableIdTrans = enableIdTrans }
        fun enableDuplicateCheck(enableDuplicateCheck: Int) = apply { this.enableDuplicateCheck = enableDuplicateCheck }
        fun duplicateCheckInterval(duplicateCheckInterval: Int) = apply { this.duplicateCheckInterval = duplicateCheckInterval }

        companion object {
            /**
             * 属性的名称和属性的map
             */
            private val propertyNames = SenderSettings::class.declaredMemberProperties
                    .associateBy { field ->
                        field.name
                                .replace("to[A-Z]".toRegex()) { "to${it.value[2].toLowerCase()}" }
                                .replace("[A-Z]".toRegex()) { "_${it.value.toLowerCase()}" }
                    }
        }

        /**
         * 覆盖,将当前设置项,覆盖至指定的类上
         * @param obj 要覆盖的类实例
         * @return 覆盖后的类实例
         */
        fun cover(obj: Any): Any {
            val objClass = obj::class
            objClass.declaredMemberProperties.forEach {
                propertyNames[it.name]?.let { property ->
                    if (it is KMutableProperty<*>) {
                        it.setter.call(obj, property.get(this))
                    }
                }
            }
            return obj
        }
    }

    /**
     * 默认设置
     */
    val defaultSettings = SenderSettings()

    /**
     * 复制默认设置
     */
    val withSettings get() = defaultSettings.copy()

    /**
     * 消息发送URL接口
     */
    private val url: String
        get() = UrlBuilder("https://qyapi.weixin.qq.com/cgi-bin/message/send")
                .addQueryParameter("access_token", accessToken)
                .build()

    /**
     * 鉴权token
     */
    private val accessToken: String
        get() = accessTokenHelper.accessToken

    /**
     * 重试机制,若发送报出Pusher异常,进行重试
     *
     * 若token过期,也会先进行更新token,再重试发送
     * @param count 重试次数
     * @param action 要执行的动作,动作出错则重试
     * @return action动作的结果
     */
    private inline fun <reified T> retry(count: Int = 2, crossinline action: () -> T): T {
        if (count > 0) {
            for (i in 0 until count) {
                try {
                    return action()
                } catch (e: PusherExceptions.SendMessageException) {
                    if (e.code == 42001) {
                        accessTokenHelper.update()
                    } else {
                        throw e
                    }
                }
            }
            throw PusherExceptions.RetryException(-1)
        } else {
            throw IllegalArgumentException("The number of retries cannot be less than 1")
        }
    }

    /**
     * 发送文本消息
     * @param content 文本内容
     * @param settings 发送设置,默认为Helper的设置
     */
    fun sendText(content: String, settings: SenderSettings = defaultSettings) {
        retry {
            Sender.postJsonResultMap<Unit, PusherExceptions.SendMessageException>(url, settings.cover(Text(
                    agentid,
                    Content(content),
            )), validateCertificateChains)
        }
    }

    /**
     * 发送图片消息
     * @param imageID 图像id,可事先根据上传临时素材[uploadMedia]得到
     * @param settings 发送设置,默认为Helper的设置
     * @see [MediaID]
     */
    fun sendImage(imageID: String, settings: SenderSettings = defaultSettings) {
        retry {
            Sender.postJsonResultMap<Unit, PusherExceptions.SendMessageException>(url, settings.cover(Image(
                    agentid,
                    Media(imageID),
            )), validateCertificateChains)
        }
    }

    /**
     * 发送语音消息
     * @param voiceID 语音id,可事先根据上传临时素材[uploadMedia]得到
     * @param settings 发送设置,默认为Helper的设置
     * @see [MediaID]
     */
    fun sendVoice(voiceID: String, settings: SenderSettings = defaultSettings) {
        retry {
            Sender.postJsonResultMap<Unit, PusherExceptions.SendMessageException>(url, settings.cover(Voice(
                    agentid,
                    Media(voiceID),
            )), validateCertificateChains)
        }
    }

    /**
     * 发送视频消息
     * @param videoID 视频id,可事先根据上传临时素材[uploadMedia]得到
     * @param title 视频标题
     * @param description 视频描述
     * @param settings 发送设置,默认为Helper的设置
     * @see [VideoMedia]
     */
    fun sendVideo(videoID: String, title: String? = null, description: String? = null, settings: SenderSettings = defaultSettings) {
        retry {
            Sender.postJsonResultMap<Unit, PusherExceptions.SendMessageException>(url, settings.cover(Video(
                    agentid,
                    VideoMedia(videoID).apply {
                        this.title = title
                        this.description = description
                    },
            )), validateCertificateChains)
        }
    }

    /**
     * 发送文件消息
     * @param fileID 文件id,可事先根据上传临时素材[uploadMedia]得到
     * @param settings 发送设置,默认为Helper的设置
     * @see [MediaID]
     */
    fun sendFile(fileID: String, settings: SenderSettings = defaultSettings) {
        retry {
            Sender.postJsonResultMap<Unit, PusherExceptions.SendMessageException>(url, settings.cover(File(
                    agentid,
                    Media(fileID),
            )), validateCertificateChains)
        }
    }

    /**
     * 发送文本卡片消息
     * @param settings 发送设置,默认为Helper的设置
     * @see [InnerTextCard]
     */
    fun sendTextCard(title: String, description: String, url: String, btntxt: String? = null, settings: SenderSettings = defaultSettings) {
        retry {
            Sender.postJsonResultMap<Unit, PusherExceptions.SendMessageException>(url, settings.cover(TextCard(
                    agentid,
                    InnerTextCard(title, description, url).apply {
                        this.btntxt = btntxt
                    },
            )), validateCertificateChains)
        }
    }

    /**
     * 发送新闻消息
     * @param settings 发送设置,默认为Helper的设置
     * @see [InnerNews]
     */
    fun sendNews(title: String, url: String, description: String? = null, picurl: String? = null, settings: SenderSettings = defaultSettings) {
        retry {
            Sender.postJsonResultMap<Unit, PusherExceptions.SendMessageException>(url, settings.cover(News(
                    agentid,
                    InnerNews(arrayOf(NewsArticles(title, url).apply {
                        this.description = description
                        this.picurl = picurl
                    })),
            )), validateCertificateChains)
        }
    }

    /**
     * 发送Mp新闻消息
     * @param settings 发送设置,默认为Helper的设置
     * @see [InnerMpNews]
     */
    fun sendMpNews(title: String, thumbMediaID: String, content: String, author: String? = null,
                   contentSourceUrl: String? = null, digest: String? = null, settings: SenderSettings = defaultSettings) {
        retry {
            Sender.postJsonResultMap<Unit, PusherExceptions.SendMessageException>(url, settings.cover(MpNews(
                    agentid,
                    InnerMpNews(arrayOf(MpNewsArticles(title, thumbMediaID, content).apply {
                        this.author = author
                        this.content_source_url = contentSourceUrl
                        this.digest = digest
                    })),
            )), validateCertificateChains)
        }
    }

    /**
     * 发送MarkDown消息
     * @param content markdown内容
     * @param settings 发送设置,默认为Helper的设置
     * @see [Markdown]
     */
    fun sendMarkdown(content: String, settings: SenderSettings = defaultSettings) {
        retry {
            Sender.postJsonResultMap<Unit, PusherExceptions.SendMessageException>(url, settings.cover(Markdown(
                    this.agentid,
                    Content(content),
            )), validateCertificateChains)
        }
    }

    /**
     * 发送任务卡片消息
     * @param settings 发送设置,默认为Helper的设置
     * @see [InnerTaskCard]
     */
    fun sendTaskCard(title: String,
                     description: String,
                     taskId: String,
                     btns: Array<Btn>,
                     btnurl: String? = null,
                     settings: SenderSettings = defaultSettings) {
        retry {
            Sender.postJsonResultMap<Unit, PusherExceptions.SendMessageException>(url, settings.cover(TaskCard(
                    this.agentid,
                    InnerTaskCard(
                            title,
                            description,
                            taskId,
                            btns
                    ).apply {
                        this.url = btnurl
                    }
            )), validateCertificateChains)
        }
    }

    /**
     * 上传媒体文件
     * @param file 文件内容
     * @param filename 文件名称
     * @param type 文件类型
     * @return 媒体ID
     */
    fun uploadMedia(file: ByteArray, filename: String, type: MediaTypeEnum): MediaID {
        return retry {
            if (file.size > type.maxSize) {
                throw PusherExceptions.UploadMediaException(-2, null, "File is to large")
            }
            val url = UrlBuilder("https://qyapi.weixin.qq.com/cgi-bin/media/upload")
                    .addQueryParameter("access_token", accessToken)
                    .addQueryParameter("type", type.typeString)
                    .build()
            return@retry Sender.uploadResultMap<MediaID, PusherExceptions.UploadMediaException>(url, file, filename, validateCertificateChains)
        }
    }

}