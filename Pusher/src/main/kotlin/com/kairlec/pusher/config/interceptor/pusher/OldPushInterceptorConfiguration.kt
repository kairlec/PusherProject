package com.kairlec.pusher.config.interceptor.pusher

import com.kairlec.pusher.annotation.OldPushBackwardsCompatibilityCondition
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Conditional
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@Conditional(OldPushBackwardsCompatibilityCondition::class)
class OldPushInterceptorConfiguration : WebMvcConfigurer {
    @Bean
    fun oldPusherInterceptorFactory(): OldPusherInterceptor {
        return OldPusherInterceptor()
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(oldPusherInterceptorFactory()).addPathPatterns("/*")
    }
}