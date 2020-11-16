package com.kairlec.pusher.config.properties

import com.kairlec.pusher.openapi.pojo.PusherUser
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "user")
//@PropertySources(
//        PropertySource("file:user.yml", ignoreResourceNotFound = true, factory = YAMLPropertySourceFactory::class),
//        PropertySource("file:user.properties", ignoreResourceNotFound = true)
//)
data class UserProperties(
        var admin: ArrayList<PusherUser> = ArrayList(),
        var department: ArrayList<String> = ArrayList(),
        var autoDisable: Boolean = false
)