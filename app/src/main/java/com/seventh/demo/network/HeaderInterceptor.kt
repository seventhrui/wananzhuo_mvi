package com.seventh.demo.network

import com.seventh.demo.core.showToast
import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import okio.GzipSource
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

/**
 * 请求头拦截器
 * 处理公共请求头
 */
class HeaderInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val method: String = original.method
        val requestBuilder = original.newBuilder()
            .removeHeader("User-Agent")
            .header("User-Agent", "android")
            .header("app_channel", "offical")
            .header("app_version_code", "546")
            .header("app_version_name", "5.7.7.2")

        if (original.body is FormBody) {
            val newFormBody = FormBody.Builder()
            val oldFormBody = original.body as FormBody?
            oldFormBody?.let {
                for (index in 0 until it.size) {
                    newFormBody.addEncoded(it.encodedName(index), it.encodedValue(index).trim())
                }
            }
            newFormBody.add("timetemp", "${System.currentTimeMillis()/1000}")
            requestBuilder.method(original.method, newFormBody.build())
        } else {
            val commonParamsUrlBuilder = original.url
                .newBuilder()
                .scheme(original.url.scheme)
                .host(original.url.host)
                .addQueryParameter("timetemp", "${System.currentTimeMillis()/1000}")
            requestBuilder
                .method(original.method, original.body)
                .url(commonParamsUrlBuilder.build())
        }
        val request = requestBuilder.build()
        val response = chain.proceed(request)

        val responseBody = response.body
        if (responseBody != null) {
            val contentLength = responseBody.contentLength()
            val source = responseBody.source()
            source.request(Long.MAX_VALUE)
            var buffer = source.buffer

            if ("gzip" == response.headers["Content-Encoding"]) {
                val gzippedResponseBody = GzipSource(buffer.clone())
                buffer = Buffer()
                buffer.writeAll(gzippedResponseBody)
            }

            val contentType = responseBody.contentType()
            val charset: Charset? = if (contentType?.charset(StandardCharsets.UTF_8) == null) {
                StandardCharsets.UTF_8
            } else {
                contentType.charset(StandardCharsets.UTF_8)
            }

            if (charset != null && contentLength != 0L) {
                return intercept(chain, response)
            }
        }
        return response
    }

    private fun intercept(chain: Interceptor.Chain, response: Response): Response {
        if (response.code != 200) {
            "服务器开小差了(${response.code})，请稍后再试".showToast()
            //构造一个假返回体
            return Response.Builder()
                .code(200)
                .protocol(Protocol.HTTP_2)
                .message("NetConnectionException")
                .body(
                    "{\"status\":\"0\",\"message\":\"${chain.request().url} => APP HandleAPIErrorInterceptor服务器${response.code}\"}"
                        .toResponseBody("text/html; charset=utf-8".toMediaTypeOrNull())
                )
                .request(chain.request())
                .build()
        }
        return response
    }
}