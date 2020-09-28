package com.kairlec.pusher

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling


@SpringBootApplication
@EnableScheduling
class PusherServerApplication

fun main(args: Array<String>) {
    runApplication<PusherServerApplication>(*args)
}
