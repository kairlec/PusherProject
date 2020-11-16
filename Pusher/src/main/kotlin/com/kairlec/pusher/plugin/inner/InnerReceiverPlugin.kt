package com.kairlec.pusher.plugin.inner

import com.kairlec.pusher.openapi.API
import com.kairlec.pusher.openapi.Event
import com.kairlec.pusher.openapi.Plugin
import com.kairlec.pusher.openapi.context.MessageContext
import com.kairlec.pusher.openapi.extension.*
import com.kairlec.pusher.openapi.pojo.PusherUser
import com.kairlec.pusher.openapi.pojo.SubscriberLevel
import com.kairlec.pusher.receiver.ReceiveInterface
import com.kairlec.pusher.receiver.ReplyMsg
import com.kairlec.pusher.receiver.dsl.ReceiveDSL
import com.kairlec.pusher.receiver.dsl.replyText
import com.kairlec.pusher.receiver.dsl.subscribeTextMessages
import com.kairlec.pusher.receiver.msg.ReceiveMsg
import org.slf4j.LoggerFactory


internal class InnerReceiver(private val api: API) : ReceiveInterface {
    override val enableDSL: Boolean = true
    override var receiveDSL: ReceiveDSL? = ReceiveDSL()
    override fun onReceiveDSL() {
        receiveDSL!!.listen(api)
    }
}

internal val logger = LoggerFactory.getLogger(InnerReceiver::class.java)

internal fun PusherUser.updateToken(api: API, old: String, new: String, msg: ReceiveMsg) {
    if (old == new) {
        msg.replyText("The new token cannot be the same as the old token")
        msg.closeReply()
    } else {
        updateTokenEncrypted(new)
        api.saveWithoutPushConfig(this)
        msg.replyText("Update token success")
        msg.closeReply()
    }
}

internal fun ReceiveDSL.listen(api: API) {
    subscribeTextMessages("Inner Admin Mode Listener") {
        matching("^(?i)/admin\\s+([^\\s]+)(?:\\s+)?([^\\s]+)?$".toRegex()) {
            if (it.groupValues[2].isEmpty()) {
                getUserWithConfigMatchTokenByUserid(api, "Please use command:/admin {openUserid} {token}", it.groupValues[1])
            } else {
                getUserWithConfigMatchToken(api, it.groupValues[1], it.groupValues[2])
            }?.apply { enableAdminMode(api, this) }
        }

        matching("^(?i)/exit\\s+admin(?:\\s+([^\\s]+))?$".toRegex()) {
            val user = if (it.groupValues[1].isEmpty()) {
                getUserWithConfig(api, "Please use command:/exit admin {openUserid}", null)
            } else {
                getUserWithConfig(api, it.groupValues[1])
            } ?: return@matching
            disableAdminMode(api, user)
        }

        matching("^(?i)/switch\\s+(off|to)(?:\\s+([^\\s]+))?$".toRegex()) {
            if (it.groupValues[1] == "off") {
                switchOff(api)?.let { user ->
                    replyText("已切回至[${user.userid}](${user.username})")
                    closeReply()
                }
            } else {
                val openUserId = it.groupValues[2]
                if (openUserId.isEmpty()) {
                    return@matching
                }
                switchTo(api, openUserId)?.let { user ->
                    replyText("已切换至[${user.userid}](${user.username})")
                    closeReply()
                }
            }
        }

        matching("^(?i)/update\\s+token\\s+(old|new)=([^\\s]+)\\s+(?!\\1)(?:old|new)=([^\\s]+)$".toRegex()) {
            val result = it.groupValues
            val (old, new) = if (result[1].toLowerCase() == "old") {
                result[2] to result[3]
            } else {
                result[3] to result[2]
            }
            getCurrentUserMatchToken(api, old)?.updateToken(api, old, new, this)?:return@matching
            replyText("Success update token")
            closeReply()
        }

        matching("^(?i)/status$".toRegex()) {
            val user = getCurrentUser(api) ?: run {
                logger.warn("get current user failed")
                return@matching
            }
            replyText("""
                    OpenUserId:${user.openUserId}
                    Userid:${user.userid}
                    Admin:${user.admin}
                    Username:${user.username}
                    Config:
                    ${user.config}
                    """.trimIndent())
            closeReply()
        }

        matching("^(?i)/rmconf\\s+([^\\s]+)".toRegex()) {
            val user = getCurrentUser(api) ?: return@matching
            val key = it.groupValues[1]
            user.config!!.remove(key)
            api.saveWithoutPushConfig(user)
            replyText("Success remove config key[$key]")
            closeReply()
        }

        matching("^(?i)/filter level(?:\\s([^\\s]+))?$".toRegex()) {
            val config = getCurrentUser(api)?.pushConfig ?: return@matching
            if (it.groupValues[1].isEmpty()) {
                replyText(config.subscriberLevel.toString())
                closeReply()
            } else {
                SubscriberLevel.parse(it.groupValues[1])
                        ?.let { level ->
                            config.subscriberLevel = level
                            api.savePushConfig(config)
                            replyText("Success set subscriber level to $level")
                            closeReply()
                        }
                        ?: run {
                            replyText("Cannot solved subscriber level for string [${it.groupValues[1]}], just only accept level string or int value.")
                            closeReply()
                        }
            }
        }

        matching("^(?i)/filter\\s+keyword\\s+(allow|block)\\s+get$".toRegex()) {
            val config = getCurrentUser(api)?.pushConfig ?: return@matching
            val allowOrBlock = it.groupValues[1]
            val set = if (allowOrBlock.toLowerCase() == "allow") {
                config.allowFilter
            } else {
                config.blockFilter
            }
            if (set.isEmpty()) {
                replyText("{None}")
            } else {
                replyText(set.joinToString("\r\n"))
            }
            closeReply()
        }

        matching("^(?i)/filter\\s+keyword\\s+(allow|block)\\s+(add|del)\\s+([^\\s]+)$".toRegex()) {
            val config = getCurrentUser(api)?.pushConfig ?: return@matching
            val allowOrBlock = it.groupValues[1]
            val addOrDel = it.groupValues[2]
            val key = it.groupValues[3]
            val set = if (allowOrBlock.toLowerCase() == "allow") {
                config.allowFilter
            } else {
                config.blockFilter
            }
            when (addOrDel.toLowerCase()) {
                "add" -> {
                    set.add(key.toLowerCase())
                    api.savePushConfig(config)
                    replyText("Success add key[$key] into ${allowOrBlock}Filter")
                    closeReply()
                }
                "del" -> {
                    set.remove(key.toLowerCase())
                    api.savePushConfig(config)
                    replyText("Success remove key[$key] from ${allowOrBlock}Filter")
                    closeReply()
                }
            }
        }
    }
}


@Plugin
class InnerReceiverPlugin : Event {
    private lateinit var api: API

    private lateinit var receiver: ReceiveInterface

    override fun onCreated(api: API) {
        this.api = api
        receiver = InnerReceiver(api)
        if (receiver.enableDSL) {
            receiver.onReceiveDSL()
        }
    }

    override fun onReceiveMessage(messageContext: MessageContext): ReplyMsg? {
        return if (receiver.enableDSL) {
            receiver.receiveDSL!!.send(messageContext.message)
            null
        } else {
            receiver.onReceive(messageContext.message)
        }
    }

}