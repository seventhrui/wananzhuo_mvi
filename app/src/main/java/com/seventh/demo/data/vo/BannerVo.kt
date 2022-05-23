package com.seventh.demo.data.vo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BannerVo(
    val id: Int,
    val desc: String,
    val imagePath: String,
    val isVisible: Int,
    val title: String,
    val type: Int,
    val url: String
): Parcelable
