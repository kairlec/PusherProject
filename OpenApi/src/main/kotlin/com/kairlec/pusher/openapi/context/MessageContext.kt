package com.kairlec.pusher.openapi.context

import com.kairlec.pusher.receiver.msg.ReceiveMsg

data class MessageContext(
        val message: ReceiveMsg
) {

}