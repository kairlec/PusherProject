package com.kairlec.pusher.core.wechat

import com.kairlec.pojo.wechat.MiniProgram
import com.kairlec.pojo.wechat.Template
import com.kairlec.pojo.wechat.TemplateValue
import com.kairlec.pusher.core.PusherExceptions
import com.kairlec.pusher.core.Sender
import com.kairlec.utils.UrlBuilder

open class WeChatTemplateHelper(private val validateCertificateChains: Boolean, private val accessTokenHelper: WeChatAccessTokenHelper) : WeChatHelper {

    fun getTemplateList(): List<Template> {
        val url = UrlBuilder("https://api.weixin.qq.com/cgi-bin/template/get_all_private_template")
                .addQueryParameter("access_token", accessTokenHelper.accessToken)
                .build()
        return Sender.getResultMap<List<Template>, PusherExceptions.TemplateException>(url, validateCertificateChains, "template_list")
    }

    fun deleteTemplate(templateID: String) {
        val url = UrlBuilder("https://api.weixin.qq.com/cgi-bin/template/del_private_template")
                .addQueryParameter("access_token", accessTokenHelper.accessToken)
                .build()
        Sender.postJsonResultMap<Unit, PusherExceptions.TemplateException>(url, object {
            val template_id = templateID
        }, validateCertificateChains)
    }

    fun sendTemplate(touser: String, templateID: String, values: Iterator<TemplateValue>, url: String? = null, miniProgram: MiniProgram? = null) {
        val sendurl = UrlBuilder("https://api.weixin.qq.com/cgi-bin/message/template/send")
                .addQueryParameter("access_token", accessTokenHelper.accessToken)
                .build()
        val map = HashMap<String, Any>()
        values.forEach {
            map[it.keyword] = object {
                val value = it.value
                val color = it.color
            }
        }
        Sender.postJsonResultMap<Unit, PusherExceptions.TemplateException>(sendurl, object {
            val touser = touser
            val template_id = templateID
            val url = url
            val miniProgram = miniProgram
            val data = map
        }, validateCertificateChains)
    }
}