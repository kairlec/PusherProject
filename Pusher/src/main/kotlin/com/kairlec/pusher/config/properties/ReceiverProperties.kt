package com.kairlec.pusher.config.properties

import com.kairlec.pusher.annotation.condition.PusherCondition
import com.kairlec.pusher.annotation.condition.ReceiverCondition
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Conditional
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotBlank

@Validated
@Configuration
@ConfigurationProperties(prefix = "wework.config")
@Conditional(ReceiverCondition::class)
data class ReceiverProperties(
        @NotBlank(message = "applicationReceivedToken(应用消息接收器token)不能为空")
        var applicationReceivedToken: String = "",

        @NotBlank(message = "applicationEncodingAESKey(应用消息接受AES密钥)不能为空")
        var applicationEncodingAESKey: String = "",

        @NotBlank(message = "enterpriseID(企业ID)不能为空")
        var enterpriseID: String = "",

        @NotBlank(message = "接受消息插件路径不能位空")
        var receiverPluginLocation: String = "",

        @NotBlank(message = "接受消息事件类不能为空")
        var receiverClassName: String = "",

        var receiverPluginDSLEnabled: Boolean = true
)
