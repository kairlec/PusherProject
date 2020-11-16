package com.kairlec.pusher.openapi.dto

import com.kairlec.pusher.openapi.pojo.PusherUserPushConfig


interface PusherUserDTO {
    fun getUserid(): String

    fun getToken(): String

    fun getAdmin(): Boolean

    fun getDisabled(): Boolean

    fun getUsername(): String

    fun getOpenUserId(): String

    fun getSalt(): String

    fun getPushConfig(): PusherUserPushConfig
}


interface PusherUserConfigDTO {
    fun getConfig(): String
}