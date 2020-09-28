package com.kairlec.pusher.core.wechat

import com.kairlec.pusher.core.PusherExceptions
import com.kairlec.pusher.core.Sender
import com.kairlec.utils.UrlBuilder

open class WeChatSenderHelper(private val validateCertificateChains: Boolean, private val accessTokenHelper: WeChatAccessTokenHelper) : WeChatHelper {
    private val url: String
        get() = UrlBuilder("https://api.weixin.qq.com/cgi-bin/message/mass/sendall")
                .addQueryParameter("access_token", accessTokenHelper.accessToken)
                .build()

    fun sendMpNews(mediaID: String, sendIgnoreReprint: Boolean, tagId: Int? = null) {
        Sender.postJsonResultMap<Unit, PusherExceptions.SendMessageException>(url, object {
            val filter: Any = if (tagId != null) {
                object {
                    val is_to_all = false
                    val tag_id = tagId
                }
            } else {
                object {
                    val is_to_all = true
                }
            }
            val mpnews = object {
                val media_id = mediaID
            }
            val msgtype = "mpnews"
            val send_ignore_reprint = if (sendIgnoreReprint) 1 else 0
        }, validateCertificateChains)
    }

    fun sendText(content: String, tagId: Int? = null): String {
        return Sender.postJsonResultMap<String, PusherExceptions.SendMessageException>(url, object {
            val filter: Any = if (tagId != null) {
                object {
                    val is_to_all = false
                    val tag_id = tagId
                }
            } else {
                object {
                    val is_to_all = true
                }
            }
            val msgtype = "text"
            val text = object {
                val content = content
            }
        }, validateCertificateChains, "msg_id")
    }

    fun sendVoice(mediaID: String, tagId: Int? = null): String {
        return Sender.postJsonResultMap<String, PusherExceptions.SendMessageException>(url, object {
            val filter: Any = if (tagId != null) {
                object {
                    val is_to_all = false
                    val tag_id = tagId
                }
            } else {
                object {
                    val is_to_all = true
                }
            }
            val msgtype = "voice"
            val voice = object {
                val media_id = mediaID
            }
        }, validateCertificateChains, "msg_id")
    }

    fun sendImage(mediaIDs: Iterable<String>, recommend: String = "分享图片", tagId: Int? = null): String {
        return Sender.postJsonResultMap<String, PusherExceptions.SendMessageException>(url, object {
            val filter: Any = if (tagId != null) {
                object {
                    val is_to_all = false
                    val tag_id = tagId
                }
            } else {
                object {
                    val is_to_all = true
                }
            }
            val msgtype = "image"
            val images = object {
                val media_ids = mediaIDs
                val recommend = recommend
                val need_open_comment = 1
                val only_fans_can_comment = 0
            }
        }, validateCertificateChains, "msg_id")
    }

    fun sendVideo(mediaID: String, tagId: Int? = null): String {
        return Sender.postJsonResultMap<String, PusherExceptions.SendMessageException>(url, object {
            val filter: Any = if (tagId != null) {
                object {
                    val is_to_all = false
                    val tag_id = tagId
                }
            } else {
                object {
                    val is_to_all = true
                }
            }
            val msgtype = "mpvideo"
            val mpvideo = object {
                val media_id = mediaID
            }
        }, validateCertificateChains, "msg_id")
    }

    fun sendWXCard(cardID: String, tagId: Int? = null): String {
        return Sender.postJsonResultMap<String, PusherExceptions.SendMessageException>(url, object {
            val filter: Any = if (tagId != null) {
                object {
                    val is_to_all = false
                    val tag_id = tagId
                }
            } else {
                object {
                    val is_to_all = true
                }
            }
            val msgtype = "wxcard"
            val wxcard = object {
                val card_id = cardID
            }
        }, validateCertificateChains, "msg_id")
    }

}