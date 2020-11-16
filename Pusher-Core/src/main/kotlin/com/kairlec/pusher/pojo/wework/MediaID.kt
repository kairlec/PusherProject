package com.kairlec.pusher.pojo.wework

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 上传临时素材后得到的mediaId
 *
 * API: https://work.weixin.qq.com/api/doc/90001/90143/90389
 */
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