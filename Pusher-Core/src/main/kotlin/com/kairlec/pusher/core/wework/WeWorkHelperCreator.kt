package com.kairlec.pusher.core.wework

import kotlin.reflect.full.primaryConstructor

/**
 * 企业微信应用辅助创建器
 * @param enterpriseID 企业ID
 * @param corpsecret 通讯录secret
 * @param validateCertificateChains 是否忽略证书校验
 */
class WeWorkEnterpriseHelperCreator(enterpriseID: String, corpsecret: String, val validateCertificateChains: Boolean) {
    val enterpriseAccessTokenHelper = WeWorkAccessTokenHelper(enterpriseID, corpsecret, validateCertificateChains)

    /**
     * 创建实例
     */
    inline fun <reified T : WeWorkHelper> newInstant(): T {
        val constructor = T::class.primaryConstructor
                ?: error("Cannot get class[${T::class.java.name}] primary constructor")
        return constructor.call(validateCertificateChains, enterpriseAccessTokenHelper)
    }
}

/**
 * 企业微信自定义小应用辅助创建器
 * @param enterpriseID 企业ID
 * @param applicationID 应用agentid
 * @param applicationKey 应用secret
 * @param validateCertificateChains 是否忽略证书校验
 */
class WeWorkApplicationHelperCreator(enterpriseID: String, val applicationID: Int, applicationKey: String, val validateCertificateChains: Boolean) {
    val applicationAccessTokenHelper = WeWorkAccessTokenHelper(enterpriseID, applicationKey, validateCertificateChains)

    /**
     * 创建实例
     */
    inline fun <reified T : WeWorkHelper> newInstant(): T {
        val constructor = T::class.primaryConstructor
                ?: error("Cannot get class[${T::class.java.name}] primary constructor")
        return constructor.call(validateCertificateChains, applicationAccessTokenHelper, applicationID)
    }
}