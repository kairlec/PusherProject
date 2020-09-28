package com.kairlec.pusher.config

import com.kairlec.pojo.PusherUser
import com.kairlec.pusher.config.properties.StatusReportProperties
import com.kairlec.pusher.util.StatusCountUtil
import com.kairlec.pusher.core.PusherException
import com.kairlec.pusher.core.wework.WeWorkSenderHelper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.Trigger
import org.springframework.scheduling.annotation.SchedulingConfigurer
import org.springframework.scheduling.config.ScheduledTaskRegistrar
import org.springframework.scheduling.support.CronTrigger
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.annotation.PostConstruct


@Component
@ConditionalOnProperty(prefix = "wework.push", value = ["enabled", "status.enabled"], matchIfMissing = true)
class StatusReportAutoConfiguration : SchedulingConfigurer {
    @Autowired
    private lateinit var statusReportProperties: StatusReportProperties

    @Autowired
    private lateinit var workSenderHelper: WeWorkSenderHelper

    @Autowired
    private lateinit var statusCountUtil: StatusCountUtil

    @Autowired
    private lateinit var pusherUsers: Map<String, *>

    @Value("\${version}")
    private lateinit var version: String

    @PostConstruct
    fun init() {
        for (pusherUserEntry in pusherUsers) {
            val user = pusherUserEntry.value as PusherUser
            if (user.admin) {
                try {
                    workSenderHelper.sendText("Pusher服务(v${version})已在时间:${LocalDateTime.now().format(dateTimeFormatter)}成功启动", workSenderHelper.withSettings.toUser(pusherUserEntry.key))
                } catch (e: PusherException) {
                    logger.error("${e.code} => ${e.message}")
                }
            }
        }
        val task = Runnable {
            for (pusherUserEntry in pusherUsers) {
                val user = pusherUserEntry.value as PusherUser
                if (user.admin) {
                    try {
                        workSenderHelper.sendText(buildStatusText()
                                , workSenderHelper.withSettings.toUser(pusherUserEntry.key))
                    } catch (e: PusherException) {
                        logger.error("${e.code} => ${e.message}")
                    }
                }
            }
        }
        this.task = task
    }

    lateinit var task: Runnable

    fun buildStatusText(): String {
        return """
               Pusher服务启动时间:${statusCountUtil.beginTime.format(dateTimeFormatter)}
               已运行:${String.format("%.2f", Duration.between(statusCountUtil.beginTime, LocalDateTime.now()).toMinutes().toDouble() / 60)}小时
               总请求次数:${statusCountUtil.successCount + statusCountUtil.errorCount}
               总成功次数:${statusCountUtil.successCount}
               今日请求次数:${statusCountUtil.todaySuccessCount + statusCountUtil.todayErrorCount}
               今日成功次数:${statusCountUtil.todaySuccessCount}
               """.trimIndent()
    }

    override fun configureTasks(taskRegistrar: ScheduledTaskRegistrar) {
        val trigger = Trigger { triggerContext ->
            val trigger = CronTrigger(statusReportProperties.cron)
            trigger.nextExecutionTime(triggerContext)
        }
        taskRegistrar.addTriggerTask(task, trigger)
    }

    companion object {
        private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss")
        private val logger = LoggerFactory.getLogger(StatusReportAutoConfiguration::class.java)
    }
}
