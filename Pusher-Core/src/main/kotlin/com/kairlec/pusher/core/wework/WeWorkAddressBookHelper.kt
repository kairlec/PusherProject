package com.kairlec.pusher.core.wework

import com.kairlec.pojo.wework.Department
import com.kairlec.pojo.wework.InUser
import com.kairlec.pojo.wework.SimpleUser
import com.kairlec.pojo.wework.User
import com.kairlec.pusher.core.PusherExceptions
import com.kairlec.pusher.core.Sender
import com.kairlec.utils.UrlBuilder

@Suppress("unused", "SpellCheckingInspection")
open class WeWorkAddressBookHelper(private val validateCertificateChains: Boolean, private val accessTokenHelper: WeWorkAccessTokenHelper) : WeWorkHelper {

    private val accessToken: String
        get() = accessTokenHelper.accessToken

    fun getUser(userID: String): InUser {
        val url = UrlBuilder("https://qyapi.weixin.qq.com/cgi-bin/user/get")
                .addQueryParameter("access_token", accessToken)
                .addQueryParameter("userid", userID)
                .build()
        return Sender.getResultMap<InUser, PusherExceptions.AddressBookUserException>(url, validateCertificateChains)
    }


    fun getDepartmentList(id: Int? = null): List<Department> {
        val urlBuilder = UrlBuilder("https://qyapi.weixin.qq.com/cgi-bin/department/list")
                .addQueryParameter("access_token", accessToken)
        id?.let { urlBuilder.addQueryParameter("id", id.toString()) }
        val url = urlBuilder.build()
        return Sender.getResultMap<List<Department>, PusherExceptions.AddressBookDepartmentListException>(url, validateCertificateChains, "department")
    }

    fun getUserSimpleList(departmentID: Int, fetchChild: Boolean = true): List<SimpleUser> {
        val url = UrlBuilder("https://qyapi.weixin.qq.com/cgi-bin/user/simplelist")
                .addQueryParameter("access_token", accessToken)
                .addQueryParameter("department_id", departmentID.toString())
                .addQueryParameter("fetch_child", if (fetchChild) "1" else "0")
                .build()
        return Sender.getResultMap<List<SimpleUser>, PusherExceptions.AddressBookUserListException>(url, validateCertificateChains, "userlist")
    }


    fun getUserList(departmentID: Int, fetchChild: Boolean = true): List<User> {
        val url = UrlBuilder("https://qyapi.weixin.qq.com/cgi-bin/user/list")
                .addQueryParameter("access_token", accessToken)
                .addQueryParameter("department_id", departmentID.toString())
                .addQueryParameter("fetch_child", if (fetchChild) "1" else "0")
                .build()
        return Sender.getResultMap<List<User>, PusherExceptions.AddressBookUserListException>(url, validateCertificateChains, "userlist")
    }

}