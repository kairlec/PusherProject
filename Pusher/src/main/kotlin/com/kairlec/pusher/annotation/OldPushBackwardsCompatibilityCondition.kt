package com.kairlec.pusher.annotation

import org.springframework.boot.autoconfigure.condition.AllNestedConditions
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.ConfigurationCondition

class OldPushBackwardsCompatibilityCondition : AllNestedConditions(ConfigurationCondition.ConfigurationPhase.PARSE_CONFIGURATION) {
    @ConditionalOnProperty(prefix = "wework.push", value = ["enabled"], matchIfMissing = true)
    internal class EnablePush
    @ConditionalOnProperty(prefix = "wework.config", value = ["backwardsCompatibility"], matchIfMissing = false)
    internal class EnableBackwardsCompatibility
}