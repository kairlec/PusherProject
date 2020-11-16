package com.kairlec.pusher.dao

import com.kairlec.pusher.openapi.pojo.PusherUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Service


@Service
interface UserRepository : JpaRepository<PusherUser, String> {
    fun removeByOpenUserIdNotInAndDisabled(openUserIds: List<String>, disabled: Boolean): Long

    fun removeByDisabled(disabled: Boolean): Long

    fun findByOpenUserIdAndUseridAndUsernameAndDisabled(openUserId: String, userid: String, username: String, disabled: Boolean): List<PusherUser>

    fun findByOpenUserIdAndUsernameAndDisabled(openUserId: String, username: String, disabled: Boolean): List<PusherUser>

    fun findByOpenUserIdAndUseridAndDisabled(openUserId: String, userid: String, disabled: Boolean): List<PusherUser>

    fun findByAdminAndDisabled(admin: Boolean, disabled: Boolean): List<PusherUser>

    fun findByAdminAndUsernameAndDisabled(admin: Boolean, username: String, disabled: Boolean): List<PusherUser>

    fun findByUsernameAndDisabled(username: String, disabled: Boolean): List<PusherUser>

    fun findByUseridAndDisabled(userid: String, disabled: Boolean): List<PusherUser>

    fun findByOpenUserIdInAndDisabled(openUserIds: List<String>, disabled: Boolean): List<PusherUser>

    fun findByUseridAndUsernameAndDisabled(userid: String, username: String, disabled: Boolean): List<PusherUser>

    fun findByDisabled(disabled: Boolean): List<PusherUser>

    fun findByOpenUserId(openUserId: String): List<PusherUser>

}
