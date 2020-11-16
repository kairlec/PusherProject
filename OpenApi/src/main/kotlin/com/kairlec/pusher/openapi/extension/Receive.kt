package com.kairlec.pusher.openapi.extension

import com.kairlec.pusher.openapi.API
import com.kairlec.pusher.openapi.pojo.PusherUser
import com.kairlec.pusher.receiver.dsl.replyText
import com.kairlec.pusher.receiver.msg.ReceiveMsg
import org.slf4j.LoggerFactory

internal class ReceiveHelper

internal val logger = LoggerFactory.getLogger(ReceiveHelper::class.java)

internal const val SWITCH_FLAG = "__PRIVATE_CONFIG_SWITCH_TO_"
internal const val ADMIN_MODE_FLAG = "__PRIVATE_CONFIG_ADMIN_MODE_"

/**
 * 断言是否为管理员
 */
fun ReceiveMsg.assertAdmin(user: PusherUser): PusherUser? {
    return if (!user.admin) {
        replyText("You are not administrator!")
        closeReply()
        null
    } else {
        user
    }
}

/**
 * 启用管理员模式
 */
fun ReceiveMsg.enableAdminMode(api: API, user: PusherUser): PusherUser? {
    return assertAdmin(user)?.let {
        it[ADMIN_MODE_FLAG] = true
        api.saveWithoutPushConfig(it)
        replyText("turn on admin mode")
        closeReply()
        null
    } ?: user
}

/**
 * 关闭管理员模式
 */
fun ReceiveMsg.disableAdminMode(api: API, user: PusherUser): PusherUser? {
    return assertAdmin(user)?.let {
        it[ADMIN_MODE_FLAG] = false
        api.saveWithoutPushConfig(user)
        replyText("turn off admin mode")
        closeReply()
        null
    } ?: user
}

/**
 * 是否启用管理员模式
 */
val PusherUser.adminMode: Boolean
    get() {
        if (!admin) {
            return false
        }
        return get<Boolean>(ADMIN_MODE_FLAG) == true
    }

/**
 * 断言用户是否被禁用
 */
internal fun ReceiveMsg.assertDisabled(user: PusherUser): PusherUser? {
    return if (user.disabled) {
        logger.info("${user.openUserId} has been disabled")
        replyText("You has been disabled!")
        closeReply()
        null
    } else {
        user
    }
}

/**
 * 断言是否找到
 */
internal fun ReceiveMsg.assertFound(user: PusherUser?, by: String): PusherUser? {
    if (user == null) {
        logger.info("$by invalid")
        replyText("$by invalid")
        closeReply()
    }
    return user
}

/**
 * 断言找到且未禁用
 */
internal fun ReceiveMsg.assertFoundAndNotDisabled(user: PusherUser?, by: String): PusherUser? {
    return assertFound(user, by)?.let { assertDisabled(it) }
}

/**
 * 断言多个是否找到
 */
internal fun ReceiveMsg.assertFound(users: List<PusherUser>, by: String): PusherUser? {
    return if (users.isEmpty()) {
        logger.info("$by invalid")
        replyText("$by invalid")
        closeReply()
        null
    } else {
        users[0]
    }
}

/**
 * 断言是否匹配了多给用户
 */
internal fun ReceiveMsg.assertMultiUserMatched(users: List<PusherUser>, message: String): Boolean {
    return if (users.size > 1) {
        logger.info("Multi user has matched. $message")
        replyText("Multi user has matched. $message")
        closeReply()
        false
    } else {
        true
    }
}

/**
 * 断言是否匹配Token
 */
internal fun ReceiveMsg.assertToken(user: PusherUser, token: String): PusherUser? {
    return if (!user.matchToken(token)) {
        logger.info("Token invalid")
        replyText("Token invalid")
        closeReply()
        null
    } else {
        user
    }
}

/**
 * 断言是否找到唯一
 */
internal fun ReceiveMsg.assertFoundOne(users: List<PusherUser>, ifMultiMessage: String, by: String): PusherUser? {
    if (!assertMultiUserMatched(users, ifMultiMessage)) {
        return null
    }
    return assertFound(users, by)
}

/**
 * 断言是否找到唯一且未被禁用
 */
internal fun ReceiveMsg.assertFoundOneAndNotDisabled(users: List<PusherUser>, ifMultiMessage: String, by: String): PusherUser? {
    return assertFoundOne(users, ifMultiMessage, by)?.let { assertDisabled(it) }
}

