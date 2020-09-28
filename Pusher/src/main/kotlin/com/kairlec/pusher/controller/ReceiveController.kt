package com.kairlec.pusher.controller

import com.kairlec.pusher.config.properties.ReceiverProperties
import com.kairlec.pusher.receiver.ReceiveInterface
import com.kairlec.pusher.receiver.dsl.ReceiveDSL
import com.kairlec.pusher.receiver.msg.ReceiveMsg
import com.kairlec.pusher.util.ReplyReceiveMsgServiceImpl
import com.qq.weixin.mp.aes.WXBizMsgCrypt
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.web.bind.annotation.*

/**
 * 接受Controller
 */
@RestController
@RequestMapping(value = ["/receive"])
@ConditionalOnProperty(prefix = "wework.receiver", value = ["enabled"], matchIfMissing = true)
class ReceiveController {
    private val logger = LoggerFactory.getLogger(ReceiveController::class.java)

    @Autowired
    private lateinit var wxBizMsgCrypt: WXBizMsgCrypt

    //@Autowired
    //private lateinit var receiverMsgJavaFileHooker: ReceiverMsgJavaFileHooker

    @Autowired(required = false)
    private lateinit var receiveDSL:ReceiveDSL

    @Autowired(required = false)
    private lateinit var replyReceiveMsgService: ReplyReceiveMsgServiceImpl

    @Autowired
    private lateinit var receiverProperties: ReceiverProperties

    @Autowired
    private lateinit var receiver: ReceiveInterface

    /**
     * 接受信息接口
     * @param msgSignature
     * @param timestamp
     * @param nonce
     * @param postData
     * @return
     */
    @Suppress("SENSELESS_COMPARISON")
    @RequestMapping(value = [""], method = [RequestMethod.POST])
    fun receive(@RequestParam("msg_signature") msgSignature: String,
                @RequestParam("timestamp") timestamp: String,
                @RequestParam("nonce") nonce: String,
                @RequestBody(required = true) postData: String
    ): String {
        val rawMsg = wxBizMsgCrypt.DecryptMsg(msgSignature, timestamp, nonce, postData)
        val msg = ReceiveMsg.parse(rawMsg,replyReceiveMsgService) ?: return ""
        logger.info("收到来自[${msg.fromUserName}]的消息:${msg.contentToString()}")
        return if (replyReceiveMsgService != null && receiveDSL!=null) {
            receiveDSL.send(msg)
            ""
        } else {
            wxBizMsgCrypt.EncryptMsg(receiver.onReceive(msg).raw, "", nonce)
        }
        //return wxBizMsgCrypt.EncryptMsg(receiverMsgJavaFileHooker.hook(msg)
        //        ?: "[Error]Cannot invoke hook method and get hooked string", "", nonce)
    }

    /**
     * 验证URL接口
     */
    @RequestMapping(value = [""], method = [RequestMethod.GET])
    fun verify(@RequestParam("msg_signature") msgSignature: String,
               @RequestParam("timestamp") timestamp: String,
               @RequestParam("nonce") nonce: String,
               @RequestParam("echostr") echostr: String
    ): String {
        logger.info("verifying...")
        val result = wxBizMsgCrypt.VerifyURL(msgSignature, timestamp, nonce, echostr)
        logger.debug("result=${result}")
        return result
    }
}