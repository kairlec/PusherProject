package com.kairlec.pusher.core.wework

import com.kairlec.pusher.core.AccessTokenHelper
import com.kairlec.utils.UrlBuilder


@Suppress("SpellCheckingInspection")
class WeWorkAccessTokenHelper(corpid: String, corpsecret: String, validateCertificateChains: Boolean) : AccessTokenHelper(validateCertificateChains) {
    override val url: String = UrlBuilder("https://qyapi.weixin.qq.com/cgi-bin/gettoken")
            .addQueryParameter("corpid", corpid)
            .addQueryParameter("corpsecret", corpsecret)
            .build()

    init {
        update()
    }
}