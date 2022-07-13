package com.seventh.demo.data.store

import com.seventh.demo.data.vo.UserInfoVO
import com.seventh.demo.extension.fromJson
import com.seventh.demo.extension.toJson

object AppUserUtil {
    private const val LOGGED_FLAG = "logged_flag"
    private const val USER_INFO = "user_info"
    private const val USER_TOKEN = "user_token"


    var isLogged: Boolean
        get() = DataStoreUtils.readBooleanData(LOGGED_FLAG, false)
        set(value) = DataStoreUtils.saveSyncBooleanData(LOGGED_FLAG, value = value)

    var userInfo: UserInfoVO?
        get() = DataStoreUtils.readStringData(USER_INFO).fromJson()
        set(value) = DataStoreUtils.saveSyncStringData(USER_INFO, value = value?.toJson() ?: "")

    fun onLogin(userInfo: UserInfoVO?) {
        isLogged = true
        this.userInfo = userInfo
    }

    var userToken: String?
        get() = DataStoreUtils.readStringData(USER_TOKEN)
        set(value) = DataStoreUtils.saveSyncStringData(USER_TOKEN, value= value ?: "")

    fun onTolen(cookie: String) {
        this.userToken = cookie
    }

    fun onLogOut() {
        isLogged = false
        this.userInfo = null
    }
}