/**
 * 获取带有配置的用户
 * 若传入的openid为null或空,则根据当前消息的fromUserName为UserId进行查找
 */
fun ReceiveMsg.getUserWithConfig(api: API, ifMultiMessage: String, openid: String?): PusherUser? {
    return if (!openid.isNullOrEmpty()) {
        assertFoundAndNotDisabled(api.getUserWithConfig(openid), "Openid")
    } else {
        assertFoundOneAndNotDisabled(api.getUserWithConfigByUserid(fromUserName), ifMultiMessage, "Userid")
    }
}

/**
 * 根据UserID,获取匹配Token的带有Config的用户
 */
fun ReceiveMsg.getUserWithConfigMatchTokenByUserid(api: API, ifMultiMessage: String, token: String): PusherUser? {
    if (token.isEmpty()) {
        return null
    }
    val users = api.getUserWithConfigByUserid(fromUserName)
    return assertFoundOneAndNotDisabled(users, ifMultiMessage, "Userid")?.let { assertToken(it, token) }
}

/**
 * 根据openId获取带有配置的用户
 */
fun ReceiveMsg.getUserWithConfig(api: API, openid: String): PusherUser? {
    if (openid.isEmpty()) {
        return null
    }
    return assertFoundAndNotDisabled(api.getUserWithConfig(openid), "Openid")
}

/**
 * 获取匹配指定Token的带配置的User
 */
fun ReceiveMsg.getUserWithConfigMatchToken(api: API, openid: String, token: String): PusherUser? {
    if (openid.isEmpty() || token.isEmpty()) {
        return null
    }
    return api.getUserWithConfig(openid)?.let { assertToken(it, token) }
}

/**
 * 获取切换后的用户或未切换的当前用户
 */
fun ReceiveMsg.getSwitcherIfSwitched(api: API, user: PusherUser?): PusherUser? {
    return user?.get<String>(SWITCH_FLAG)?.let {
        assertFound(api.getUserWithConfig(it), "Openid")
    } ?: user
}

/**
 * 切换至指定用户上
 */
fun ReceiveMsg.switchTo(api: API, targetOpenUserId: String): PusherUser? {
    val currentUser = assertFoundOneAndNotDisabled(api.getUserWithConfigByUserid(fromUserName), "Not support yet", "Userid")?.let { assertAdmin(it) }
            ?: return null
    val targetUser = assertFound(getUserWithConfig(api, targetOpenUserId), "Openid") ?: return null
    currentUser[SWITCH_FLAG] = targetUser.openUserId
    api.saveWithoutPushConfig(currentUser)
    return targetUser
}

/**
 * 关闭切换
 */
fun ReceiveMsg.switchOff(api: API): PusherUser? {
    val currentUser = assertFoundOneAndNotDisabled(api.getUserWithConfigByUserid(fromUserName), "Not support yet", "Userid")
            ?: return null
    currentUser.config?.remove(SWITCH_FLAG)
    api.saveWithoutPushConfig(currentUser)
    return currentUser
}

/**
 * 获取当前用户(带配置),若用户为管理员,则获取当前是否有切换
 */
fun ReceiveMsg.getCurrentUser(api: API): PusherUser? {
    return getSwitcherIfSwitched(api, assertFoundOneAndNotDisabled(api.getUserWithConfigByUserid(fromUserName), "Not support yet", "Userid"))
}

/**
 * 获取当前用户(带配置),若用户为管理员,则获取当前是否有切换
 */
fun ReceiveMsg.getCurrentUserMatchToken(api: API, token: String): PusherUser? {
    return getSwitcherIfSwitched(api, getCurrentUser(api)?.let { assertToken(it, token) })
}

/**
 * 获取当前用户发消息人本人(带配置)
 */
fun ReceiveMsg.getRealCurrentUser(api: API): PusherUser? {
    return assertFoundOneAndNotDisabled(api.getUserWithConfigByUserid(fromUserName), "Not support yet", "Userid")
}

/**
 * 获取匹配Token的当前用户发消息人本人(带配置)
 */
fun ReceiveMsg.getRealCurrentUserMatchToken(api: API, token: String): PusherUser? {
    return getCurrentUser(api)?.let { assertToken(it, token) }
}