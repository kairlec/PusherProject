package com.kairlec.pusher.annotation.condition

import org.springframework.boot.autoconfigure.condition.AllNestedConditions
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Conditional
import org.springframework.context.annotation.ConfigurationCondition


class ReceiverCondition : AllNestedConditions(ConfigurationCondition.ConfigurationPhase.PARSE_CONFIGURATION) {
    @ConditionalOnProperty(prefix = "wework.receiver", value = ["enabled"], matchIfMissing = true)
    internal class ReceiverEnabled
}

class ReceiveDSLCondition : AllNestedConditions(ConfigurationCondition.ConfigurationPhase.PARSE_CONFIGURATION) {
    @Conditional(ReceiverCondition::class)
    internal class ReceiverEnabled

    @ConditionalOnProperty(prefix = "wework.receiverPluginDSLEnabled", value = ["enabled"], matchIfMissing = true)
    internal class ReceiverPluginDSLEnabled

    @Conditional(ReplyReceiveMsgCondition::class)
    internal class ReplyReceiveMsgEnabled
}
