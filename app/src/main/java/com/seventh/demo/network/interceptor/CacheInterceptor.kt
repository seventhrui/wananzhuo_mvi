package com.seventh.demo.network.interceptor

import com.orhanobut.logger.Logger
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class CacheInterceptor: Interceptor {
    companion object {
        var cookies: List<String> = ArrayList()
        var response: Response? = null
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        response = chain.proceed(request)
        val onlineCacheTime = 60
        if (response!!.headers("Set-Cookie") != null) {
            cookies = response!!.headers("Set-Cookie")
        }
        Logger.e("cookie:${cookies}")
        return response!!.newBuilder()
            .header("Cache-Control", "public, max-age=$onlineCacheTime")
            .removeHeader("Pragma")
            .build()
    }

}