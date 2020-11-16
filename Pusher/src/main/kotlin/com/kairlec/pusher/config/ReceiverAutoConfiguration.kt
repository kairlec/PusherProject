package com.kairlec.pusher.config

import com.kairlec.pusher.config.properties.ReceiverProperties
import com.qq.weixin.mp.aes.WXBizMsgCrypt
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class ReceiverAutoConfiguration {

    @Autowired
    private lateinit var receiverProperties: ReceiverProperties

    @Bean("WXBizMsgCrypt")
    @ConditionalOnMissingBean(WXBizMsgCrypt::class)
    fun wxBizMsgCryptCreator(): WXBizMsgCrypt {
        return WXBizMsgCrypt(receiverProperties.applicationReceivedToken, receiverProperties.applicationEncodingAESKey, receiverProperties.enterpriseID)
    }


}
