package com.kairlec.pusher.service

import com.kairlec.pusher.openapi.pojo.PusherUser
import com.kairlec.pusher.openapi.pojo.PusherUserPushConfig
import org.springframework.stereotype.Service


@Service
interface UserService {
    fun match(userid: String? = null, username: String? = null, openid: String? = null): List<PusherUser>

    fun matchWithConfig(userid: String? = null, username: String? = null, openid: String? = null): List<PusherUser>

    fun getAllAdmin(): List<PusherUser>

    fun getAll(): MutableIterable<PusherUser>

    fun getAllWithConfig(): MutableIterable<PusherUser>

    fun addOrUpdate(user: PusherUser): PusherUser

    fun addOrUpdateWithoutPushConfig(user: PusherUser): PusherUser

    fun addOrUpdatePushConfig(userPushConfig: PusherUserPushConfig): PusherUserPushConfig

    fun disableByOpenid(openids: List<String>): Int

    fun getUserWithConfigByOpenid(openid: String): PusherUser?

    fun getConfig(openid: String): Map<String, Any>?

}