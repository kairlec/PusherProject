package com.kairlec.pusher.config

import com.kairlec.pusher.config.properties.UserProperties
import com.kairlec.pusher.core.wework.WeWorkAddressBookHelper
import com.kairlec.pusher.openapi.pojo.PusherUser
import com.kairlec.pusher.service.impl.UserServiceImpl
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import javax.annotation.PostConstruct

@Configuration
class UserAutoConfiguration {

    @Autowired
    private lateinit var userService: UserServiceImpl

    @Autowired
    private lateinit var userProperties: UserProperties

    @Autowired
    private lateinit var workAddressBookHelper: WeWorkAddressBookHelper

    private val logger = LoggerFactory.getLogger(UserAutoConfiguration::class.java)

    @PostConstruct
    fun init() {
        val all = userService.getAllWithConfig().map { it.openUserId }
        val departments = workAddressBookHelper.getDepartmentList().filter { it.name in userProperties.department }
        val invalidOpenUserIds: ArrayList<String> = ArrayList(all)
        departments.forEach { department ->
            val users = workAddressBookHelper.getUserSimpleList(department.id, true)
            val newUsers = users.filter { it.openUserID !in all }
            invalidOpenUserIds.removeAll(users.map { it.openUserID })
            newUsers.forEach {
                val user = PusherUser()
                user.userid = it.userID
                user.username = it.name
                user.admin = false
                user.openUserId = it.openUserID
                user.updateTokenEncrypted(it.userID)
                userService.addOrUpdate(user)
            }
        }
        if (userProperties.autoDisable) {
            val count = userService.disableByOpenid(invalidOpenUserIds)
            logger.info("Disable unused users for $count")
        }
        userProperties.admin.forEach {
            val users = when {
                it.username.isNotEmpty() && it.userid.isNotEmpty() -> {
                    userService.matchWithConfig(userid = it.userid, username = it.username)
                }
                it.username.isNotEmpty() -> {
                    userService.matchWithConfig(username = it.username)
                }
                it.userid.isNotEmpty() -> {
                    userService.matchWithConfig(userid = it.userid)
                }
                else -> {
                    emptyList()
                }
            }
            if (users.size == 1) {
                val user = users[0]
                user.admin = true
                user.updateTokenEncrypted(it.token)
                userService.addOrUpdate(user)
            }else{
                logger.error("There are multi user for (userid=${it.userid}, username=${it.username})")
            }
        }
    }
}