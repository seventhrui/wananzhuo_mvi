package com.seventh.demo.network

import com.seventh.demo.BuildConfig
import com.seventh.demo.data.vo.AppVersionVO
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSession

interface Api {
    companion object {
        val BASE_URL = when(BuildConfig.IS_DEV) {
            true -> "https://www.wanandroid.com"
            false -> "https://www.wanandroid.com"
        }

        val service: Api by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            val okHttpClientBuilder = OkHttpClient.Builder()
                .hostnameVerifier(object : HostnameVerifier {
                    override fun verify(hostname: String?, session: SSLSession?): Boolean {
                        return true
                    }
                })
                .addInterceptor(HeaderInterceptor())
                .addInterceptor(LoggerInterceptor(BuildConfig.IS_DEBUG, BuildConfig.IS_DEBUG))
//                .proxy(Proxy(Proxy.Type.HTTP, InetSocketAddress("192.168.23.31", 8888)))

            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClientBuilder.build())
                .build()
                .create(Api::class.java)
        }
    }

//    @FormUrlEncoded
    @POST("/Index/checkUpdate")
    suspend fun checkAppUpdate(): BaseResponse<AppVersionVO>

    /**
     * 下载文件流IO
     */
    @Streaming
    @GET
    fun downloadFile(@Url fileUrl: String): Call<ResponseBody>

    /**
     * 注册
     * username
     * password
     * repassword
     */
    @FormUrlEncoded
    @POST("/user/register")
    fun registerUser(@FieldMap paramsMap: HashMap<String, Any>): BaseResponse<Any>

    /**
     * 登录
     * username
     * password
     */
    @FormUrlEncoded
    @POST("/user/login")
    suspend fun loginUser(@FieldMap paramsMap: HashMap<String, Any>): BaseResponse<Any>

    /**
     * 首页文章列表
     * page
     * page_size
     */
    @GET("/article/list/{page}/json")
    fun articleList(@Path("page") page: Int, @Query("page_size") pageSize: Int): BaseResponse<Any>

    /**
     * 首页banner
     */
    @GET("/banner/json")
    fun bannerList(): BaseResponse<Any>


}