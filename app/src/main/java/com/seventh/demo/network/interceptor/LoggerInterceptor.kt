package com.seventh.demo.network.interceptor

import android.text.TextUtils
import com.orhanobut.logger.Logger
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import java.io.IOException

class LoggerInterceptor(private val showRquestBody: Boolean, private val showResponseBody: Boolean): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        logForRequest(request)
        val response = chain.proceed(request)
        return logForResponse(response)
    }

    private fun logForResponse(response: Response): Response {
        try {
            val builder = response.newBuilder()
            val clone = builder.build()
            if (showResponseBody) {
                val printBuilder = StringBuilder()
                printBuilder.append("response => ")
                printBuilder.append("[url = ${clone.request.url}]")
                printBuilder.append("[code = ${clone.code}]")
                printBuilder.append("[protocol = ${clone.protocol}]")
                if (!TextUtils.isEmpty(clone.message)) {
                    printBuilder.append("[message = ${clone.message}]")
                }
                var body = clone.body
                Logger.e("长度：${body?.contentLength()}")
                if (body != null && body?.contentLength()<1024*1024*10) {
                    val mediaType = body.contentType()
                    if (mediaType != null) {
                        printBuilder.append("[mediaType = $mediaType]")
                        val resp = body.string()
                        printBuilder.append("\n $resp")
                        Logger.d(printBuilder.toString())
                        body = resp.toResponseBody(mediaType)
                        return response.newBuilder().body(body).build()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return response
    }

    private fun logForRequest(request: Request) {
        try {
            val url = request.url.toString()
            val headers = request.headers
            if (showRquestBody) {
                val requestBody = request.body
                if (requestBody != null) {
                    val mediaType = requestBody.contentType()
                    if (mediaType != null) {
                        Logger.d("request => ${url}?${bodyToString(request)}" +
                                "[method = ${request.method}][url = $url][headers = $headers][mediaType = $mediaType]")
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun bodyToString(request: Request): String {
        return try {
            val copy = request.newBuilder().build()
            val buffer = Buffer()
            copy.body?.writeTo(buffer)
            buffer.readUtf8()
        } catch (e: IOException) {
            "something error when show requestBody"
        }
    }
}