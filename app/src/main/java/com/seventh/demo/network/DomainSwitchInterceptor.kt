package com.seventh.demo.network

import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Request
import okhttp3.Response

/**
 * 请求头拦截器
 * 切换域名
 */
class DomainSwitchInterceptor: Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(buildRequest(chain))
    }

    private fun buildRequest(chain: Chain): Request {
        var request = chain.request()
        val httpUrl = request.url
        val domain = request.header("domain")
        domain?.let {
            val domainUrl = domain.toHttpUrl()
            val newUrl = httpUrl.newBuilder().scheme(domainUrl.scheme).host(domainUrl.host).port(domainUrl.port).build()
            val builder = request.newBuilder()
            builder.url(newUrl)
            request = builder.build()
        }
        return request
    }
}