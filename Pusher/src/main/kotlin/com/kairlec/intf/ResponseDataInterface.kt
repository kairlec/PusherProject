package com.kairlec.intf

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.http.HttpStatus

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
interface ResponseDataInterface {
    val code: Int
    val msg: String
    val data: Any?

    @get:JsonIgnore
    val status: HttpStatus
}
