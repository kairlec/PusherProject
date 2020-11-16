package com.kairlec.pusher.core

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.convertValue
import com.kairlec.utils.urlEncode
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.security.cert.X509Certificate
import java.time.Duration
import java.util.*
import java.util.concurrent.CompletableFuture
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import javax.swing.tree.TreeNode
import kotlin.reflect.KFunction
import kotlin.reflect.full.primaryConstructor

/**
 * 发送实现
 */
@Suppress("SpellCheckingInspection")
object Sender {

    /**
     * 获取异常构造函数
     * @return 异常构造函数
     */
    inline fun <reified R : Throwable> getExceptionConstructor(): KFunction<R> {
        return R::class.primaryConstructor
                ?: error("Cannot get class[${R::class.java.name}] primary constructor")
    }

    /**
     * 发送get请求并获取json结果映射到指定类
     * @param url 要请求的地址
     * @param validateCertificateChains 是否验证证书
     * @param dataname 返回的json的包装字段,若传入null,则为根
     * @param headers HTTP Header
     * @param warpNode 指示在处理映射前该如何包装JsonNode
     * @return 映射后得到的类
     * @see resultMap
     */
    inline fun <reified T, reified R : Throwable> getResultMap(
            url: String,
            validateCertificateChains: Boolean,
            dataname: String? = null,
            headers: List<Pair<String, String>>? = null,
            warpNode: (JsonNode) -> JsonNode = { it }
    ): T {
        val exceptionConstructor = getExceptionConstructor<R>()
        val result = get(url, validateCertificateChains, headers)
                .whenComplete { _, throwable ->
                    if (throwable != null) {
                        throw exceptionConstructor.call(-1, throwable)
                    }
                }.join().body()
        return resultMap(result, exceptionConstructor, dataname, warpNode)
    }

    /**
     * 发送post请求并获取json结果映射到指定类
     * @param url 要请求的地址
     * @param data 传入的类,将自动转换为json
     * @param validateCertificateChains 是否验证证书
     * @param dataname 返回的json的包装字段,若传入null,则为根
     * @param headers HTTP Header
     * @param warpNode 指示在处理映射前该如何包装JsonNode
     * @return 映射后得到的类
     * @see resultMap
     */
    inline fun <reified T, reified R : Throwable> postJsonResultMap(
            url: String,
            data: Any,
            validateCertificateChains: Boolean,
            dataname: String? = null,
            headers: List<Pair<String, String>>? = null,
            warpNode: (JsonNode) -> JsonNode = { it }
    ): T {
        val exceptionConstructor = getExceptionConstructor<R>()
        val result = postJson(url, data, validateCertificateChains, headers)
                .whenComplete { _, throwable ->
                    if (throwable != null) {
                        throw exceptionConstructor.call(-1, throwable)
                    }
                }.join().body()
        return resultMap(result, exceptionConstructor, dataname, warpNode)
    }

    /**
     * 发送post请求并获取json结果映射到指定类
     * @param url 要请求的地址
     * @param validateCertificateChains 是否验证证书
     * @param form 传入的form表单
     * @param dataname 返回的json的包装字段,若传入null,则为根
     * @param headers HTTP Header
     * @param warpNode 指示在处理映射前该如何包装JsonNode
     * @return 映射后得到的类
     * @see resultMap
     */
    inline fun <reified T, reified R : Throwable> postFormResultMap(
            url: String,
            validateCertificateChains: Boolean,
            form: List<Pair<String, String>>? = null,
            dataname: String? = null,
            headers: List<Pair<String, String>>? = null,
            warpNode: (JsonNode) -> JsonNode = { it }
    ): T {
        val exceptionConstructor = getExceptionConstructor<R>()
        val result = postForm(url, validateCertificateChains, headers, form)
                .whenComplete { _, throwable ->
                    if (throwable != null) {
                        throw exceptionConstructor.call(-1, throwable)
                    }
                }.join().body()
        return resultMap(result, exceptionConstructor, dataname, warpNode)
    }

