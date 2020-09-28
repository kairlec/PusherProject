package com.kairlec.utils

import com.kairlec.error.SKException
import com.kairlec.pojo.PusherUser

fun check(pusherUsers: Map<String, *>, userid: String, token: String, touser: String) {
    if (userid !in pusherUsers) {
        SKException.ServiceErrorEnum.NO_SUCH_USER.throwout()
    }
    val user = pusherUsers[userid] as? PusherUser ?: SKException.ServiceErrorEnum.NO_SUCH_USER.throwout()
    if (user.token != token) {
        SKException.ServiceErrorEnum.VERIFICATION_FAILED.throwout()
    }
    if (touser != userid && !user.admin) {
        SKException.ServiceErrorEnum.PERMISSION_DENIED.throwout()
    }
}
