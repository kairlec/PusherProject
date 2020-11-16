package com.kairlec.pusher.core

import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.schedule
import kotlin.concurrent.withLock
import kotlin.properties.Delegates


/**
 * Token鉴权辅助器
 */
@Suppress("MemberVisibilityCanBePrivate", "SpellCheckingInspection")
abstract class AccessTokenHelper(protected open val validateCertificateChains: Boolean) {
    /**
     * token字符串
     */
    protected open lateinit var token: String

    /**
     * 下一次过期时间
     */
    protected open lateinit var expiredTime: LocalDateTime

    /**
     * 过期时间
     */
    protected open var expiresIn by Delegates.notNull<Long>()

    /**
     * 更新锁
     */
    protected open val updateLocker: Lock = ReentrantLock()

    /**
     * 更新url
     */
    protected abstract val url: String

    /**
     * 定时器
     */
    protected open var timer = nextTimer(1)

    /**
     * 更新定时器
     */
    protected fun nextTimer(delay: Long): TimerTask {
        return Timer("AccessTokenHelperUpdater", false).schedule(delay) {
            update()
        }
    }

    /**
     * 更新token
     */
    open fun update() {
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
            val jsonNode = try {
                objectMapper.readTree(tokenResult)
            } catch (e: Exception) {
                throw PusherExceptions.AccessTokenException(-1, e)
            }
            if (jsonNode["errcode"]?.asInt() ?: 0 != 0) {
                throw PusherExceptions.AccessTokenException(jsonNode["errcode"].asInt(), null, jsonNode["errmsg"].asText())
            }
            token = jsonNode["access_token"].asText()
            expiresIn = jsonNode["expires_in"].asLong()
            expiredTime = LocalDateTime.now().plusSeconds(expiresIn)
            timer.cancel()
            timer = nextTimer(expiresIn * 1000)
        }
    }

    /**
     * 获取token,若即将过期,则更新token后返回
     */
    val accessToken: String
        get() {
            updateLocker.withLock { }
            val duration = Duration.between(LocalDateTime.now(), expiredTime)
            if (duration.toMinutes() < 5) {
                timer.cancel()
                update()
            }
            return token
        }
}