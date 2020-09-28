package com.kairlec.pusher.core.wechat

import com.kairlec.pusher.core.AccessTokenHelper
import com.kairlec.utils.UrlBuilder


@Suppress("SpellCheckingInspection")
class WeChatAccessTokenHelper(appid: String, secret: String, validateCertificateChains: Boolean) : AccessTokenHelper(validateCertificateChains) {
    override val url: String = UrlBuilder("https://api.weixin.qq.com/cgi-bin/token")
            .addQueryParameter("grant_type", "client_credential")
            .addQueryParameter("appid", appid)
            .addQueryParameter("secret", secret)
            .build()
    init {
        update()
    }
}