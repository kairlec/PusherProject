package com.kairlec.pusher.core.wechat

import com.kairlec.pojo.wechat.PartTagUserList
import com.kairlec.pojo.wechat.PartUserList
import com.kairlec.pojo.wechat.Tag
import com.kairlec.pojo.wechat.User
import com.kairlec.pusher.core.PusherExceptions
import com.kairlec.pusher.core.Sender
import com.kairlec.pusher.core.objectMapper
import com.kairlec.utils.UrlBuilder

open class WeChatUserHelper(private val validateCertificateChains: Boolean, private val accessTokenHelper: WeChatAccessTokenHelper) : WeChatHelper {

    fun getUserList(nextOpenID: String? = null): PartUserList {
        val url = nextOpenID?.let {
            UrlBuilder("https://api.weixin.qq.com/cgi-bin/user/get")
                    .addQueryParameter("access_token", accessTokenHelper.accessToken)
                    .addQueryParameter("next_openid", nextOpenID)
                    .build()
        } ?: UrlBuilder("https://api.weixin.qq.com/cgi-bin/user/get")
                .addQueryParameter("access_token", accessTokenHelper.accessToken)
                .build()
        return Sender.getResultMap<PartUserList, PusherExceptions.AddressBookUserListException>(url, validateCertificateChains) {
            if (it["count"].asInt() == 0) {
                objectMapper.valueToTree(PartUserList(0, PartUserList.Data(emptyList()), "", it["total"].asInt()))
            } else {
                it
            }
        }
    }

    fun getAllUserList(): List<String> {
        var nextOpenID: String? = null
        var count = 0
        var total = -1
        var allList: ArrayList<String>? = null
        while (count != total) {
            val list = getUserList(nextOpenID)
            if (total == -1) {
                total = list.total
                allList = ArrayList(total)
            }
            count += list.count
            nextOpenID = list.nextOpenid
            allList!!.addAll(list.data.openid)
        }
        return allList!!
    }

    fun getUserInfo(openID: String): User {
        val url = UrlBuilder("https://api.weixin.qq.com/cgi-bin/user/info")
                .addQueryParameter("access_token", accessTokenHelper.accessToken)
                .addQueryParameter("openid", openID)
                .build()
        return Sender.getResultMap<User, PusherExceptions.AddressBookUserListException>(url, validateCertificateChains)
    }

    fun getAllUserInfo(openIDs: List<String>): List<User> {
        val url = UrlBuilder("https://api.weixin.qq.com/cgi-bin/user/info/batchget")
                .addQueryParameter("access_token", accessTokenHelper.accessToken)
                .build()
        return Sender.postJsonResultMap<List<User>, PusherExceptions.AddressBookUserListException>(url, object {
            val user_list = openIDs.map {
                object {
                    val openid = it
                }
            }
        }, validateCertificateChains, "user_info_list")
    }

    fun createTag(name: String): Tag {
        val url = UrlBuilder("https://api.weixin.qq.com/cgi-bin/tags/create")
                .addQueryParameter("access_token", accessTokenHelper.accessToken)
                .build()
        return Sender.postJsonResultMap<Tag, PusherExceptions.AddressBookTagException>(url, object {
            val tag = object {
                val name = name
            }
        }, validateCertificateChains, "tag")
    }

    fun getAllTags(): List<Tag> {
        val url = UrlBuilder("https://api.weixin.qq.com/cgi-bin/tags/get")
                .addQueryParameter("access_token", accessTokenHelper.accessToken)
                .build()
        return Sender.getResultMap<List<Tag>, PusherExceptions.AddressBookTagException>(url, validateCertificateChains, "tags")
    }

    fun editTag(tagID: Int, name: String) {
        val url = UrlBuilder("https://api.weixin.qq.com/cgi-bin/tags/update")
                .addQueryParameter("access_token", accessTokenHelper.accessToken)
                .build()
        Sender.postJsonResultMap<Unit, PusherExceptions.AddressBookTagException>(url, object {
            val tag = object {
                val id = tagID
                val name = name
            }
        }, validateCertificateChains)
    }

    fun deleteTag(tagID: Int) {
        val url = UrlBuilder("https://api.weixin.qq.com/cgi-bin/tags/delete")
                .addQueryParameter("access_token", accessTokenHelper.accessToken)
                .build()
        Sender.postJsonResultMap<Unit, PusherExceptions.AddressBookTagException>(url, object {
            val tag = object {
                val id = tagID
            }
        }, validateCertificateChains)
    }

    fun getTagUserList(tagID: Int, nextOpenID: String? = null): PartTagUserList {
        val url = UrlBuilder("https://api.weixin.qq.com/cgi-bin/user/tag/get")
                .addQueryParameter("access_token", accessTokenHelper.accessToken)
                .build()
        return Sender.postJsonResultMap<PartTagUserList, PusherExceptions.AddressBookTagException>(url, object {
            val tagid = tagID
            val next_openid = nextOpenID ?: ""
        }, validateCertificateChains) {
            if (it["count"].asInt() == 0) {
                objectMapper.valueToTree(PartTagUserList(0, PartTagUserList.Data(emptyList()), ""))
            } else {
                it
            }
        }
    }


    fun getAllTagUserList(tagID: Int): List<String> {
        var nextOpenID: String? = null
        var count = 0
        val allList = ArrayList<String>()
        do {
            val list = getTagUserList(tagID, nextOpenID)
            count += list.count
            nextOpenID = list.nextOpenid
            allList.addAll(list.data.openid)
        } while (nextOpenID == "")
        return allList
    }

    fun setUserTag(tagID: Int, vararg openID: String) {
        val url = UrlBuilder("https://api.weixin.qq.com/cgi-bin/tags/members/batchtagging")
                .addQueryParameter("access_token", accessTokenHelper.accessToken)
                .build()
        Sender.postJsonResultMap<Unit, PusherExceptions.AddressBookTagException>(url, object {
            val tagid = tagID
            val openid_list = openID
        }, validateCertificateChains)
    }

    fun cancelUserTag(tagID: Int, vararg openID: String) {
        val url = UrlBuilder("https://api.weixin.qq.com/cgi-bin/tags/members/batchuntagging")
                .addQueryParameter("access_token", accessTokenHelper.accessToken)
                .build()
        Sender.postJsonResultMap<Unit, PusherExceptions.AddressBookTagException>(url, object {
            val tagid = tagID
            val openid_list = openID
        }, validateCertificateChains)
    }

    fun getUserTags(openID: String): List<Int> {
        val url = UrlBuilder("https://api.weixin.qq.com/cgi-bin/tags/getidlist")
                .addQueryParameter("access_token", accessTokenHelper.accessToken)
                .build()
        return Sender.postJsonResultMap<List<Int>, PusherExceptions.AddressBookTagException>(url, object {
            val openid = openID
        }, validateCertificateChains, "tagid_list")
    }
}