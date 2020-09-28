package com.kairlec.lib

import kotlin.random.Random

fun CharRange.randomString(length: Int) = (1..length).map { randomChar }.joinToString("")

val CharRange.randomChar: Char
    get() = Random.nextInt(first.toInt(), last.toInt()).toChar()

fun CharRange.Companion.randomString(length: Int, vararg ranges: CharRange): String {
    return (1..length).map { ranges[(Random.nextInt(ranges.size))].randomChar }.joinToString("")
}