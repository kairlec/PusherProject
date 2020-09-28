package com.kairlec.error

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import com.kairlec.intf.ResponseDataInterface
import com.kairlec.utils.ResponseDataUtil.getTrackMessage
import org.springframework.http.HttpStatus

@Suppress("unused")
class SKException : RuntimeException {
    private val serviceErrorEnum: ServiceErrorEnum?

    constructor() : super() {
        this.serviceErrorEnum = null
    }

    constructor(serviceErrorEnum: ServiceErrorEnum, cause: Throwable? = null) : super(serviceErrorEnum.msg, cause) {
        this.serviceErrorEnum = serviceErrorEnum
    }

    constructor(detailedMessage: String) : super(detailedMessage) {
        this.serviceErrorEnum = null
    }

    constructor(t: Throwable) : super(t) {
        this.serviceErrorEnum = null
    }

    constructor(detailedMessage: String, t: Throwable) : super(detailedMessage, t) {
        this.serviceErrorEnum = null
    }

    fun getServiceErrorEnum(): ServiceErrorEnum? {
        return serviceErrorEnum
    }

    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    enum class ServiceErrorEnum(override val code: Int, override val msg: String, override var data: Any? = null, @get:JsonIgnore override var status: HttpStatus = HttpStatus.OK) : ResponseDataInterface {
        NO_ERROR(0, "OK"),
        NO_ERROR_BACKWARDS_COMPATIBILITY_WARN(0, "向下兼容模式,当前请求的是老API,请尽量使用新API"),
        UNKNOWN_REQUEST(80000, "未知请求", null, HttpStatus.BAD_REQUEST),
        NO_SUCH_USER(10001, "无此用户", null, HttpStatus.UNAUTHORIZED),
        PERMISSION_DENIED(10002, "没有权限", null, HttpStatus.UNAUTHORIZED),
        VERIFICATION_FAILED(10003, "验证失败", null, HttpStatus.UNAUTHORIZED),
        EMPTY_DATA(10004, "数据为空", null, HttpStatus.BAD_REQUEST),
        MISSING_REQUEST_PART(10005, "缺少请求内容", HttpStatus.BAD_REQUEST),
        AN_EXCEPTION_OCCURRED(90003, "服务异常", HttpStatus.BAD_GATEWAY),
        NOT_YET_SUPPORTED(90004, "尚未支持", null, HttpStatus.BAD_GATEWAY),
        ;

        fun data(data: Any?) = apply {
            this.data = data
        }

        fun status(status: HttpStatus) = apply {
            this.status = status
        }

        val ok
            @JsonIgnore
            get() = code == 0

        val bad
            @JsonIgnore
            get() = code != 0

        fun throwout(cause: Throwable? = null): Nothing = throwout(this, cause)

        companion object {
            fun throwout(error: ServiceErrorEnum, cause: Throwable? = null): Nothing {
                throw SKException(error, cause)
            }

            fun fromException(e: Exception): ServiceErrorEnum {
                return AN_EXCEPTION_OCCURRED.data(e.getTrackMessage())
            }
        }
    }
}