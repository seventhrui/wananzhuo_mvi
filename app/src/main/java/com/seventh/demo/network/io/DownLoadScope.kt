package com.seventh.demo.network.io

import android.text.TextUtils
import com.seventh.demo.network.Api
import com.seventh.demo.utils.FileUtil.FileCachePath
import kotlinx.coroutines.*
import okhttp3.ResponseBody
import retrofit2.Call
import java.io.*

class DownLoadScope(
    val url: String,
    val path: String = FileCachePath,
    val dsl: DownloadProgressDSL.() -> Unit
): CoroutineScope by MainScope() {
    private lateinit var filePath: String
    private var mCall: Call<ResponseBody>? = null

    private val downloadProgressDSL: DownloadProgressDSL = DownloadProgressDSL().apply(dsl)

    suspend fun suspendStart() {
        var mFile: File? = null
        var outputStream: OutputStream? = null
        var inputStream: InputStream? = null
        try {
            withContext(Dispatchers.Main) {
                downloadProgressDSL.before?.invoke()
            }
            var fileName: String = url
            val index = fileName.lastIndexOf('/')
            if (index != -1) {
                fileName = fileName.substring(index)
                filePath = "$path$fileName"
            }
            if (TextUtils.isEmpty(filePath))
                return

            mFile = File(filePath)
            if (!mFile.parentFile.exists())
                mFile.parentFile.mkdirs()

            withContext(Dispatchers.Main) {
                downloadProgressDSL.start?.invoke()
            }

            mCall = Api.downloadService.downloadFile(url)
            val response = mCall?.execute()
            val body = response?.body()
            val totalLength = body?.contentLength()
            if (body == null || totalLength == null || totalLength < 0) {
                withContext(Dispatchers.Main) {
                    downloadProgressDSL.failure?.invoke("response error")
                }
                return
            }

            val historyFile = File(filePath)
            if (historyFile.exists() && historyFile.length() == totalLength) {
                withContext(Dispatchers.Main) {
                    downloadProgressDSL.finish?.invoke(filePath)
                }
                return
            } else {
                historyFile.delete()
            }

            inputStream = body.byteStream()
            outputStream = FileOutputStream(mFile)
            var len: Int
            val buff = ByteArray(2048)
            var writeLength: Long = 0
            while (inputStream.read(buff).also { len = it } != -1) {
                outputStream.write(buff, 0, len)
                writeLength += len.toLong()
                withContext(Dispatchers.Main) {
                    downloadProgressDSL.progress?.invoke((100 * writeLength / totalLength).toInt())
                }
                if ((100 * writeLength / totalLength).toInt() == 100 && writeLength == totalLength) {
                    withContext(Dispatchers.Main) {
                        downloadProgressDSL.finish?.invoke(filePath)
                    }
                }
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
            withContext(Dispatchers.Main) {
                downloadProgressDSL.failure?.invoke("response error")
            }
            mFile?.delete()
        } finally {
            try {
                outputStream?.close()
                inputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun start() {
        launch(Dispatchers.IO) {
            suspendStart()
        }
    }

    fun cancelIO() {
        try {
            mCall?.cancel()
            cancel()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}