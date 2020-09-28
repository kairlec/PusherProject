package com.kairlec.pusher.config

import com.kairlec.pusher.config.properties.ReceiverProperties
import com.kairlec.pusher.receiver.ReceiveInterface
import com.kairlec.pusher.receiver.dsl.ReceiveDSL
import com.qq.weixin.mp.aes.WXBizMsgCrypt
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.File
import java.net.URLClassLoader


@Configuration
@ConditionalOnProperty(prefix = "wework.receiver", value = ["enabled"], matchIfMissing = true)
class ReceiverAutoConfiguration {

    @Autowired
    private lateinit var receiverProperties: ReceiverProperties

    @Bean("WXBizMsgCrypt")
    @ConditionalOnMissingBean(WXBizMsgCrypt::class)
    fun wxBizMsgCryptCreator(): WXBizMsgCrypt {
        return WXBizMsgCrypt(receiverProperties.applicationReceivedToken, receiverProperties.applicationEncodingAESKey, receiverProperties.enterpriseID)
    }

    @Bean("ReceiveInterface")
    @ConditionalOnMissingBean(ReceiveInterface::class)
    fun receiveInterfaceCreator(): ReceiveInterface {
        val fileUrl = File(receiverProperties.receiverPluginLocation).toURI().toURL()
        val child = URLClassLoader(arrayOf(fileUrl), this::class.java.classLoader)
        val clazz = Class.forName(receiverProperties.receiverClassName, true, child)
        return clazz.getConstructor().newInstance() as ReceiveInterface
    }

}


@Configuration
@ConditionalOnProperty(prefix = "wework", value = ["receiver.enabled", "receiverPluginDSLEnabled.enabled"], matchIfMissing = true)
class ReceiveDSLAutoConfiguration {

    @Autowired
    private lateinit var receiver: ReceiveInterface

    @Bean("ReceiveDSLImpl")
    @ConditionalOnMissingBean(ReceiveDSL::class)
    fun receiveDSLImplCreator(): ReceiveDSL {
        val receiveDSL = ReceiveDSL()
        receiver.onReceiveDSL(receiveDSL)
        return receiveDSL
    }
}