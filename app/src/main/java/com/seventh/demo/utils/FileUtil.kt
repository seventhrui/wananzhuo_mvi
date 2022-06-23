package com.seventh.demo.utils

import com.seventh.demo.context.ContextProvider

object FileUtil {
    val FileCachePath: String = "${ContextProvider.mAppContext.getExternalFilesDir(null)?.absolutePath}"
}