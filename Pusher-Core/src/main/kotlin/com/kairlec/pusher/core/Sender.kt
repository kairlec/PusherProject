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


@Suppress("SpellCheckingInspection")
internal object Sender {
    private val sslContext = SSLContext.getInstance("TLSv1.2")

    init {
        sslContext.init(null, arrayOf<TrustManager>(
                //关闭证书检查(相信不受信任的证书)
                object : X509TrustManager {
                    override fun getAcceptedIssuers(): Array<X509Certificate> {
                        return emptyArray()
                    }

                    override fun checkClientTrusted(certs: Array<X509Certificate?>?, authType: String?) {
                        //不做任何检查
                    }

                    override fun checkServerTrusted(certs: Array<X509Certificate?>?, authType: String?) {
                        //不做任何检查
                    }
                }
        ), null)
    }

    inline fun <reified R : Throwable> getExceptionConstructor(): KFunction<R> {
        return R::class.primaryConstructor
                ?: error("Cannot get class[${R::class.java.name}] primary constructor")
    }

    inline fun <reified T, reified R : Throwable> getResultMap(
            url: String,
            validateCertificateChains: Boolean,
            dataname: String? = null,
            headers: List<Pair<String, String>>? = null,
            warpNode: (JsonNode) -> JsonNode = { it }
    ): T {
        val exceptionConstructor = getExceptionConstructor<R>()
        var result = ""
        get(url, validateCertificateChains, headers)
                .whenComplete { httpResponse, throwable ->
                    if (throwable != null) {
                        throw exceptionConstructor.call(-1, throwable)
                    } else {
                        result = httpResponse.body()
                    }
                }.join()
        return resultMap(result, exceptionConstructor, dataname, warpNode)
    }

    inline fun <reified T, reified R : Throwable> postJsonResultMap(
            url: String,
            data: Any,
            validateCertificateChains: Boolean,
            dataname: String? = null,
            headers: List<Pair<String, String>>? = null,
            warpNode: (JsonNode) -> JsonNode = { it }
    ): T {
        val exceptionConstructor = getExceptionConstructor<R>()
        var result = ""
        postJson(url, data, validateCertificateChains, headers)
                .whenComplete { httpResponse, throwable ->
                    if (throwable != null) {
                        throw exceptionConstructor.call(-1, throwable)
                    } else {
                        result = httpResponse.body()
                    }
                }.join()
        return resultMap(result, exceptionConstructor, dataname, warpNode)
    }

    inline fun <reified T, reified R : Throwable> postFormResultMap(
            url: String,
            validateCertificateChains: Boolean,
            form: List<Pair<String, String>>? = null,
            dataname: String? = null,
            headers: List<Pair<String, String>>? = null,
            warpNode: (JsonNode) -> JsonNode = { it }
    ): T {
        val exceptionConstructor = getExceptionConstructor<R>()
        var result = ""
        postForm(url, validateCertificateChains, headers, form)
                .whenComplete { httpResponse, throwable ->
                    if (throwable != null) {
                        throw exceptionConstructor.call(-1, throwable)
                    } else {
                        result = httpResponse.body()
                    }
                }.join()
        return resultMap(result, exceptionConstructor, dataname, warpNode)
    }

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
        var result = ""
        upload(url, file, filename, validateCertificateChains, headers)
                .whenComplete { httpResponse, throwable ->
                    if (throwable != null) {
                        throw exceptionConstructor.call(-1, throwable)
                    } else {
                        result = httpResponse.body()
                    }
                }.join()
        return resultMap(result, exceptionConstructor, dataname, warpNode)
    }

    private inline fun <reified T, reified R : Throwable> resultMap(
            result: String,
            exceptionConstructor: KFunction<R>,
            dataname: String?,
            warpNode: (JsonNode) -> JsonNode
    ): T {
        val jsonNode: JsonNode
        try {
            jsonNode = objectMapper.readTree(result)
        } catch (e: Exception) {
            throw exceptionConstructor.call(-1, e)
        }
        if (jsonNode["errcode"]?.asInt() ?: 0 != 0) {
            throw exceptionConstructor.call(jsonNode["errcode"].asInt(), null, jsonNode["errmsg"].asText())
        }
        return when (T::class.java) {
            Unit::class.java -> Unit as T
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

    private val httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5000)).followRedirects(HttpClient.Redirect.NORMAL).build()
    private val httpClientNoValidateCertificateChains = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5000)).followRedirects(HttpClient.Redirect.NORMAL).sslContext(sslContext).build()

    private fun getHttpClient(validateCertificateChains: Boolean): HttpClient {
        return if (validateCertificateChains) {
            httpClientNoValidateCertificateChains
        } else {
            httpClient
        }
    }

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

    fun postJson(url: String, data: Any, validateCertificateChains: Boolean, headers: List<Pair<String, String>>? = null): CompletableFuture<HttpResponse<String>> {
        val httpClient = getHttpClient(validateCertificateChains)
        val jsonString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(data)
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