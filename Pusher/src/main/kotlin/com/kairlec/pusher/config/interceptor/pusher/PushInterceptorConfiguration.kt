package com.kairlec.pusher.config.interceptor.pusher

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@ConditionalOnProperty(prefix = "wework.push", value = ["enabled"], matchIfMissing = true)
class PushInterceptorConfiguration:WebMvcConfigurer{
    @Bean
    fun pusherInterceptorFactory(): PusherInterceptor {
        return PusherInterceptor()
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(pusherInterceptorFactory()).addPathPatterns("/push/**")
    }
}