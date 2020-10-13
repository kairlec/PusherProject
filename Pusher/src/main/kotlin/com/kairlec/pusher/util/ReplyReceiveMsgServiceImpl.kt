package com.kairlec.pusher.util

import com.kairlec.pojo.wework.MediaTypeEnum
import com.kairlec.pusher.annotation.condition.ReplyReceiveMsgCondition
import com.kairlec.pusher.core.wework.WeWorkSenderHelper
import com.kairlec.pusher.receiver.reply.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Conditional
import org.springframework.stereotype.Component

@Component
@Conditional(ReplyReceiveMsgCondition::class)
class ReplyReceiveMsgServiceImpl : ReplyService {

    private val logger = LoggerFactory.getLogger(ReplyReceiveMsgServiceImpl::class.java)

    @Autowired
    private lateinit var workSenderHelper: WeWorkSenderHelper

    override fun reply(message: Message) {
        logger.info("reply to server:${message}")
        if(logger.isDebugEnabled) {
            when (message) {
                is TextMessage -> {
                    logger.debug("reply text[${message.content}] to [${message.toUser}]")
                }
                is FileMessage -> {
                    logger.debug("reply file[${message.filename}] to [${message.toUser}]")
                }
                is ImageMessage -> {
                    logger.debug("reply image[${message.filename}] to [${message.toUser}]")
                }
                is VideoMessage -> {
                    logger.debug("reply video[${message.filename}] to [${message.toUser}]")
                }
                is VoiceMessage -> {
                    logger.debug("reply voice[${message.filename}] to [${message.toUser}]")
                }
                is MarkdownMessage -> {
                    logger.debug("reply markdown[${message.content}] to [${message.toUser}]")
                }
                is MpNewsMessage -> {
                    logger.debug("reply mpnews[${message.title}] to [${message.toUser}]")
                }
                is NewsMessage -> {
                    logger.debug("reply news[${message.title}] to [${message.toUser}]")
                }
                is TextCardMessage -> {
                    logger.debug("reply textcard[${message.title}] to [${message.toUser}]")
                }
            }
        }
        when (message) {
            is TextMessage -> {
                workSenderHelper.sendText(message.content, workSenderHelper.withSettings.toUser(message.toUser))
            }
            is FileMessage -> {
                val mediaID = workSenderHelper.uploadMedia(message.fileByteArray, message.filename, MediaTypeEnum.FILE)
                workSenderHelper.sendFile(mediaID.mediaID, workSenderHelper.withSettings.toUser(message.toUser))
            }
            is ImageMessage -> {
                val mediaID = workSenderHelper.uploadMedia(message.imageByteArray, message.filename, MediaTypeEnum.IMAGE)
                workSenderHelper.sendFile(mediaID.mediaID, workSenderHelper.withSettings.toUser(message.toUser))
            }
            is VideoMessage -> {
                val mediaID = workSenderHelper.uploadMedia(message.videoByteArray, message.filename, MediaTypeEnum.VIDEO)
                workSenderHelper.sendFile(mediaID.mediaID, workSenderHelper.withSettings.toUser(message.toUser))
            }
            is VoiceMessage -> {
                val mediaID = workSenderHelper.uploadMedia(message.voiceByteArray, message.filename, MediaTypeEnum.VOICE)
                workSenderHelper.sendFile(mediaID.mediaID, workSenderHelper.withSettings.toUser(message.toUser))
            }
            is MarkdownMessage -> {
                workSenderHelper.sendMarkdown(message.content, workSenderHelper.withSettings.toUser(message.toUser))
            }
            is MpNewsMessage -> {
                workSenderHelper.sendMpNews(message.title, message.thumbMediaID, message.content, message.author, message.contentSourceUrl, message.digest, workSenderHelper.withSettings.toUser(message.toUser))
            }
            is NewsMessage -> {
                workSenderHelper.sendNews(message.title, message.url, message.description, message.picurl, workSenderHelper.withSettings.toUser(message.toUser))
            }
            is TextCardMessage -> {
                workSenderHelper.sendTextCard(message.title, message.description, message.url, message.btntxt, workSenderHelper.withSettings.toUser(message.toUser))
            }
        }
    }

}