    /**
     * 发送post请求并获取json结果映射到指定类
     * @param url 要请求的地址
     * @param file 文件内容
     * @param filename 文件名
     * @param validateCertificateChains 是否验证证书
     * @param dataname 返回的json的包装字段,若传入null,则为根
     * @param headers HTTP Header
     * @param warpNode 指示在处理映射前该如何包装JsonNode
     * @return 映射后得到的类
     * @see resultMap
     */
    inline fun <reified T, reified R : Throwable> uploadResultMap(
            url: String,
            file: ByteArray,
            filename: String,
            validateCertificateChains: Boolean,
            dataname: String? = null,
            headers: List<Pair<String, String>>? = null,
            warpNode: (JsonNode) -> JsonNode = { it }
    ): T {
        val exceptionConstructor = getExceptionConstructor<R>()
        val result = upload(url, file, filename, validateCertificateChains, headers)
                .whenComplete { _, throwable ->
                    if (throwable != null) {
                        throw exceptionConstructor.call(-1, throwable)
                    }
                }.join().body()
        return resultMap(result, exceptionConstructor, dataname, warpNode)
    }

    /**
     * 将请求后返回的json内容处理得到指定类
     * @param result 请求返回内容
     * @param exceptionConstructor 异常构造函数
     * @param dataname 返回的json的包装字段,若传入null,则为根
     * @param warpNode 指示在处理映射前该如何包装JsonNode
     * @return 映射后得到的类
     * @see resultMap
     */
    inline fun <reified T, reified R : Throwable> resultMap(
            result: String,
            exceptionConstructor: KFunction<R>,
            dataname: String?,
            warpNode: (JsonNode) -> JsonNode
    ): T {
        val jsonNode = try {
            objectMapper.readTree(result)
        } catch (e: Exception) {
            throw exceptionConstructor.call(-1, e)
        }
        if (jsonNode["errcode"]?.asInt() ?: 0 != 0) {
            throw exceptionConstructor.call(jsonNode["errcode"].asInt(), null, jsonNode["errmsg"].asText())
        }
        return when (T::class.java) {
            //若传入Unit,则不需要对结果进行任何处理,返回Unit即可
            Unit::class.java -> Unit as T
            //若传入JsonNode或者TreeNode,则直接返回原始Node,不经过处理
            JsonNode::class.java, TreeNode::class.java -> jsonNode as? T
                    ?: throw exceptionConstructor.call(-1, null, "Cannot cast type[${jsonNode::class.java.name}] to type[${T::class.java.name}] with value `$jsonNode`")
            else -> {
                val node = if (dataname == null) jsonNode else jsonNode[dataname]
                val warpedNode = warpNode(node)
                return try {
                    objectMapper.convertValue<T>(warpedNode)
                            ?: throw exceptionConstructor.call(-1, null, "Cannot tree to value type[${warpedNode::class.java.name}] to type[${T::class.java.name}] with value `$warpedNode`")
                } catch (e: Exception) {
                    throw exceptionConstructor.call(-1, e, "Cannot tree to value type[${warpedNode::class.java.name}] to type[${T::class.java.name}] with value `$warpedNode`")
                }
            }
        }
    }

    var protocol = "TLSv1.2"

    val version = HttpClient.Version.HTTP_1_1

    private val sslContext = SSLContext.getInstance(protocol).apply {
        init(null, arrayOf<TrustManager>(
                object : X509TrustManager {
                    override fun getAcceptedIssuers(): Array<X509Certificate> {
                        return emptyArray()
                    }

                    override fun checkClientTrusted(certs: Array<X509Certificate?>?, authType: String?) {
                    }

                    override fun checkServerTrusted(certs: Array<X509Certificate?>?, authType: String?) {
                    }
                }
        ), null)
    }

