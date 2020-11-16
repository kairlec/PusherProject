package com.kairlec.pusher.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.context.annotation.Conditional
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Validated
@ConstructorBinding
@ConfigurationProperties(prefix = "wework.config")
data class PusherProperties(
        @NotBlank(message = "enterpriseID(企业ID)不能为空")
        val enterpriseID: String,

        @NotBlank(message = "applicationkey(应用密钥)不能为空")
        val applicationkey: String,

        @NotNull(message = "applicationID(应用ID)不能为空")
        val applicationID: Int
)