package com.kairlec.pusher.config

import com.kairlec.pusher.config.properties.WeChatPusherProperties
import com.kairlec.pusher.core.wechat.WeChatHelperCreator
import com.kairlec.pusher.core.wechat.WeChatTemplateHelper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
@EnableConfigurationProperties(WeChatPusherProperties::class)
@ConditionalOnProperty(prefix = "test.wechat.push", value = ["enabled"], matchIfMissing = false)
class WeChatHelperCreatorAutoConfiguration(private val weChatPusherProperties: WeChatPusherProperties) {
    private val logger = LoggerFactory.getLogger(WeChatHelperCreatorAutoConfiguration::class.java)

    @Bean("WeChatHelperCreator")
    @ConditionalOnMissingBean(WeChatHelperCreator::class)
    fun weChatHelperCreator(): WeChatHelperCreator {
        logger.info(weChatPusherProperties.toString())
        return WeChatHelperCreator(weChatPusherProperties.wechatAppid, weChatPusherProperties.wechatSecret, true)
    }
}

@Configuration
@ConditionalOnProperty(prefix = "test.wechat.push", value = ["enabled"], matchIfMissing = false)
class WeChatPusherAutoConfiguration {

    @Autowired
    private lateinit var weChatHelperCreator: WeChatHelperCreator

    @Bean("WeChatTemplateHelper")
    @ConditionalOnMissingBean(WeChatTemplateHelper::class)
    fun weChatTemplateHelper(): WeChatTemplateHelper {
        return weChatHelperCreator.newInstant()
    }
}