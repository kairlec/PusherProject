package com.kairlec.pusher.config

import com.kairlec.pojo.PusherUser
import com.kairlec.pusher.annotation.condition.PusherCondition
import com.kairlec.pusher.config.properties.PusherUsersProperties
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Conditional
import org.springframework.context.annotation.Configuration

@Configuration
@Conditional(PusherCondition::class)
class PusherUsersAutoConfiguration {

    @Autowired
    private lateinit var pusherUsersProperties: PusherUsersProperties

    private val logger = LoggerFactory.getLogger(PusherUsersAutoConfiguration::class.java)

    @Bean("PusherUsers")
    fun pusherUsers(): Map<String, PusherUser> {
        if(pusherUsersProperties.users.isEmpty()){
           logger.warn("wework push is enabled but users count is 0!")
        }
        return pusherUsersProperties.users.mapValues {
            val value = it.value as LinkedHashMap<*, *>
            return@mapValues PusherUser(value["token"] as String, value["admin"] as Boolean)
        }
    }
}