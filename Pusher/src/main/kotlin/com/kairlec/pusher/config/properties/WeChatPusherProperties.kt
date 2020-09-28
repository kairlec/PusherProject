package com.kairlec.pusher.config.properties

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotBlank

@Validated
@ConstructorBinding
@ConfigurationProperties(prefix = "test.wechat.config")
@ConditionalOnProperty(prefix = "test.wechat.push", value = ["enabled"], matchIfMissing = false)
data class WeChatPusherProperties(
        @NotBlank(message = "wechatAppid(微信公众ID)不能为空")
        val wechatAppid: String,

        @NotBlank(message = "wechatSecret(企业通讯录密钥)不能为空")
        val wechatSecret: String,

        @NotBlank(message = "templateID(模板ID)不能为空")
        val templateID: String
)