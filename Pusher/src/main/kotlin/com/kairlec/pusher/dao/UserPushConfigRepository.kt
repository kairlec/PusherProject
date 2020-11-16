package com.kairlec.pusher.dao

import com.kairlec.pusher.openapi.pojo.PusherUserPushConfig
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Service

@Service
interface UserPushConfigRepository : JpaRepository<PusherUserPushConfig, String> {
    fun findByOpenUserId(openUserId: String): List<PusherUserPushConfig>
}