package com.seventh.demo.utils

import java.text.SimpleDateFormat

object TimeUtils {
    const val dateFormatYMD = "yyyy-MM-dd"

    /**
     * 描述：获取milliseconds表示的日期时间的字符串.
     *
     * @param milliseconds the milliseconds
     * @param format       格式化字符串，如："yyyy-MM-dd HH:mm:ss"
     * @return String 日期时间字符串
     */
    fun getStringByFormat(milliseconds: Long, format: String): String {
        var thisDateTime = ""
        try {
            val mSimpleDateFormat = SimpleDateFormat(format)
            thisDateTime = mSimpleDateFormat.format(milliseconds)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return thisDateTime
    }
}