    private val httpClient by lazy {
        HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5000)).followRedirects(HttpClient.Redirect.NORMAL).version(version).build()
    }
    private val httpClientNoValidateCertificateChains by lazy {
        HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5000)).followRedirects(HttpClient.Redirect.NORMAL).version(version).sslContext(sslContext).build()
    }

    private fun getHttpClient(validateCertificateChains: Boolean): HttpClient {
        return if (validateCertificateChains) {
            httpClientNoValidateCertificateChains
        } else {
            httpClient
        }
    }

    /**
     * 发送带有Form表单的post请求
     * @param url 请求地址
     * @param validateCertificateChains 是否取消证书校验
     * @param headers HTTP Header
     * @param form Form表单
     */
    fun postForm(url: String, validateCertificateChains: Boolean, headers: List<Pair<String, String>>? = null, form: List<Pair<String, String>>? = null): CompletableFuture<HttpResponse<String>> {
        val httpClient = getHttpClient(validateCertificateChains)
        val formString = form?.let {
            StringBuilder().apply {
                form.forEachIndexed { index, (name, value) ->
                    append(if (index == 0) "" else "&")
                    append(name, "=", value.urlEncode())
                }
            }.toString()
        }
        val httpBodyPublisher = HttpRequest.BodyPublishers.ofString(formString ?: "")
        val httpRequestBuilder = HttpRequest.newBuilder()
                .uri(URI(url))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(httpBodyPublisher)
        headers?.let {
            for (header in it) {
                httpRequestBuilder.header(header.first, header.second)
            }
        }
        val httpRequest = httpRequestBuilder.build()
        return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
    }

    /**
     * 发送post请求
     * @param url 请求地址
     * @param data 请求体,将自动转换为Json
     * @param validateCertificateChains 是否取消证书校验
     * @param headers HTTP Header
     */
    fun postJson(url: String, data: Any, validateCertificateChains: Boolean, headers: List<Pair<String, String>>? = null): CompletableFuture<HttpResponse<String>> {
        val httpClient = getHttpClient(validateCertificateChains)
        val jsonString = objectMapper.writeValueAsString(data)
        val httpBodyPublisher = HttpRequest.BodyPublishers.ofString(jsonString)
        val httpRequestBuilder = HttpRequest.newBuilder()
                .uri(URI(url))
                .header("Content-Type", "application/json")
                .POST(httpBodyPublisher)
        headers?.let {
            for (header in it) {
                httpRequestBuilder.header(header.first, header.second)
            }
        }
        val httpRequest = httpRequestBuilder.build()
        return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
    }

    /**
     * 发送get请求
     * @param url 请求地址
     * @param validateCertificateChains 是否取消证书校验
     * @param headers HTTP Header
     */
    fun get(url: String, validateCertificateChains: Boolean, headers: List<Pair<String, String>>? = null): CompletableFuture<HttpResponse<String>> {
        val httpClient = getHttpClient(validateCertificateChains)
        val httpRequestBuilder = HttpRequest.newBuilder()
                .uri(URI(url))
                .header("Content-Type", "application/json")
                .GET()
        headers?.let {
            for (header in it) {
                httpRequestBuilder.header(header.first, header.second)
            }
        }
        val httpRequest = httpRequestBuilder.build()
        return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
    }

    /**
     * 上传文件(Post)
     * @param url 请求地址
     * @param file 文件内容
     * @param filename 文件名
     * @param validateCertificateChains 是否取消证书校验
     * @param headers HTTP Header
     */
    fun upload(url: String, file: ByteArray, filename: String, validateCertificateChains: Boolean, headers: List<Pair<String, String>>? = null): CompletableFuture<HttpResponse<String>> {
        val boundary = UUID.randomUUID()
        val first = "--${boundary}\r\nContent-Disposition: form-data; name=\"media\"; filename=\"$filename\"; filelength=${file.size}\r\nContent-Type: application/octet-stream\r\n\r\n"
        val bodyByteArray = first.toByteArray(Charsets.UTF_8) + file + "\r\n--${boundary}--\r\n".toByteArray(Charsets.UTF_8)
        val httpClient = getHttpClient(validateCertificateChains)
        val httpBodyPublisher = HttpRequest.BodyPublishers.ofByteArray(bodyByteArray)
        val httpRequestBuilder = HttpRequest.newBuilder()
                .uri(URI(url))
                .header("Content-Type", "multipart/form-data; boundary=${boundary}")
                .POST(httpBodyPublisher)
        headers?.let {
            for (header in it) {
                httpRequestBuilder.header(header.first, header.second)
            }
        }
        val httpRequest = httpRequestBuilder.build()
        return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
    }
}