package com.seventh.demo.context

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Bundle
import java.lang.Exception
import java.lang.ref.WeakReference
import java.util.*

class ActivityTracker {
    private val activityRefs = Stack<WeakReference<Activity>>()
    private val lifecycleCallbacks: ActivityLifecycleCallbacksAdapter =
        object : ActivityLifecycleCallbacksAdapter() {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                add(activity)
            }

            override fun onActivityDestroyed(activity: Activity) {
                remove(activity)
            }
        }

    fun beginTracking(application: Application) {
        application.registerActivityLifecycleCallbacks(lifecycleCallbacks)
    }

    fun endTracking(application: Application) {
        application.unregisterActivityLifecycleCallbacks(lifecycleCallbacks)
    }

    fun tryGetCurrentActivity(): Activity? {
        return try {
            activityRefs.peek().get()
        } catch (ex: Exception) {
            null
        }
    }

    fun finishActivityByCls(clazz: Class<*>?) {
        if (clazz == null) {
            return
        }
        for (ref in activityRefs) {
            val activity = ref.get()
            if(activity != null && activity.javaClass.name == clazz.name){
                activity.finish()
                break
            }
        }
    }

    operator fun contains(clazz: Class<*>?): Boolean {
        if (clazz == null) {
            return false
        }
        var result = false
        for (ref in activityRefs) {
            val activity = ref.get()
            if(activity != null && activity.javaClass.name == clazz.name){
                result = true
                break
            }
        }
        return result
    }

    fun getActivityByCls(clazz: Class<*>?): Activity? {
        if (clazz == null) {
            return null
        }
        var result : Activity? = null
        for (ref in activityRefs) {
            val activity = ref.get()
            if(activity != null && activity.javaClass.name == clazz.name){
                result = activity
                break
            }
        }
        return result
    }

    fun exitAndKillStack(context: Context) {
        val activityManager = context.applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appTaskList = activityManager.appTasks
        for (appTask in appTaskList) {
            appTask.finishAndRemoveTask()
        }
    }

    fun closeApp(){
        activityRefs.forEach { ref ->
            ref.get()?.finish()
        }
        activityRefs.clear()
    }

    private fun add(activity: Activity) {
        activityRefs.add(WeakReference(activity))
    }

    private fun remove(activity: Activity) {
        removeFromWeakList(activityRefs, activity)
    }

    private fun removeFromWeakList(list: MutableList<WeakReference<Activity>>, activity: Activity) {
        val weakReferenceIterator = list.iterator()
        while (weakReferenceIterator.hasNext()) {
            if (weakReferenceIterator.next().get() === activity) {
                weakReferenceIterator.remove()
            }
        }
    }
}