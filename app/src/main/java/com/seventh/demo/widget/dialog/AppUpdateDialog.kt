package com.seventh.demo.widget.dialog

import android.Manifest
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import com.seventh.demo.R
import com.seventh.demo.base.BaseDialogFragment
import com.seventh.demo.core.showToast
import com.seventh.demo.data.vo.AppVersionVO
import com.seventh.demo.databinding.DialogAppUpdateBinding
import com.seventh.demo.network.io.DownLoadScope
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions

@RuntimePermissions
class AppUpdateDialog: BaseDialogFragment<DialogAppUpdateBinding>(DialogAppUpdateBinding::inflate) {

    companion object {
        var downloadScope: DownLoadScope? = null

        fun newInstance(appVersionVO: AppVersionVO): AppUpdateDialog {
            val bundle = Bundle()
            bundle.putSerializable("data", appVersionVO)
            val appUpdateDialog = AppUpdateDialog()
            appUpdateDialog.arguments = bundle
            return appUpdateDialog
        }
    }

    override fun themeStyle(): Int = R.style.popup_dialog_style

    override fun initWindow() {
        val window = dialog?.window
        window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window?.setGravity(Gravity.CENTER)
        isCancelable = false
        dialog?.setCanceledOnTouchOutside(false)
    }

    override fun initView() {
        val appVersionVO = arguments?.get("data") as AppVersionVO
        binding.tvVersion.text = appVersionVO.version_name.plus("\n").plus(appVersionVO.updatetime).plus("\n").plus(appVersionVO.desc)

        binding.tvDownload.setOnClickListener {
            needPermissionWithPermissionCheck(appVersionVO.url)
        }
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun needPermission(downloadApkPath: String) {
        downloadScope = DownLoadScope(url = downloadApkPath, dsl = {
            onBefore {
                if (isBindingDestroy) return@onBefore
                binding.tvDownload.text = "准备下载"
                binding.tvDownload.isEnabled = false
            }
            onStart {
                if (isBindingDestroy) return@onStart
                binding.tvDownload.text = "正在升级"
                binding.tvDownload.isEnabled = false
            }
            onProgress { progress ->
                if (isBindingDestroy) return@onProgress
                binding.tvDownload.isEnabled = false
                binding.tvDownload.text = "正在下载($progress%)"
            }
            onFinish { path ->
                if (isBindingDestroy) return@onFinish
                binding.tvDownload.text = "下载完成，安装"
                binding.tvDownload.isEnabled = true
                // 通知安装
//                invokeDismiss()
            }
            onFailure {
                if (isBindingDestroy) return@onFailure
                binding.tvDownload.isEnabled = true
                binding.tvDownload.text = "重新下载"
                "APP更新，下载失败".showToast()
            }
        })
        downloadScope?.start()
    }

    override fun onDestroyView() {
        downloadScope?.let { it.cancelIO() }
        super.onDestroyView()
    }

    @OnPermissionDenied(
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    fun permissionDenied() {
        invokeDismiss()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }
}