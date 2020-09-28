package com.kairlec.pusher.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class StatusCount(val error: Boolean = true, val success: Boolean = true)