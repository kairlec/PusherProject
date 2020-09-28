package com.kairlec.pojo.wechat


import com.fasterxml.jackson.annotation.JsonProperty

data class PartUserList(
        /**
         * 拉取的OPENID个数，最大值为10000
         */
        @JsonProperty("count")
        val count: Int,
        /**
         * 列表数据，OPENID的列表
         */
        @JsonProperty("data")
        val data: Data,
        /**
         * 拉取列表的最后一个用户的OPENID
         */
        @JsonProperty("next_openid")
        val nextOpenid: String,
        /**
         * 关注该公众账号的总用户数
         */
        @JsonProperty("total")
        val total: Int
) {
    data class Data(
            @JsonProperty("openid")
            val openid: List<String>
    )
}