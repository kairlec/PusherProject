package com.kairlec.pusher.openapi

import com.kairlec.pusher.openapi.pojo.PusherUser
import com.kairlec.pusher.openapi.pojo.PusherUserPushConfig
import com.kairlec.pusher.receiver.reply.ReplyService

/**
 * 插件对接API,此API接口实例将会自动以参数传入[Event.onCreated],无需自己实例化
 */
interface API {
    /**
     * 获取所有管理员用户
     */
    fun getAdminUser(): List<PusherUser>

    /**
     * 获取用户
     * @param openUserId [String] 用户openid
     */
    fun getUser(openUserId: String): PusherUser?

    /**
     * 获取有配置信息的用户
     * @param openUserId [String] 用户openid
     */
    fun getUserWithConfig(openUserId: String): PusherUser?

    /**
     * 以用户id获取用户
     * @param userid [String] 用户id
     */
    fun getUserByUserid(userid: String): List<PusherUser>

    /**
     * 以用户id获取有配置信息的用户
     * @param userid [String] 用户id
     */
    fun getUserWithConfigByUserid(userid: String): List<PusherUser>

    /**
     * 保存用户
     * @param user [PusherUser] 要保存的用户
     */
    fun save(user: PusherUser): PusherUser

    /**
     * 保存用户(不更新PushConfig)
     * @param user [PusherUser] 要保存的用户
     */
    fun saveWithoutPushConfig(user: PusherUser): PusherUser

    /**
     * 保存用户推送配置
     * @param userPushConfig [PusherUserPushConfig] 要保存的配置
     */
    fun savePushConfig(userPushConfig: PusherUserPushConfig): PusherUserPushConfig

    /**
     * 获取回复服务
     */
    fun getReplyService(): ReplyService

    /**
     * 注册Controller
     * @param beanName 注册的Bean名称
     * @param controllerClass 指定的Controller类,该类应当有诸如[org.springframework.web.bind.annotation.RestController],[org.springframework.web.bind.annotation.ControllerAdvice],[org.springframework.stereotype.Controller]等注解
     */
    fun registerController(beanName: String, controllerClass: Class<*>)

}