package com.kairlec.pusher.annotation.condition

import org.springframework.boot.autoconfigure.condition.AllNestedConditions
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.ConfigurationCondition

class PusherCondition : AllNestedConditions(ConfigurationCondition.ConfigurationPhase.PARSE_CONFIGURATION) {
    @ConditionalOnProperty(prefix = "wework.pusher", value = ["enabled"], matchIfMissing = true)
    internal class PusherEnabled
}