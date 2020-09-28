package com.kairlec.pojo.wework

import com.fasterxml.jackson.annotation.JsonProperty

data class MediaID(
        /**
         * 媒体文件类型，分别有图片（image）、语音（voice）、视频（video），普通文件(file)
         */
        @JsonProperty("type")
        val type: String,
        /**
         * 媒体文件上传后获取的唯一标识，3天内有效
         */
        @JsonProperty("media_id")
        val mediaID: String,
        /**
         * 媒体文件上传时间戳
         */
        @JsonProperty("created_at")
        val createdAt: Long
)