package com.kairlec.pojo.wechat


import com.fasterxml.jackson.annotation.JsonProperty

data class Tag(
        @JsonProperty("id")
        val id: Int,
        @JsonProperty("name")
        val name: String,
        @JsonProperty("count")
        val count: Int = 0
)