package com.kairlec.utils

import com.fasterxml.jackson.databind.BeanProperty
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializationConfig
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider
import com.fasterxml.jackson.databind.ser.SerializerFactory


class CustomSerializerProvider : DefaultSerializerProvider {
    constructor() : super()
    constructor(provider: CustomSerializerProvider, config: SerializationConfig, jsf: SerializerFactory) : super(provider, config, jsf)

    override fun createInstance(config: SerializationConfig, jsf: SerializerFactory): CustomSerializerProvider {
        return CustomSerializerProvider(this, config, jsf)
    }

    override fun findNullValueSerializer(property: BeanProperty): JsonSerializer<Any?> {
        return if (property.type.rawClass == String::class.java) Serializers.EMPTY_STRING_SERIALIZER_INSTANCE else super.findNullValueSerializer(property)
    }
}