package com.kairlec.pojo.wework

enum class MediaTypeEnum(val typeString: String,val maxSize: Long,val allowExt: List<String>? = null) {
    /**
     * 图片（image）：2MB，支持JPG,PNG格式
     */
    IMAGE("image", 2097150, listOf("JPG", "PNG")),
    /**
     * 语音（voice） ：2MB，播放长度不超过60s，仅支持AMR格式
     */
    VOICE("voice", 2097150, listOf("AMR")),
    /**
     * 视频（video） ：10MB，支持MP4格式
     */
    VIDEO("video", 10485760, listOf("MP4")),
    /**
     * 普通文件（file）：20MB
     */
    FILE("file", 20971520),
    ;
}