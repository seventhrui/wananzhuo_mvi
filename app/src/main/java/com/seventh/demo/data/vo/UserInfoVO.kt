package com.seventh.demo.data.vo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserInfoVO(
    val admin: Boolean,
    val coinCount: Int,
    val email: String,
    val icon: String,
    val id: Int,
    val nickname: String,
    val publicName: String,
    val type: Int,
    val username: String
): Parcelable
