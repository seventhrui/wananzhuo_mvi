package com.seventh.demo.network

import com.google.gson.annotations.SerializedName

data class BaseResponse<T>(
    @SerializedName("errorCode") var status: Int = 0,
    @SerializedName("errorMsg") var message: String = "",
    @SerializedName("data") var data: T? = null
)
