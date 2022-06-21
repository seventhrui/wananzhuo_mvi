package com.seventh.demo.extension

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import coil.imageLoader
import coil.request.ImageRequest

suspend fun Context.getImageBitmapByUrl(url: String): Bitmap? {
    val request = ImageRequest.Builder(this)
        .data(url)
        .allowHardware(false)
        .build()
    val result = imageLoader.execute(request)
    val drawable = result.drawable
    drawable?.let { return (drawable as BitmapDrawable).bitmap }
    return null
}