package com.kairlec.utils

import com.kairlec.error.SKException
import com.kairlec.intf.ResponseDataInterface
import com.kairlec.pusher.core.PusherException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.multipart.support.MissingServletRequestPartException
import java.io.ByteArrayOutputStream
import java.io.PrintWriter

/**
 * 返回体消息辅助器
 */
object ResponseDataUtil {

    private val logger = LoggerFactory.getLogger(ResponseDataUtil::class.java)

    val Any?.responseOK
        get() = SKException.ServiceErrorEnum.NO_ERROR.data(this)

    fun Exception.getTrackMessage(): String {
        lateinit var expMessage: String
        ByteArrayOutputStream().use {
            this.printStackTrace(PrintWriter(it, true))
            expMessage = it.toString()
        }
        return expMessage
    }

    private fun fromPusherException(exception: PusherException) = object : ResponseDataInterface {
        override val code = exception.code
        override val msg = exception.message ?: ""
        override val data = exception.getTrackMessage()
        override val status: HttpStatus = HttpStatus.BAD_REQUEST
    }

    val Exception?.responseError: ResponseDataInterface
        get() {
            if (this == null) {
                return SKException.ServiceErrorEnum.UNKNOWN_REQUEST
            }
            logger.error(this.message, this)
            if (this is SKException) {
                logger.error("a SKException has throwout:${this.message}")
                this.getServiceErrorEnum()?.let {
                    logger.error("${it.msg} with data ${it.data}")
                    return it
                }
            }
            if (this is PusherException) {
                logger.error("a PusherException has throwout:[${this.code}]${this.message}")
                return fromPusherException(this)
            }
            if (this is MissingServletRequestPartException) {
                logger.error("a MissingServletRequestPartException has throwout:${this.message}")
                return SKException.ServiceErrorEnum.MISSING_REQUEST_PART.data(this.requestPartName)
            }
            if (this is MissingServletRequestParameterException) {
                logger.error("a MissingServletRequestParameterException has throwout:${this.message}")
                return SKException.ServiceErrorEnum.MISSING_REQUEST_PART.data("[${this.parameterType}]${this.parameterName}")
            }
            logger.error("a Exception has throwout:${this.message}")
            return SKException.ServiceErrorEnum.fromException(this)
        }

}