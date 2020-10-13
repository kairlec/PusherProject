package com.kairlec.pusher.annotation.condition

import org.springframework.boot.autoconfigure.condition.AllNestedConditions
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Conditional
import org.springframework.context.annotation.ConfigurationCondition

class StatusReportCondition : AllNestedConditions(ConfigurationCondition.ConfigurationPhase.PARSE_CONFIGURATION) {
    @Conditional(PusherCondition::class)
    internal class PusherEnabled

    @ConditionalOnProperty(prefix = "wework.push.status", value = ["enabled"], matchIfMissing = true)
    internal class StatusEnabled
}