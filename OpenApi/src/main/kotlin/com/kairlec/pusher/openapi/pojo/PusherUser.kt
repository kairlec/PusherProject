package com.kairlec.pusher.openapi.pojo

import com.fasterxml.jackson.module.kotlin.convertValue
import com.kairlec.pusher.openapi.dto.PusherUserDTO
import com.kairlec.pusher.utils.PBKDF2Util
import java.util.*
import javax.persistence.*

/**
 * 推送用户
 */
@Entity
class PusherUser {
    companion object {
        fun fromDTO(userDTO: PusherUserDTO): PusherUser {
            val pusherUser = PusherUser()
            pusherUser.userid = userDTO.getUserid()
            pusherUser.token = userDTO.getToken()
            pusherUser.admin = userDTO.getAdmin()
            pusherUser.disabled = userDTO.getDisabled()
            pusherUser.username = userDTO.getUsername()
            pusherUser.openUserId = userDTO.getOpenUserId()
            pusherUser.salt = userDTO.getSalt()
            pusherUser.pushConfig = userDTO.getPushConfig()
            pusherUser.config = null
            return pusherUser
        }

        fun convertConfigToString(config: HashMap<String, Any>): String {
            return objectMapper.writeValueAsString(config)
        }

        fun convertStringToConfig(s: String): HashMap<String, Any> {
            return objectMapper.convertValue(objectMapper.readTree(s))
        }

    }

    /**
     * 用户ID
     */
    var userid: String = ""

    /**
     * 用户Token
     *
     * 应当是已经被[PBKDF2Util]加密后的密文
     */
    @Column(length = 128)
    var token: String = ""

    /**
     * 是否为管理员
     */
    var admin: Boolean = false

    /**
     * 是否已禁用
     */
    var disabled: Boolean = false

    /**
     * 用户名
     */
    var username: String = ""

    /**
     * 全局唯一ID
     */
    @Id
    @Column(length = 64)
    var openUserId: String = ""

    /**
     * Token的加密盐
     */
    @Column(length = 32)
    var salt: String = PBKDF2Util.generateSalt()

    /**
     * 配置类(可选获取)
     */
    @Suppress("JpaAttributeTypeInspection")
    @Convert(converter = CustomHashMapConverter::class)
    @Column(length = 65535, columnDefinition = "TEXT")
    var config: HashMap<String, Any>? = HashMap()
        private set

    /**
     * 推送配置
     */
    @OneToOne
    var pushConfig: PusherUserPushConfig = PusherUserPushConfig(this)
        private set

    /**
     * 验证密码
     */
    @Transient
    fun matchToken(token: String): Boolean {
        return PBKDF2Util.authenticate(token, this.token, salt)
    }

    /**
     * 更新密码
     */
    @Transient
    fun updateTokenEncrypted(token: String) {
        this.token = PBKDF2Util.getEncryptedPassword(token, salt)
    }

    operator fun <T : Any> set(key: String, value: T): Any? = config?.put(key, value)

    inline operator fun <reified T : Any> get(key: String): T? = config?.get(key)?.let { objectMapper.convertValue<T>(it) }

    override fun toString(): String {
        return "PusherUser(userid=$userid, token=$token, admin=$admin, username=$username, openUserId=$openUserId, salt=$salt, config=$config)"
    }
}

