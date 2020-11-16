package com.kairlec.pusher.core

import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.kairlec.utils.CustomSerializerProvider


val objectMapper = run {
    val mapper = ObjectMapper()
    mapper.configOverride(String::class.java).setterInfo = JsonSetter.Value.forValueNulls(Nulls.AS_EMPTY)
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
    mapper.setSerializerProvider(CustomSerializerProvider())
    mapper
}
