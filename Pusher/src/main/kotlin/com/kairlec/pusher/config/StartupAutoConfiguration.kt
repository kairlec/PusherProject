package com.kairlec.pusher.config


import com.kairlec.pusher.pojo.Plugin
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.context.ApplicationContext
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component


@Component
@Order(99999)
class StartupAutoConfiguration : CommandLineRunner {
    private val logger = LoggerFactory.getLogger(javaClass.name)

    @Autowired
    private lateinit var applicationContext: ApplicationContext

    override fun run(vararg args: String) {
        Plugin.Invoker.onStartup(context = applicationContext)
    }
}