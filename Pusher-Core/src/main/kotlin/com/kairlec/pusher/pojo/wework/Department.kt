package com.kairlec.pusher.pojo.wework


import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 通过department/list获取到的部门信息
 *
 * API: https://work.weixin.qq.com/api/doc/90001/90143/90344
 */
data class Department(
        /**
         * 创建的部门id
         */
        @JsonProperty("id")
        val id: Int,
        /**
         * 	部门名称，此字段从2019年12月30日起，对新创建第三方应用不再返回，2020年6月30日起，对所有历史第三方应用不再返回，后续第三方仅通讯录应用可获取，第三方页面需要通过通讯录展示组件来展示部门名称
         */
        @JsonProperty("name")
        val name: String,
        /**
         * 	英文名称
         */
        @JsonProperty("name_en")
        val nameEn: String,
        /**
         * 在父部门中的次序值。order值大的排序靠前。值范围是[0, 2^32)
         */
        @JsonProperty("order")
        val order: Int,
        /**
         * 父部门id。根部门为1
         */
        @JsonProperty("parentid")
        val parentID: Int
)