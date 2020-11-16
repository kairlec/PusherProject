package com.kairlec.pusher.core.wework

import com.kairlec.pusher.core.PusherExceptions
import com.kairlec.pusher.core.Sender
import com.kairlec.pusher.pojo.wework.Department
import com.kairlec.pusher.pojo.wework.InUser
import com.kairlec.pusher.pojo.wework.SimpleUser
import com.kairlec.pusher.pojo.wework.User
import com.kairlec.utils.UrlBuilder

@Suppress("unused", "SpellCheckingInspection")
open class WeWorkAddressBookHelper(private val validateCertificateChains: Boolean, private val accessTokenHelper: WeWorkAccessTokenHelper) : WeWorkHelper {

    private val accessToken: String
        get() = accessTokenHelper.accessToken

    /**
     * 获取用户
     * @param userID 用户ID
     * @see InUser
     */
    fun getUser(userID: String): InUser {
        val url = UrlBuilder("https://qyapi.weixin.qq.com/cgi-bin/user/get")
                .addQueryParameter("access_token", accessToken)
                .addQueryParameter("userid", userID)
                .build()
        return Sender.getResultMap<InUser, PusherExceptions.AddressBookUserException>(url, validateCertificateChains)
    }

    /**
     * 获取部门列表
     * @param id 部门id
     * @see Department
     */
    fun getDepartmentList(id: Int? = null): List<Department> {
        val urlBuilder = UrlBuilder("https://qyapi.weixin.qq.com/cgi-bin/department/list")
                .addQueryParameter("access_token", accessToken)
        id?.let { urlBuilder.addQueryParameter("id", id.toString()) }
        val url = urlBuilder.build()
        return Sender.getResultMap<List<Department>, PusherExceptions.AddressBookDepartmentListException>(url, validateCertificateChains, "department")
    }

    /**
     * 获取基本用户信息列表
     * @param departmentID 部门id
     * @see SimpleUser
     */
    fun getUserSimpleList(departmentID: Int, fetchChild: Boolean = true): List<SimpleUser> {
        val url = UrlBuilder("https://qyapi.weixin.qq.com/cgi-bin/user/simplelist")
                .addQueryParameter("access_token", accessToken)
                .addQueryParameter("department_id", departmentID.toString())
                .addQueryParameter("fetch_child", if (fetchChild) "1" else "0")
                .build()
        return Sender.getResultMap<List<SimpleUser>, PusherExceptions.AddressBookUserListException>(url, validateCertificateChains, "userlist").apply {
            forEach {
                it.openUserID = getOpenUserId(it.userID)
            }
        }
    }

    /**
     * 获取详细用户信息列表
     * @param departmentID 部门id
     * @param fetchChild 是否递归获取子用户
     * @see User
     */
    fun getUserList(departmentID: Int, fetchChild: Boolean = true): List<User> {
        val url = UrlBuilder("https://qyapi.weixin.qq.com/cgi-bin/user/list")
                .addQueryParameter("access_token", accessToken)
                .addQueryParameter("department_id", departmentID.toString())
                .addQueryParameter("fetch_child", if (fetchChild) "1" else "0")
                .build()
        return Sender.getResultMap<List<User>, PusherExceptions.AddressBookUserListException>(url, validateCertificateChains, "userlist").apply {
            forEach {
                it.openUserID = getOpenUserId(it.userID)
            }
        }
    }

    /**
     * 根据用户userid,获取其openUserId
     * @param userID UserId
     * @return OpenUserId
     */
    private fun getOpenUserId(userID: String): String {
        val url = UrlBuilder("https://qyapi.weixin.qq.com/cgi-bin/user/convert_to_openid")
                .addQueryParameter("access_token", accessToken)
                .build()
        return Sender.postJsonResultMap<String, PusherExceptions.AddressBookUserListException>(url, object {
            val userid = userID
        }, validateCertificateChains, "openid")
    }

}