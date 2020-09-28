package com.kairlec.utils

import java.net.URLEncoder

/**
 * URL编码
 */
fun String.urlEncode(): String = if (isEmpty()) this else URLEncoder.encode(this, Charsets.UTF_8)

