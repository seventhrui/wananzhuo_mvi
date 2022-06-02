package com.seventh.demo

import android.app.Application
import android.view.Gravity
import com.hjq.toast.ToastUtils
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import com.seventh.demo.data.store.DataStoreUtils
import com.tencent.smtt.export.external.TbsCoreSettings
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.QbSdk.PreInitCallback

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

        initDataStore()
        initLogger()
        initToast()
        initX5WebView()
    }

    private fun initDataStore() {
        DataStoreUtils.init(this)
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

    private fun initX5WebView() {
        QbSdk.initX5Environment(this, object: PreInitCallback{
            override fun onCoreInitFinished() {
            }

            override fun onViewInitFinished(p0: Boolean) {

            }
        })
        val map = HashMap<String, Any>()
        map[TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER] = true
        map[TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE] = true
        QbSdk.initTbsSettings(map)
    }
}
