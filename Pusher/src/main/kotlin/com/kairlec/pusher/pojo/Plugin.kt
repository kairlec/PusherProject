package com.kairlec.pusher.pojo

import com.kairlec.pusher.openapi.API
import com.kairlec.pusher.openapi.Event
import com.kairlec.pusher.openapi.context.MessageContext
import com.kairlec.pusher.openapi.context.SendStatusContext
import com.kairlec.pusher.receiver.ReplyMsg
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import java.util.*

data class Plugin(
        val pluginName: String,
        val event: Event,
) {
    object Invoker : Event {
        private val logger = LoggerFactory.getLogger(Invoker::class.java)

        internal val plugins = ArrayList<Plugin>()

        private inline fun runAll(async: Boolean = true, crossinline invoke: Plugin.() -> Unit) {
            runBlocking {
                val job = launch {
                    plugins.forEach {
                        try {
                            it.invoke()
                        } catch (e: Throwable) {
                            logger.error("[Plugin-${it.pluginName}] error:${e.message}", e)
                        }
                    }
                }
                if (async) {
                    job.join()
                }
            }
        }

        @Deprecated("The invoke helper should not use the onCreate method", level = DeprecationLevel.ERROR)
        override fun onCreated(api: API) {
        }

        override fun onStartup(context: ApplicationContext) {
            runAll {
                event.onStartup(context)
            }
        }

        override fun onSendStatus(sendStatusContext: SendStatusContext) {
            runAll {
                event.onSendStatus(sendStatusContext)
            }
        }

        override fun onReceiveMessage(messageContext: MessageContext): ReplyMsg? {
            var replyMsg: ReplyMsg? = null
            runAll(true) {
                replyMsg = replyMsg ?: event.onReceiveMessage(messageContext)
            }
            return replyMsg
        }

    }
}