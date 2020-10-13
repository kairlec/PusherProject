package com.kairlec.pusher.config.properties

import com.kairlec.pusher.annotation.condition.AddressBookCondition
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.context.annotation.Conditional
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotBlank


@Validated
@ConstructorBinding
@ConfigurationProperties(prefix = "wework.config")
@Conditional(AddressBookCondition::class)
data class AddressBookProperties(
        @NotBlank(message = "enterpriseID(企业ID)不能为空")
        val enterpriseID: String,

        @NotBlank(message = "enterpriseSecret(企业通讯录密钥)不能为空")
        val enterpriseSecret: String
)