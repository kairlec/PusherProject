package com.kairlec.pusher.config

import com.kairlec.pusher.annotation.condition.PusherCondition
import com.kairlec.pusher.config.properties.PusherProperties
import com.kairlec.pusher.core.wework.WeWorkApplicationHelperCreator
import com.kairlec.pusher.core.wework.WeWorkSenderHelper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Conditional
import org.springframework.context.annotation.Configuration


@Configuration
@EnableConfigurationProperties(PusherProperties::class)
@Conditional(PusherCondition::class)
class WeWorkApplicationHelperCreatorAutoConfiguration(private val pushProperties: PusherProperties) {
    @Bean("WeWorkApplicationHelperCreator")
    @ConditionalOnMissingBean(WeWorkApplicationHelperCreator::class)
    fun serverSender(): WeWorkApplicationHelperCreator {
        return WeWorkApplicationHelperCreator(pushProperties.enterpriseID, pushProperties.applicationID, pushProperties.applicationkey, true)
    }
}

@Configuration
@Conditional(PusherCondition::class)
class PusherAutoConfiguration {

    @Autowired
    private lateinit var weWorkApplicationHelperCreator: WeWorkApplicationHelperCreator

    @Bean("WeWorkSenderHelper")
    @ConditionalOnMissingBean(WeWorkSenderHelper::class)
    fun serverSender(): WeWorkSenderHelper {
        return weWorkApplicationHelperCreator.newInstant()
    }
}