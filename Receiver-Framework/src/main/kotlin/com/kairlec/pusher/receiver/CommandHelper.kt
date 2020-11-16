package com.kairlec.pusher.receiver

import com.kairlec.pusher.receiver.msg.ReceiveMsg
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * 命令行辅助器,可以辅助解析命令行命令
 * @param commandString 原始命令行
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
open class CommandHelper(commandString: String, val msg: ReceiveMsg, private val separator: String = " ") : Iterator<String> {
    /**
     * 原始命令
     */
    val rawCommand = commandString.trim()

    /**
     * 命令列表
     */
    val commandList = rawCommand.split(separator)

    /**
     * 当前命令
     */
    var currentCommandIndex = -1

    /**
     * 锁,防止多线程同时读取和修改会造成命令不一致
     */
    private val nextLock = ReentrantLock()

    /**
     * 剩余的所有命令,以什么字符分割的就以什么字符拼接
     * @return 获取剩余的所有命令
     */
    val remainingCommand
        get() = commandList.subList(currentCommandIndex + 1, commandList.size).joinToString(separator)

    /**
     * 是否是有效的命令
     * @return 是否是有效的命令
     */
    fun isValidCommand(): Boolean {
        return rawCommand.startsWith("/") && rawCommand.length > 1
    }

    /**
     * 匹配下一项命令
     * @param next 下一条命令
     * @param trim 是否去除空格
     * @param ignoreCase 是否不区分大小写
     * @return 是否匹配
     */
    fun matchNext(next: String, trim: Boolean = true, ignoreCase: Boolean = false): Boolean {
        if (hasNext()) {
            nextLock.withLock {
                return if (trim) {
                    commandList[currentCommandIndex + 1].trim()
                } else {
                    commandList[currentCommandIndex + 1]
                }.equals(next, ignoreCase)
            }
        }
        return false
    }

    /**
     * 匹配当前命令
     * @param current 下一条命令
     * @param trim 是否去除空格
     * @param ignoreCase 是否不区分大小写
     * @return 是否匹配
     */
    fun matchCurrent(current: String, trim: Boolean = true, ignoreCase: Boolean = false): Boolean {
        if (isValidCommand()) {
            nextLock.withLock {
                return if (trim) {
                    commandList[currentCommandIndex].trim()
                } else {
                    commandList[currentCommandIndex]
                }.equals(current, ignoreCase)
            }
        }
        return false
    }

    /**
     * 基命令(第一条命令)
     * @return 基命令
     */
    fun baseCommand(): String {
        return commandList[0].substring(1)
    }

    /**
     * 后面是否还有命令
     * @return 是否还有命令
     */
    override fun hasNext(): Boolean {
        return currentCommandIndex < commandList.size - 1
    }

    /**
     * 前面是否还有命令
     * @return 是否还有命令
     */
    fun hasPrev(): Boolean {
        return currentCommandIndex <= 0
    }

    /**
     * 上一条无处理命令,需要预先判断[hasPrev]
     * @return 上一条命令
     */
    fun prev(): String {
        nextLock.withLock {
            currentCommandIndex--
        }
        return current()
    }

    /**
     * 上一条已经过[String.trim]处理命令,需要预先判断[hasPrev]
     * @return 上一条命令
     */
    fun prevCommand() = prev().trim()

    /**
     * 下一条已经过[String.trim]处理命令,需要预先判断[hasNext]
     * @return 下一条命令
     */
    fun nextCommand() = next().trim()

    /**
     * 当前命令,必须要是[hasNext]判断过的
     * @return 当前命令
     */
    fun currentCommand(): String = current().trim()

    /**
     * 下一条无处理命令,需要预先判断[hasNext]
     * @return 下一条命令
     */
    override fun next(): String {
        nextLock.withLock {
            currentCommandIndex++
        }
        return current()
    }

    /**
     * 下一条无处理命令,需要预先判断[hasNext]
     * @return 下一条命令
     */
    fun current(): String {
        nextLock.withLock {
            return commandList[currentCommandIndex]
        }
    }

}

