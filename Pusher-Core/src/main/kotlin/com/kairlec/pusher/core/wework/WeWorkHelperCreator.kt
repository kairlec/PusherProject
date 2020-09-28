package com.kairlec.pusher.core.wework

import kotlin.reflect.full.primaryConstructor

class WeWorkEnterpriseHelperCreator(enterpriseID: String, corpsecret: String, val validateCertificateChains: Boolean) {
    val enterpriseAccessTokenHelper = WeWorkAccessTokenHelper(enterpriseID, corpsecret, validateCertificateChains)
    inline fun <reified T : WeWorkHelper> newInstant(): T {
        val constructor = T::class.primaryConstructor
                ?: error("Cannot get class[${T::class.java.name}] primary constructor")
        return constructor.call(validateCertificateChains, enterpriseAccessTokenHelper)
        //val constructor = T::class.java.getConstructor(Boolean::class.java, WeWorkAccessTokenHelper::class.java)
        //return constructor.newInstance(validateCertificateChains, enterpriseAccessTokenHelper)
    }
}

class WeWorkApplicationHelperCreator(enterpriseID: String, val applicationID: Int, applicationKey: String, val validateCertificateChains: Boolean) {
    val applicationAccessTokenHelper = WeWorkAccessTokenHelper(enterpriseID, applicationKey, validateCertificateChains)
    inline fun <reified T : WeWorkHelper> newInstant(): T {
        val constructor = T::class.primaryConstructor
                ?: error("Cannot get class[${T::class.java.name}] primary constructor")
        return constructor.call(validateCertificateChains, applicationAccessTokenHelper, applicationID)
        //val constructor = T::class.java.getConstructor(Boolean::class.java, WeWorkAccessTokenHelper::class.java, Int::class.java)
        //return constructor.newInstance(validateCertificateChains, applicationAccessTokenHelper, applicationID)
    }
}