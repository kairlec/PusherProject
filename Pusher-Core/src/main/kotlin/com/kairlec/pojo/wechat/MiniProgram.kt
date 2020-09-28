package com.kairlec.pojo.wechat

import com.fasterxml.jackson.annotation.JsonProperty


@Suppress("SpellCheckingInspection")
data class MiniProgram(
        @JsonProperty("appid")
        val appID: String,
        @JsonProperty("pagepath")
        val pagePath: String
)