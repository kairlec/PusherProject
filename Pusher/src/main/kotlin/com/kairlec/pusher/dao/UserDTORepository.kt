package com.kairlec.pusher.dao

import com.kairlec.pusher.openapi.dto.PusherUserDTO
import com.kairlec.pusher.openapi.pojo.PusherUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Service

@Service
interface UserDTORepository : JpaRepository<PusherUser, String> {
    fun findByOpenUserIdAndUseridAndUsernameAndDisabled(openUserId: String, userid: String, username: String, disabled: Boolean): List<PusherUserDTO>

    fun findByOpenUserIdAndUsernameAndDisabled(openUserId: String, username: String, disabled: Boolean): List<PusherUserDTO>

    fun findByOpenUserIdAndUseridAndDisabled(openUserId: String, userid: String, disabled: Boolean): List<PusherUserDTO>

    fun findByAdminAndDisabled(admin: Boolean, disabled: Boolean): List<PusherUserDTO>

    fun findByAdminAndUsernameAndDisabled(admin: Boolean, username: String, disabled: Boolean): List<PusherUserDTO>

    fun findByUsernameAndDisabled(username: String, disabled: Boolean): List<PusherUserDTO>

    fun findByUseridAndDisabled(userid: String, disabled: Boolean): List<PusherUserDTO>

    fun findByOpenUserIdInAndDisabled(openUserIds: List<String>, disabled: Boolean): List<PusherUserDTO>

    fun findByUseridAndUsernameAndDisabled(userid: String, username: String, disabled: Boolean): List<PusherUserDTO>

    fun findByDisabled(disabled: Boolean): List<PusherUserDTO>

    fun findByOpenUserId(openUserId: String): List<PusherUserDTO>

}
