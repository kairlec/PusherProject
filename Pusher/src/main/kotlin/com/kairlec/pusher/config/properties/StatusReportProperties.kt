package com.kairlec.pusher.config.properties

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotBlank

@Validated
@Configuration
@ConfigurationProperties(prefix = "wework.push.status")
@ConditionalOnProperty(prefix = "wework.push", value = ["enabled", "status.enabled"], matchIfMissing = true)
data class StatusReportProperties(
        @NotBlank(message = "Cron表达式不能为空")
        var cron: String = "",

        var error: Boolean = false,

        var success: Boolean = false
)