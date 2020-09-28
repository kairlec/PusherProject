package com.kairlec.pojo

//import org.w3c.dom.Element
//import org.xml.sax.InputSource
//import java.io.StringReader
//import javax.xml.parsers.DocumentBuilderFactory
//
//
//data class ReceiveMsg(
//        val agentID: Long,
//        val content: String,
//        val createTime: Long,
//        val fromUserName: String,
//        val msgId: Long,
//        val msgType: String,
//        val toUserName: String
//) {
//    companion object {
//        operator fun Element?.get(name: String): String? {
//            return this?.getElementsByTagName(name)?.item(0)?.textContent
//        }
//
//        fun parse(rawString: String): ReceiveMsg? {
//            val documentBuilderFactory = DocumentBuilderFactory.newInstance()
//            val documentBuilder = documentBuilderFactory.newDocumentBuilder()
//            StringReader(rawString).use {
//                val inputSource = InputSource(it)
//                val document = documentBuilder.parse(inputSource)
//                val root = document.documentElement
//                val content = root["Content"]?:return null
//                val toUsername = root["ToUserName"]?:return null
//                val fromUserName = root["FromUserName"]?:return null
//                val createTime = root["CreateTime"]?.toLong()?:return null
//                val msgType = root["MsgType"]?:return null
//                val msgId = root["MsgId"]?.toLong()?:return null
//                val agentID = root["AgentID"]?.toLong()?:return null
//                return ReceiveMsg(agentID, content, createTime, fromUserName, msgId, msgType, toUsername)
//            }
//        }
//    }
//}