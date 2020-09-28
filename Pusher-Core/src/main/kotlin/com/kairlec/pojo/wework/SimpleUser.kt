package com.kairlec.pojo.wework


import com.fasterxml.jackson.annotation.JsonProperty

data class SimpleUser(
        /**
         * 成员所属部门列表。列表项为部门ID，32位整型
         */
        @JsonProperty("department")
        val department: List<Int>,
        /**
         * 成员名称，此字段从2019年12月30日起，对新创建第三方应用不再返回，2020年6月30日起，对所有历史第三方应用不再返回，后续第三方仅通讯录应用可获取，第三方页面需要通过通讯录展示组件来展示名字
         */
        @JsonProperty("name")
        val name: String,
        /**
         * 全局唯一。对于同一个服务商，不同应用获取到企业内同一个成员的open_userid是相同的，最多64个字节。仅第三方应用可获取
         */
        @JsonProperty("open_userid")
        val openUserID: String,
        /**
         * 成员UserID。对应管理端的帐号
         */
        @JsonProperty("userid")
        val userID: String
)