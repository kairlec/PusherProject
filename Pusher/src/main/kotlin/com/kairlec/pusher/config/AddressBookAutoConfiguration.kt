package com.kairlec.pusher.config

import com.kairlec.pusher.core.wework.WeWorkAddressBookHelper
import com.kairlec.pusher.core.wework.WeWorkEnterpriseHelperCreator
import com.kairlec.pusher.config.properties.AddressBookProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(AddressBookProperties::class)
@ConditionalOnProperty(prefix = "wework.addressbook", value = ["enabled"], matchIfMissing = true)
class WeWorkEnterpriseHelperAutoConfiguration(private val addressBookProperties: AddressBookProperties) {

    @Bean("WeWorkEnterpriseHelperCreator")
    @ConditionalOnMissingBean(WeWorkEnterpriseHelperCreator::class)
    fun weWorkEnterpriseHelperCreator(): WeWorkEnterpriseHelperCreator {
        return WeWorkEnterpriseHelperCreator(addressBookProperties.enterpriseID, addressBookProperties.enterpriseSecret, true)
    }

}

@Configuration
@ConditionalOnProperty(prefix = "wework.addressbook", value = ["enabled"], matchIfMissing = true)
class AddressBookAutoConfiguration {

    @Autowired
    private lateinit var weWorkEnterpriseHelperCreator: WeWorkEnterpriseHelperCreator

    @Bean("WeWorkAddressBook")
    @ConditionalOnMissingBean(WeWorkAddressBookHelper::class)
    fun weWorkAddressBook(): WeWorkAddressBookHelper {
        return weWorkEnterpriseHelperCreator.newInstant()
    }
}