package com.seventh.demo.extension

import android.content.Context

/**
 * 返回int类型px值，直接把小数删除
 */
fun Context.dimenPixelOffset(redId: Int): Int {
    return this.resources.getDimensionPixelOffset(redId)
}

fun Context.strById(resId: Int, vararg args: Any): String {
    return this.resources.getString(resId, *args)
}

fun Context.colorById(resId: Int): Int {
    return this.resources.getColor(resId)
}