package com.kairlec.pusher.openapi.pojo

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.util.*
import javax.persistence.AttributeConverter
import kotlin.math.max

val objectMapper = jacksonObjectMapper()


internal class CustomHashSetConverter : AttributeConverter<HashSet<String>, String> {
    override fun convertToDatabaseColumn(set: HashSet<String>?): String {
        return if (set == null) {
            "{}"
        } else PusherUserPushConfig.convertConfigToString(set)
    }

    override fun convertToEntityAttribute(s: String): HashSet<String> {
        return PusherUserPushConfig.convertStringToConfig(s)
    }
}


internal class CustomHashMapConverter : AttributeConverter<HashMap<String, Any>, String> {
    override fun convertToDatabaseColumn(map: HashMap<String, Any>?): String {
        return if (map == null) {
            "{}"
        } else PusherUser.convertConfigToString(map)
    }

    override fun convertToEntityAttribute(s: String): HashMap<String, Any> {
        return PusherUser.convertStringToConfig(s)
    }
}


internal fun similar(s: String, t: String): Double {
    val n = s.length
    val m = t.length
    if (n == 0) {
        return m.toDouble()
    }
    if (m == 0) {
        return n.toDouble()
    }
    val mp = Array(n + 1) { Array(m + 1) { it }.apply { set(0, it) } }
    for (i in 1..n) {
        s[i - 1].let {
            for (j in 1..m) {
                mp[i][j] = minOf(mp[i - 1][j] + 1, mp[i][j - 1] + 1, mp[i - 1][j - 1] + if (it == t[j - 1]) 0 else 1)
            }
        }
    }
    return (1 - mp[n][m].toDouble() / max(s.length, t.length))
}

internal fun Double.cutOff(target: Double, to: Double = 0.0): Double = if (this < target) to else this

internal fun <T> Pair<Double, T>.cutOff(target: Double): T? = if (first < target) null else second
