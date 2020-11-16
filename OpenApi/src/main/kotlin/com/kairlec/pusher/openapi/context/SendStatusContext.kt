package com.kairlec.pusher.openapi.context

import com.kairlec.pusher.openapi.pojo.PusherUser
import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class SendStatusContext(
        /**
         * 系统运行时间
         */
        val systemStartTime: LocalDateTime,

        /**
         * 所有成功推送次数
         */
        val allSuccessCount: Long,

        /**
         * 所有失败推送次数
         */
        val allErrorCount: Long,

        /**
         * 今日成功推送次数
         */
        val todaySuccessCount: Long,

        /**
         * 今天失败推送次数
         */
        val todayErrorCount: Long,

        /**
         * 管理员列表
         */
        val admins: List<PusherUser>
) {
    companion object {
        private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss")
        private val logger = LoggerFactory.getLogger(SendStatusContext::class.java)
    }

    /**
     * 构造状态文本
     */
    fun buildStatusText(): String {
        return """
               Pusher服务启动时间:${systemStartTime.format(dateTimeFormatter)}
               已运行:${String.format("%.2f", Duration.between(systemStartTime, LocalDateTime.now()).toMinutes().toDouble() / 60)}小时
               总请求次数:${allSuccessCount + allErrorCount}
               总成功次数:${allSuccessCount}
               今日请求次数:${todaySuccessCount + todayErrorCount}
               今日成功次数:${todaySuccessCount}
               """.trimIndent()
    }
}