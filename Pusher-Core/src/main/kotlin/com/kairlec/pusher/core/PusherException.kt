package com.kairlec.pusher.core

open class PusherException(open val code: Int, message: String, cause: Throwable?) : Exception(message, cause)

sealed class PusherExceptions {
    class AccessTokenException(override val code: Int, cause: Throwable? = null, message: String = "Failed to post access token") : PusherException(code, message, cause)
    class SendMessageException(override val code: Int, cause: Throwable? = null, message: String = "Failed to post message to send") : PusherException(code, message, cause)
    class AddressBookDepartmentListException(override val code: Int, cause: Throwable? = null, message: String = "Failed to post department list") : PusherException(code, message, cause)
    class AddressBookUserListException(override val code: Int, cause: Throwable? = null, message: String = "Failed to post user list") : PusherException(code, message, cause)
    class AddressBookUserException(override val code: Int, cause: Throwable? = null, message: String = "Failed to post user") : PusherException(code, message, cause)
    class AddressBookTagException(override val code: Int, cause: Throwable? = null, message: String = "Failed to post tag") : PusherException(code, message, cause)
    class TemplateException(override val code: Int, cause: Throwable? = null, message: String = "Failed to post tag") : PusherException(code, message, cause)
    class UploadMediaException(override val code: Int, cause: Throwable? = null, message: String = "Failed to upload media file") : PusherException(code, message, cause)
}