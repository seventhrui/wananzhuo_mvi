package com.seventh.demo.data.vo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TagVO(
    val name: String,
    val url: String
): Parcelable
