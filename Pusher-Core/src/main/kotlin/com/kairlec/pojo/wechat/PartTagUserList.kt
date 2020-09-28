package com.kairlec.pojo.wechat


import com.fasterxml.jackson.annotation.JsonProperty

data class PartTagUserList(
        /**
         * 这次获取的粉丝数量
         */
        @JsonProperty("count")
        val count: Int,
        /**
         * 粉丝列表
         */
        @JsonProperty("data")
        val data: Data,
        /**
         * 拉取列表最后一个用户的openid
         */
        @JsonProperty("next_openid")
        val nextOpenid: String
) {
        data class Data(
                @JsonProperty("openid")
                val openid: List<String>
        )
}