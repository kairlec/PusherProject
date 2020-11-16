package com.kairlec.pusher.pojo.wework


import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 通过user/get接口获取到的成员信息
 *
 * API: https://work.weixin.qq.com/api/doc/90001/90143/90332
 */
data class InUser(
        /**
         * 地址。第三方仅通讯录应用可获取
         */
        @JsonProperty("address")
        val address: String,
        /**
         * 别名；第三方仅通讯录应用可获取
         */
        @JsonProperty("alias")
        val alias: String,
        /**
         * 头像url。 第三方仅通讯录应用可获取
         */
        @JsonProperty("avatar")
        val avatar: String,
        /**
         * 成员所属部门id列表，仅返回该应用有查看权限的部门id
         */
        @JsonProperty("department")
        val department: List<Int>,
        /**
         * 邮箱，第三方仅通讯录应用可获取
         */
        @JsonProperty("email")
        val email: String,
        /**
         * 扩展属性，第三方仅通讯录应用可获取
         */
        @JsonProperty("extattr")
        val extAttr: ExtAttr?,
        /**
         * 对外职务，如果设置了该值，则以此作为对外展示的职务，否则以position来展示。第三方仅通讯录应用可获取
         */
        @JsonProperty("external_position")
        val externalPosition: String,
        /**
         * 成员对外属性，字段详情见对外属性；第三方仅通讯录应用可获取
         */
        @JsonProperty("external_profile")
        val externalProfile: ExternalProfile?,
        /**
         * 性别。0表示未定义，1表示男性，2表示女性
         */
        @JsonProperty("gender")
        val gender: String,
        /**
         * 表示在所在的部门内是否为上级。；第三方仅通讯录应用可获取
         */
        @JsonProperty("is_leader_in_dept")
        val isLeaderInDept: List<Int>,
        /**
         * 主部门
         */
        @JsonProperty("main_department")
        val mainDepartment: Int,
        /**
         * 手机号码，第三方仅通讯录应用可获取
         */
        @JsonProperty("mobile")
        val mobile: String,
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
         * 部门内的排序值，默认为0。数量必须和department一致，数值越大排序越前面。值范围是[0, 2^32)
         */
        @JsonProperty("order")
        val order: List<Int>,
        /**
         * 职务信息；第三方仅通讯录应用可获取
         */
        @JsonProperty("position")
        val position: String,
        /**
         * 员工个人二维码，扫描可添加为外部联系人(注意返回的是一个url，可在浏览器上打开该url以展示二维码)；第三方仅通讯录应用可获取
         */
        @JsonProperty("qr_code")
        val qrCode: String,
        /**
         * 激活状态: 1=已激活，2=已禁用，4=未激活，5=退出企业。已激活代表已激活企业微信或已关注微工作台（原企业号）。未激活代表既未激活企业微信又未关注微工作台（原企业号）。
         */
        @JsonProperty("status")
        val status: Int,
        /**
         * 座机。第三方仅通讯录应用可获取
         */
        @JsonProperty("telephone")
        val telephone: String,
        /**
         * 头像缩略图url。第三方仅通讯录应用可获取
         */
        @JsonProperty("thumb_avatar")
        val thumbAvatar: String,
        /**
         * 成员UserID。对应管理端的帐号，企业内必须唯一。不区分大小写，长度为1~64个字节
         */
        @JsonProperty("userid")
        val userID: String
) {
    data class ExtAttr(
            @JsonProperty("attrs")
            val attrs: List<Attr> = ArrayList()
    ) {
        data class Attr(
                @JsonProperty("name")
                val name: String,
                @JsonProperty("text")
                val text: Text?,
                @JsonProperty("type")
                val type: Int,
                @JsonProperty("web")
                val web: Web?
        ) {
            data class Text(
                    @JsonProperty("value")
                    val value: String
            )

            data class Web(
                    @JsonProperty("title")
                    val title: String,
                    @JsonProperty("url")
                    val url: String
            )
        }
    }

    data class ExternalProfile(
            @JsonProperty("external_attr")
            val externalAttr: List<ExternalAttr> = ArrayList(),
            @JsonProperty("external_corp_name")
            val externalCorpName: String
    ) {
        data class ExternalAttr(
                @JsonProperty("miniprogram")
                val miniProgram: MiniProgram?,
                @JsonProperty("name")
                val name: String,
                @JsonProperty("text")
                val text: Text?,
                @JsonProperty("type")
                val type: Int,
                @JsonProperty("web")
                val web: Web?
        ) {
            data class MiniProgram(
                    @JsonProperty("appid")
                    val appID: String,
                    @JsonProperty("pagepath")
                    val pagePath: String,
                    @JsonProperty("title")
                    val title: String
            )

            data class Text(
                    @JsonProperty("value")
                    val value: String
            )

            data class Web(
                    @JsonProperty("title")
                    val title: String,
                    @JsonProperty("url")
                    val url: String
            )
        }
    }
}