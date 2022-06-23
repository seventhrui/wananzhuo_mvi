package com.seventh.demo.context

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Process
import androidx.core.content.FileProvider

class ContextProvider: FileProvider() {
    companion object {
        lateinit var mAppContext: Application
        val activityTracker = ActivityTracker()

        fun mCurrentActivity(): Activity? = activityTracker.tryGetCurrentActivity()

        fun closeAppAllPages() = activityTracker.closeApp()

        fun getProcessName(context: Context): String? {
            val pid = Process.myPid()
            var processName: String? = null
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            if(activityManager.runningAppProcesses == null) return null
            val iterator: Iterator<ActivityManager.RunningAppProcessInfo> = activityManager.runningAppProcesses.iterator()
            while (iterator.hasNext()) {
                val info = iterator.next()
                if (info.pid == pid) {
                    processName = info.processName
                    break
                }
            }
            return processName
        }

    }

    override fun onCreate(): Boolean {
        mAppContext = context?.applicationContext as Application
        activityTracker.beginTracking(mAppContext)
        return super.onCreate()
    }
}