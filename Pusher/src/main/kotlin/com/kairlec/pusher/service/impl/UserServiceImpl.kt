package com.kairlec.pusher.service.impl

import com.kairlec.pusher.dao.UserConfigRepository
import com.kairlec.pusher.dao.UserDTORepository
import com.kairlec.pusher.dao.UserPushConfigRepository
import com.kairlec.pusher.dao.UserRepository
import com.kairlec.pusher.openapi.pojo.PusherUser
import com.kairlec.pusher.openapi.pojo.PusherUserPushConfig
import com.kairlec.pusher.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserServiceImpl : UserService {
    @Autowired
    private lateinit var userDTORepository: UserDTORepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var userPushConfigRepository: UserPushConfigRepository

    @Autowired
    private lateinit var userConfigRepository: UserConfigRepository

    override fun match(userid: String?, username: String?, openid: String?): List<PusherUser> {
        return when {
            userid != null && username != null && openid != null -> {
                userDTORepository.findByOpenUserIdAndUseridAndUsernameAndDisabled(openid, userid, username, false).map(PusherUser.Companion::fromDTO)
            }
            userid != null && username != null -> {
                userDTORepository.findByUseridAndUsernameAndDisabled(userid, username, false).map(PusherUser.Companion::fromDTO)
            }
            userid != null && openid != null -> {
                userDTORepository.findByOpenUserIdAndUseridAndDisabled(openid, userid, false).map(PusherUser.Companion::fromDTO)
            }
            username != null && openid != null -> {
                userDTORepository.findByOpenUserIdAndUsernameAndDisabled(openid, username, false).map(PusherUser.Companion::fromDTO)
            }
            username != null -> {
                userDTORepository.findByUsernameAndDisabled(username, false).map(PusherUser.Companion::fromDTO)
            }
            userid != null -> {
                userDTORepository.findByUseridAndDisabled(userid, false).map(PusherUser.Companion::fromDTO)
            }
            openid != null -> {
                userDTORepository.findByOpenUserId(openid).map(PusherUser.Companion::fromDTO)
            }
            else -> {
                emptyList()
            }
        }
    }

    override fun matchWithConfig(userid: String?, username: String?, openid: String?): List<PusherUser> {
        return when {
            userid != null && username != null && openid != null -> {
                userRepository.findByOpenUserIdAndUseridAndUsernameAndDisabled(openid, userid, username, false)
            }
            userid != null && username != null -> {
                userRepository.findByUseridAndUsernameAndDisabled(userid, username, false)
            }
            userid != null && openid != null -> {
                userRepository.findByOpenUserIdAndUseridAndDisabled(openid, userid, false)
            }
            username != null && openid != null -> {
                userRepository.findByOpenUserIdAndUsernameAndDisabled(openid, username, false)
            }
            username != null -> {
                userRepository.findByUsernameAndDisabled(username, false)
            }
            userid != null -> {
                userRepository.findByUseridAndDisabled(userid, false)
            }
            openid != null -> {
                userRepository.findByOpenUserId(openid)
            }
            else -> {
                emptyList()
            }
        }
    }

    override fun addOrUpdate(user: PusherUser): PusherUser {
        userPushConfigRepository.save(user.pushConfig)
        return userDTORepository.save(user)
    }

    override fun addOrUpdatePushConfig(userPushConfig: PusherUserPushConfig): PusherUserPushConfig {
        return userPushConfigRepository.save(userPushConfig)
    }

    override fun addOrUpdateWithoutPushConfig(user: PusherUser): PusherUser {
        return userDTORepository.save(user)
    }

    override fun getAll(): MutableIterable<PusherUser> {
        return userDTORepository.findAll()
    }

    override fun getAllWithConfig(): MutableIterable<PusherUser> {
        return userRepository.findAll()
    }

    override fun getAllAdmin(): List<PusherUser> {
        return userDTORepository.findByAdminAndDisabled(true, disabled = false).map(PusherUser.Companion::fromDTO)
    }

    override fun disableByOpenid(openids: List<String>): Int {
        openids.forEach {
            val user = userDTORepository.getOne(it)
            user.disabled = true
            userDTORepository.save(user)
        }
        return openids.size
    }

    override fun getUserWithConfigByOpenid(openid: String): PusherUser? {
        return userRepository.findById(openid).let {
            if (it.isPresent) {
                it.get()
            } else {
                null
            }
        }
    }

    override fun getConfig(openid: String): Map<String, Any>? {
        val lt = userConfigRepository.findByOpenUserIdAndDisabled(openid, false)
        return if (lt.isNotEmpty()) {
            PusherUser.convertStringToConfig(lt[0].getConfig())
        } else {
            null
        }
    }

}