package com.seventh.demo.network.interceptor

import com.orhanobut.logger.Logger
import com.seventh.demo.data.store.AppUserUtil
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class ReadCookiesInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder: Request.Builder = chain.request().newBuilder()

        if (SaveCookiesInterceptor.cookies.isNotEmpty()) {
            var cookie = AppUserUtil.userToken ?: ""
            builder.addHeader("Cookie", cookie)
            Logger.v("OkHttp Adding Header: $cookie")
        }
        return chain.proceed(builder.build())
    }
}