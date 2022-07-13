package com.seventh.demo.network.interceptor

import com.orhanobut.logger.Logger
import com.seventh.demo.data.store.AppUserUtil
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class SaveCookiesInterceptor: Interceptor {
    companion object {
        var cookies: List<String> = ArrayList()
        var response: Response? = null
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        response = chain.proceed(request)
        cookies = response!!.headers("Set-Cookie")
        Logger.e("cookie:${cookies}")
        if (cookies.size>1)
            AppUserUtil.onTolen(encodeCookie(cookies))
        return response!!.newBuilder()
            .build()
    }

    private fun encodeCookie(cookies: List<String>): String {
        val sb = StringBuilder()
        val set: MutableSet<String> = HashSet()
        for (cookie in cookies) {
            val arr = cookie.split(";").toTypedArray()
            for (s in arr) {
                set.add(s)
            }
        }
        for (cookie in set) {
            sb.append(cookie).append(";")
        }

        return sb.toString()
    }
}