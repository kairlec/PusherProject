package com.kairlec.pusher.controller

import com.kairlec.pusher.openapi.context.MessageContext
import com.kairlec.pusher.pojo.Plugin
import com.kairlec.pusher.receiver.msg.ReceiveMsg
import com.kairlec.pusher.util.ReplyReceiveMsgServiceImpl
import com.qq.weixin.mp.aes.WXBizMsgCrypt
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

/**
 * 接受Controller
 */
@RestController
@RequestMapping(value = ["/receive"])
class ReceiverController {
    private val logger = LoggerFactory.getLogger(ReceiverController::class.java)

    @Autowired
    private lateinit var wxBizMsgCrypt: WXBizMsgCrypt

    @Autowired
    private lateinit var replyReceiveMsgService: ReplyReceiveMsgServiceImpl

    /**
     * 接受信息接口
     * @param msgSignature
     * @param timestamp
     * @param nonce
     * @param postData
     * @return 返回的消息原生内容
     */
    @Suppress("SENSELESS_COMPARISON")
    @RequestMapping(value = ["/"], method = [RequestMethod.POST])
    fun receive(@RequestParam("msg_signature") msgSignature: String,
                @RequestParam("timestamp") timestamp: String,
                @RequestParam("nonce") nonce: String,
                @RequestBody(required = true) postData: String
    ): String {
        val rawMsg = wxBizMsgCrypt.DecryptMsg(msgSignature, timestamp, nonce, postData)
        val msg = ReceiveMsg.parse(rawMsg, replyReceiveMsgService) ?: return ""
        logger.info("Received message from [${msg.fromUserName}] :${msg.contentToString()}")
        val result = Plugin.Invoker.onReceiveMessage(MessageContext(msg))
        return if (result == null) {
            ""
        } else {
            wxBizMsgCrypt.EncryptMsg(result.raw, "", nonce)
        }
    }

    /**
     * 验证URL接口
     */
    @RequestMapping(value = ["/"], method = [RequestMethod.GET])
    fun verify(@RequestParam("msg_signature") msgSignature: String,
               @RequestParam("timestamp") timestamp: String,
               @RequestParam("nonce") nonce: String,
               @RequestParam("echostr") echostr: String
    ): String {
        logger.info("verifying...")
        val result = wxBizMsgCrypt.VerifyURL(msgSignature, timestamp, nonce, echostr)
        if (logger.isDebugEnabled) {
            logger.debug("result=${result}")
        }
        return result
    }
}