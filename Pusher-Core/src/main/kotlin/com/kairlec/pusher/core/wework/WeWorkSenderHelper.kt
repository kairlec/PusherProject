package com.kairlec.pusher.core.wework

import com.kairlec.pojo.wework.MediaID
import com.kairlec.pojo.wework.MediaTypeEnum
import com.kairlec.pusher.core.PusherExceptions
import com.kairlec.pusher.core.Sender
import com.kairlec.utils.UrlBuilder

@Suppress("unused", "SpellCheckingInspection", "SameParameterValue")
open class WeWorkSenderHelper(private val validateCertificateChains: Boolean, private val accessTokenHelper: WeWorkAccessTokenHelper, private val agentid: Int) : WeWorkHelper {
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
    }


    val defaultSettings = SenderSettings()

    val withSettings get() = defaultSettings.copy()

    private val url: String
        get() = UrlBuilder("https://qyapi.weixin.qq.com/cgi-bin/message/send")
                .addQueryParameter("access_token", accessToken)
                .build()

    private val accessToken: String
        get() = accessTokenHelper.accessToken

    fun sendText(content: String, settings: SenderSettings = defaultSettings) {
        Sender.postJsonResultMap<Unit, PusherExceptions.SendMessageException>(url, object {
            val touser = settings.toUser
            val toparty = settings.toParty
            val totag = settings.toTag
            val msgtype = "text"
            val agentid = this@WeWorkSenderHelper.agentid
            val text = object {
                val content = content
            }
            val safe = settings.safe
            val enable_id_trans = settings.enableIdTrans
            val enable_duplicate_check = settings.enableDuplicateCheck
            val duplicate_check_interval = settings.duplicateCheckInterval
        }, validateCertificateChains)
    }

    fun sendImage(imageID: String, settings: SenderSettings = defaultSettings) {
        Sender.postJsonResultMap<Unit, PusherExceptions.SendMessageException>(url, object {
            val touser = settings.toUser
            val toparty = settings.toParty
            val totag = settings.toTag
            val msgtype = "image"
            val agentid = this@WeWorkSenderHelper.agentid
            val image = object {
                val media_id = imageID
            }
            val safe = settings.safe
            val enable_duplicate_check = settings.enableDuplicateCheck
            val duplicate_check_interval = settings.duplicateCheckInterval
        }, validateCertificateChains)
    }

    fun sendVoice(voiceID: String, settings: SenderSettings = defaultSettings) {
        Sender.postJsonResultMap<Unit, PusherExceptions.SendMessageException>(url, object {
            val touser = settings.toUser
            val toparty = settings.toParty
            val totag = settings.toTag
            val msgtype = "voice"
            val agentid = this@WeWorkSenderHelper.agentid
            val voice = object {
                val media_id = voiceID
            }
            val enable_duplicate_check = settings.enableDuplicateCheck
            val duplicate_check_interval = settings.duplicateCheckInterval
        }, validateCertificateChains)
    }

    fun sendVideo(videoID: String, title: String? = null, description: String? = null, settings: SenderSettings = defaultSettings) {
        Sender.postJsonResultMap<Unit, PusherExceptions.SendMessageException>(url, object {
            val touser = settings.toUser
            val toparty = settings.toParty
            val totag = settings.toTag
            val msgtype = "video"
            val agentid = this@WeWorkSenderHelper.agentid
            val video = object {
                val media_id = videoID
                val title = title
                val description = description
            }
            val safe = settings.safe
            val enable_duplicate_check = settings.enableDuplicateCheck
            val duplicate_check_interval = settings.duplicateCheckInterval
        }, validateCertificateChains)
    }

    fun sendFile(fileID: String, settings: SenderSettings = defaultSettings) {
        Sender.postJsonResultMap<Unit, PusherExceptions.SendMessageException>(url, object {
            val touser = settings.toUser
            val toparty = settings.toParty
            val totag = settings.toTag
            val msgtype = "file"
            val agentid = this@WeWorkSenderHelper.agentid
            val file = object {
                val media_id = fileID
            }
            val safe = settings.safe
            val enable_duplicate_check = settings.enableDuplicateCheck
            val duplicate_check_interval = settings.duplicateCheckInterval
        }, validateCertificateChains)
    }

    fun sendTextCard(title: String, description: String, url: String, btntxt: String? = null, settings: SenderSettings = defaultSettings) {
        Sender.postJsonResultMap<Unit, PusherExceptions.SendMessageException>(url, object {
            val touser = settings.toUser
            val toparty = settings.toParty
            val totag = settings.toTag
            val msgtype = "textcard"
            val agentid = this@WeWorkSenderHelper.agentid
            val textcard = object {
                val title = title
                val description = description
                val url = url
                val btntxt = btntxt
            }
            val enable_id_trans = settings.enableIdTrans
            val enable_duplicate_check = settings.enableDuplicateCheck
            val duplicate_check_interval = settings.duplicateCheckInterval
        }, validateCertificateChains)
    }


    fun sendNews(title: String, url: String, description: String? = null, picurl: String? = null, settings: SenderSettings = defaultSettings) {
        Sender.postJsonResultMap<Unit, PusherExceptions.SendMessageException>(url, object {
            val touser = settings.toUser
            val toparty = settings.toParty
            val totag = settings.toTag
            val msgtype = "news"
            val agentid = this@WeWorkSenderHelper.agentid
            val news = object {
                val articles = arrayOf(object {
                    val title = title
                    val description = description
                    val url = url
                    val picurl = picurl
                })
            }
            val enable_id_trans = settings.enableIdTrans
            val enable_duplicate_check = settings.enableDuplicateCheck
            val duplicate_check_interval = settings.duplicateCheckInterval
        }, validateCertificateChains)
    }

    fun sendMpNews(title: String, thumbMediaID: String, content: String, author: String? = null,
                   contentSourceUrl: String? = null, digest: String? = null, settings: SenderSettings = defaultSettings) {
        Sender.postJsonResultMap<Unit, PusherExceptions.SendMessageException>(url, object {
            val touser = settings.toUser
            val toparty = settings.toParty
            val totag = settings.toTag
            val msgtype = "mpnews"
            val agentid = this@WeWorkSenderHelper.agentid
            val news = object {
                val articles = arrayOf(object {
                    val title = title
                    val thumb_media_id = thumbMediaID
                    val author = author
                    val content_source_url = contentSourceUrl
                    val content = content
                    val digest = digest
                })
            }
            val enable_id_trans = settings.enableIdTrans
            val enable_duplicate_check = settings.enableDuplicateCheck
            val duplicate_check_interval = settings.duplicateCheckInterval
        }, validateCertificateChains)
    }

    fun sendMarkdown(content: String, settings: SenderSettings = defaultSettings) {
        Sender.postJsonResultMap<Unit, PusherExceptions.SendMessageException>(url, object {
            val touser = settings.toUser
            val toparty = settings.toParty
            val totag = settings.toTag
            val msgtype = "markdown"
            val agentid = this@WeWorkSenderHelper.agentid
            val markdown = object {
                val content = content
            }
            val enable_duplicate_check = settings.enableDuplicateCheck
            val duplicate_check_interval = settings.duplicateCheckInterval
        }, validateCertificateChains)
    }

    fun uploadMedia(file: ByteArray, filename: String, type: MediaTypeEnum): MediaID {
        if (file.size > type.maxSize) {
            throw PusherExceptions.UploadMediaException(-2, null, "File is to large")
        }
        val url = UrlBuilder("https://qyapi.weixin.qq.com/cgi-bin/media/upload")
                .addQueryParameter("access_token", accessToken)
                .addQueryParameter("type", type.typeString)
                .build()
        return Sender.uploadResultMap<MediaID, PusherExceptions.UploadMediaException>(url, file, filename, validateCertificateChains)
    }
}