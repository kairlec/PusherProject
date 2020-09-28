package com.kairlec.pusher.core.wechat

import kotlin.reflect.full.primaryConstructor

class WeChatHelperCreator(appid: String, secret: String, val validateCertificateChains: Boolean) {
    val accessTokenHelper = WeChatAccessTokenHelper(appid, secret, validateCertificateChains)
    inline fun <reified T : WeChatHelper> newInstant(): T {
        val constructor = T::class.primaryConstructor
                ?: error("Cannot get class[${T::class.java.name}] primary constructor")
        return constructor.call(validateCertificateChains, accessTokenHelper)
    }
}