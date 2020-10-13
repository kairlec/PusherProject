package com.kairlec.pusher.annotation.condition

import org.springframework.boot.autoconfigure.condition.AllNestedConditions
import org.springframework.context.annotation.Conditional
import org.springframework.context.annotation.ConfigurationCondition



class ReplyReceiveMsgCondition : AllNestedConditions(ConfigurationCondition.ConfigurationPhase.PARSE_CONFIGURATION) {
    @Conditional(PusherCondition::class)
    internal class PusherEnabled

    @Conditional(ReceiverCondition::class)
    internal class ReceiverEnabled
}