package com.kairlec.pojo.wechat


import com.fasterxml.jackson.annotation.JsonProperty

data class Template(
        @JsonProperty("content")
        val content: String,
        @JsonProperty("deputy_industry")
        val deputyIndustry: String,
        @JsonProperty("example")
        val example: String,
        @JsonProperty("primary_industry")
        val primaryIndustry: String,
        @JsonProperty("template_id")
        val templateId: String,
        @JsonProperty("title")
        val title: String
)