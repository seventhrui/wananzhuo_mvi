package com.seventh.demo.extension

import android.content.Context

/**
 * 返回int类型px值，直接把小数删除
 */
fun Context.dimenPixelOffset(redId: Int): Int {
    return this.resources.getDimensionPixelOffset(redId)
}

fun Context.px2dp(pxValue: Float): Int {
    val scale = this.resources.displayMetrics.density
    return (pxValue / scale + 0.5f).toInt()
}

fun Context.dp2px(dpValue: Float): Int {
    val scale = this.resources.displayMetrics.density
    return (dpValue * scale + 0.5f).toInt()
}

fun Context.strById(resId: Int, vararg args: Any): String {
    return this.resources.getString(resId, *args)
}

fun Context.colorById(resId: Int): Int {
    return this.resources.getColor(resId)
}