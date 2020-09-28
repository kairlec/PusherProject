package com.kairlec.pusher.core

import com.fasterxml.jackson.databind.JsonNode
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.properties.Delegates


@Suppress("MemberVisibilityCanBePrivate", "SpellCheckingInspection")
abstract class AccessTokenHelper(protected open val validateCertificateChains: Boolean) {
    protected open lateinit var token: String
    protected open lateinit var expiredTime: LocalDateTime
    protected open var expiresIn by Delegates.notNull<Long>()
    protected open val scheduledExecutorService: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    protected open lateinit var currentScheduledFuture: ScheduledFuture<*>
    protected open val updateLocker: Lock = ReentrantLock()
    protected abstract val url: String
    protected open fun update() {
        updateLocker.withLock {
            var tokenResult = ""
            Sender.get(url, validateCertificateChains)
                    .whenComplete { httpResponse, throwable ->
                        if (throwable != null) {
                            throw PusherExceptions.AccessTokenException(-1, throwable)
                        } else {
                            tokenResult = httpResponse.body()
                        }
                    }.join()
            val jsonNode: JsonNode
            try {
                jsonNode = objectMapper.readTree(tokenResult)
            } catch (e: Exception) {
                throw PusherExceptions.AccessTokenException(-1, e)
            }
            if (jsonNode["errcode"]?.asInt() ?: 0 != 0) {
                throw PusherExceptions.AccessTokenException(jsonNode["errcode"].asInt(), null, jsonNode["errmsg"].asText())
            }
            token = jsonNode["access_token"].asText()
            expiresIn = jsonNode["expires_in"].asLong()
            expiredTime = LocalDateTime.now().plusSeconds(expiresIn)
            currentScheduledFuture = scheduledExecutorService.schedule({
                update()
            }, expiresIn, TimeUnit.SECONDS)
        }
    }

    val accessToken: String
        get() {
            updateLocker.withLock { }
            val duration = Duration.between(LocalDateTime.now(), expiredTime)
            if (duration.toMinutes() < 5) {
                if (!currentScheduledFuture.isDone && !currentScheduledFuture.isCancelled) {
                    currentScheduledFuture.cancel(true)
                }
                update()
            }
            return token
        }
}