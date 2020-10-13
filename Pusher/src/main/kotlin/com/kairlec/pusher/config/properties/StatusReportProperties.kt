package com.kairlec.pusher.config.properties

import com.kairlec.pusher.annotation.condition.StatusReportCondition
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Conditional
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotBlank

@Validated
@Configuration
@ConfigurationProperties(prefix = "wework.push.status")
@Conditional(StatusReportCondition::class)
data class StatusReportProperties(
        @NotBlank(message = "Cron表达式不能为空")
        var cron: String = "",

        var error: Boolean = false,

        var success: Boolean = false
)