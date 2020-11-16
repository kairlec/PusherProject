package com.kairlec.pusher.config

import com.kairlec.pusher.annotation.condition.StatusReportCondition
import com.kairlec.pusher.config.properties.StatusReportProperties
import com.kairlec.pusher.core.PusherException
import com.kairlec.pusher.core.wework.WeWorkSenderHelper
import com.kairlec.pusher.openapi.context.SendStatusContext
import com.kairlec.pusher.pojo.Plugin
import com.kairlec.pusher.service.impl.UserServiceImpl
import com.kairlec.pusher.util.StatusCountUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Conditional
import org.springframework.scheduling.Trigger
import org.springframework.scheduling.annotation.SchedulingConfigurer
import org.springframework.scheduling.config.ScheduledTaskRegistrar
import org.springframework.scheduling.support.CronTrigger
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.annotation.PostConstruct


@Component
@Conditional(StatusReportCondition::class)
class StatusReportAutoConfiguration : SchedulingConfigurer {
    @Autowired
    private lateinit var statusReportProperties: StatusReportProperties

    @Autowired
    private lateinit var workSenderHelper: WeWorkSenderHelper

    @Autowired
    private lateinit var statusCountUtil: StatusCountUtil

    @Autowired
    private lateinit var userService: UserServiceImpl

    @Value("\${version}")
    private lateinit var version: String

    @PostConstruct
    fun init() {
        val admins = userService.getAllAdmin()
        for (user in admins) {
            try {
                workSenderHelper.sendText("Pusher服务(v${version})已在时间:${LocalDateTime.now().format(dateTimeFormatter)}成功启动", workSenderHelper.withSettings.toUser(user.userid))
            } catch (e: PusherException) {
                logger.error("${e.code} => ${e.message}")
            }
        }
        val task = Runnable {
            Plugin.Invoker.onSendStatus(SendStatusContext(
                    statusCountUtil.beginTime,
                    statusCountUtil.successCount,
                    statusCountUtil.errorCount,
                    statusCountUtil.todaySuccessCount,
                    statusCountUtil.todayErrorCount,
                    admins
            ))
        }
        this.task = task
    }

    lateinit var task: Runnable

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
