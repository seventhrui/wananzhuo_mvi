package com.seventh.demo.network

import com.seventh.demo.BuildConfig
import com.seventh.demo.data.vo.*
import com.seventh.demo.network.interceptor.*
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSession

interface Api {
    companion object {
        private val BASE_URL = when(BuildConfig.IS_DEV) {
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
                .addInterceptor(ReadCookiesInterceptor())
                .addInterceptor(SaveCookiesInterceptor())
                .addInterceptor(HeaderInterceptor())
                .addInterceptor(DomainSwitchInterceptor())
                .addInterceptor(LoggerInterceptor(BuildConfig.IS_DEBUG, BuildConfig.IS_DEBUG))

            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClientBuilder.build())
                .build()
                .create(Api::class.java)
        }

        val downloadService: Api by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            val okHttpClientBuilder = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(DomainSwitchInterceptor())
                .hostnameVerifier { hostname, session -> true }

            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClientBuilder.build())
                .build()
                .create(Api::class.java)
        }
    }

    @Headers("domain:https://www.xxx.com")
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
    suspend fun loginUser(@FieldMap paramsMap: HashMap<String, Any>): BaseResponse<UserInfoVO>

    /**
     * 首页banner
     */
    @GET("/banner/json")
    suspend fun bannerList(): BaseResponse<ArrayList<BannerVo>>

    /**
     * 首页文章列表
     * page
     * page_size
     */
    @GET("/article/list/{page}/json")
    suspend fun articleList(@Path("page") page: Int, @Query("page_size") pageSize: Int): BaseResponse<ArticleListVO>

    /**
     * 分类
     */
    @GET("/tree/json")
    suspend fun courseList(): BaseResponse<ArrayList<CourseGroupVO>>

    /**
     * 项目列表
     */
    @GET("/project/list/{page}/json")
    suspend fun projectList(@Path("page") page: Int, @Query("page_size") pageSize: Int, @Query("cid") cid: Int): BaseResponse<ProjectListVo>

    /**
     * 收藏列表
     */
    @GET("/lg/collect/list/{page}/json")
    suspend fun collectList(@Path("page") page: Int, @Query("page_size") pageSize: Int): BaseResponse<ArticleListVO>
}