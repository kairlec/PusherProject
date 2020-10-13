package com.kairlec.pusher.config.interceptor.pusher

import com.kairlec.pusher.annotation.condition.PusherCondition
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Conditional
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@Conditional(PusherCondition::class)
class PushInterceptorConfiguration:WebMvcConfigurer{
    @Bean
    fun pusherInterceptorFactory(): PusherInterceptor {
        return PusherInterceptor()
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(pusherInterceptorFactory()).addPathPatterns("/push/**")
    }
}