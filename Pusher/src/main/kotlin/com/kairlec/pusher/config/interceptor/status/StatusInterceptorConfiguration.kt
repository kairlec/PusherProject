package com.kairlec.pusher.config.interceptor.status

import com.kairlec.pusher.annotation.condition.StatusReportCondition
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Conditional
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@Conditional(StatusReportCondition::class)
class StatusInterceptorConfiguration : WebMvcConfigurer {
    @Bean
    fun statusInterceptorFactory(): StatusInterceptor {
        return StatusInterceptor()
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(statusInterceptorFactory()).addPathPatterns("/push/**").addPathPatterns("/*").addPathPatterns("*")
    }

}