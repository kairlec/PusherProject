package com.kairlec.pusher.dao

import com.kairlec.pusher.openapi.dto.PusherUserConfigDTO
import com.kairlec.pusher.openapi.pojo.PusherUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Service


@Service
interface UserConfigRepository : JpaRepository<PusherUser, String> {
    fun findByOpenUserIdAndDisabled(openUserId: String, disabled: Boolean): List<PusherUserConfigDTO>
}