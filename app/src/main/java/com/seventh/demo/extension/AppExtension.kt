package com.seventh.demo.extension

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import java.io.File

fun Context.install(downloadApkPath: String) {
    try {
        val intent = getInstallApkIntent(this, downloadApkPath)
        intent?.let {
            if (this.packageManager.queryIntentActivities(intent, 0).size > 0) {
                this.startActivity(intent)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun getInstallApkIntent(context: Context, downloadApk: String): Intent? {
    try {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val file = File(downloadApk)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //区别于 FLAG_GRANT_READ_URI_PERMISSION 跟 FLAG_GRANT_WRITE_URI_PERMISSION， URI权限会持久存在即使重启，直到明确的用 revokeUriPermission(Uri, int) 撤销。 这个flag只提供可能持久授权。但是接收的应用必须调用ContentResolver的takePersistableUriPermission(Uri, int)方法实现
            intent.flags =
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
            val apkUri =
                FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive")
        }
        return intent
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}