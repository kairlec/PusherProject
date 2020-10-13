package com.kairlec.pusher.annotation.condition

import org.springframework.boot.autoconfigure.condition.AllNestedConditions
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.ConfigurationCondition

class AddressBookCondition : AllNestedConditions(ConfigurationCondition.ConfigurationPhase.PARSE_CONFIGURATION) {
    @ConditionalOnProperty(prefix = "wework.addressbook", value = ["enabled"], matchIfMissing = true)
    internal class AddressBookEnabled
}