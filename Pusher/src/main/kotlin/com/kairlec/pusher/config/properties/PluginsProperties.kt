package com.kairlec.pusher.config.properties

import com.kairlec.pusher.annotation.condition.PluginsCondition
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.context.annotation.Conditional
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotBlank

@Validated
@ConstructorBinding
@ConfigurationProperties(prefix = "wework.config.plugins")
@Conditional(PluginsCondition::class)
data class PluginsProperties(
        @NotBlank(message = "pluginsDir(插件文件夹路径)不能为空")
        val pluginsDir: String,

        @NotBlank(message = "pluginPackageName(插件包名)不能为空")
        val pluginPackageName:String,
)