package com.kairlec.pusher.config.interceptor.status

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@ConditionalOnProperty(prefix = "wework.push", value = ["enabled", "status.enabled"], matchIfMissing = true)
class StatusInterceptorConfiguration: WebMvcConfigurer {
    @Bean
    fun statusInterceptorFactory(): StatusInterceptor {
        return StatusInterceptor()
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(statusInterceptorFactory()).addPathPatterns("/push/**").addPathPatterns("/*").addPathPatterns("*")
    }
}