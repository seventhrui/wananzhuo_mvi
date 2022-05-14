package com.seventh.demo

import android.app.Application
import android.view.Gravity
import com.hjq.toast.ToastUtils
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy

class MyApp: Application() {
    companion object {
        lateinit var instant: MyApp

        fun get(): MyApp {
            return instant
        }
    }

    override fun onCreate() {
        super.onCreate()
        instant = this

        initLogger()
        initToast()
    }

    /**
     * logger重打印
     */
    private fun initLogger() {
        val formatStrategy = PrettyFormatStrategy.newBuilder()
            .showThreadInfo(false)
            .methodCount(0)
            .methodOffset(3)
            .tag(BuildConfig.APP_TAG)
            .build()
        Logger.addLogAdapter(object : AndroidLogAdapter(formatStrategy) {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return BuildConfig.IS_DEBUG
            }
        })
    }

    private fun initToast() {
        ToastUtils.init(this)
        ToastUtils.setDebugMode(BuildConfig.IS_DEBUG)
        ToastUtils.setView(R.layout.layout_toast_custom_view)
        ToastUtils.setGravity(Gravity.CENTER, 0, 0)
    }
}