package com.kairlec.pusher.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.kairlec.pusher.annotation.condition.StatusReportCondition
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Conditional
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.io.File
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

/**
 * 计数统计工具
 */
@Component
@Conditional(StatusReportCondition::class)
class StatusCountUtil {

    /**
     * 增加一次成功计数
     */
    fun addSuccessCount() {
        pojo.successCount++
        pojo.todaySuccessCount++
    }

    /**
     * 增加一次失败计数
     */
    fun addErrorCount() {
        pojo.errorCount++
        pojo.todayErrorCount++
    }

    /**
     * 失败计数
     */
    val errorCount: Long
        get() = pojo.errorCount

    val successCount: Long
        get() = pojo.successCount

    val todayErrorCount: Long
        get() = pojo.todayErrorCount

    val todaySuccessCount: Long
        get() = pojo.todaySuccessCount

    val beginTime: LocalDateTime
        get() = LocalDateTime.ofEpochSecond(pojo.beginTime, 0, ZoneOffset.ofHours(8));

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    /**
     * 初始化事件
     */
    @PostConstruct
    fun init() {
        pojo = if (!jsonFile.exists()) {
            StatusCountPOJO(0, 0, 0, 0, LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8")))
        } else {
            objectMapper.readValue(jsonFile)
        }
    }

    /**
     * 摧毁事件
     */
    @PreDestroy
    fun destroy() {
        objectMapper.writeValue(jsonFile, pojo)
    }

    /**
     * 清空今天的计数
     */
    @Scheduled(cron = "0 0 0 * * ?")
    fun clearTodayCount() {
        pojo.todayErrorCount = 0
        pojo.todaySuccessCount = 0
    }

    /**
     * 计数类实体
     */
    data class StatusCountPOJO(
            var errorCount: Long,
            var successCount: Long,
            var todayErrorCount: Long,
            var todaySuccessCount: Long,
            var beginTime: Long
    )

    companion object {
        private lateinit var pojo: StatusCountPOJO
        private val jsonFile = File("statusCount.json")
    }
}