package com.kairlec.utils

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider


class Serializers : JsonSerializer<Any>() {
    override fun serialize(o: Any, jsonGenerator: JsonGenerator, serializerProvider: SerializerProvider) {
        jsonGenerator.writeString("")
    }

    private class EmptyStringSerializer : JsonSerializer<Any?>() {
        override fun serialize(o: Any?, jsonGenerator: JsonGenerator, serializerProvider: SerializerProvider) {
            jsonGenerator.writeString("")
        }
    }

    companion object {
        val EMPTY_STRING_SERIALIZER_INSTANCE: JsonSerializer<Any?> = EmptyStringSerializer()
    }
}