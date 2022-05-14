package com.seventh.demo.ui.setting

import android.app.ProgressDialog
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.orhanobut.logger.Logger
import com.seventh.demo.core.observeEvent
import com.seventh.demo.core.observeState
import com.seventh.demo.core.showToast
import com.seventh.demo.databinding.ActivitySettingBinding
import com.seventh.demo.ui.login.LoginViewState
import com.seventh.demo.vo.AppVersionVO

class SettingActivity: AppCompatActivity() {
    lateinit var viewBinding: ActivitySettingBinding
    private val viewModel by viewModels<SettingViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        initView()
        initViewStates()
        initViewEvents()
    }

    private fun initView() {

        viewBinding.btnUpdate.setOnClickListener {
            viewModel.dispatch(SettingViewAction.UpdateVersion)
        }
    }

    private fun initViewStates() {
        viewModel.viewStates.let { states ->
            states.observeState(this, SettingViewState::appVersion) {
                if (it!=null) {
                    showUploadDialog(it)
                }
            }
        }
    }

    private fun initViewEvents() {
        viewModel.viewEvents.observeEvent(this) {
            when(it) {
                is SettingViewEvent.ShowToast -> it.message.showToast()
                is SettingViewEvent.ShowLoadingDialog -> showLoadingDialog()
                is SettingViewEvent.DismissLoadingDialog -> dismissLoadingDialog()
            }
        }
    }

    private var progressDialog: ProgressDialog? = null

    private fun showLoadingDialog() {
        if (progressDialog == null) {
            progressDialog = ProgressDialog(this)
        }
        progressDialog?.show()
    }

    private fun dismissLoadingDialog() {
        progressDialog?.takeIf { it.isShowing }?.dismiss()
    }

    private fun showUploadDialog(appVersionVO: AppVersionVO) {
        Logger.e("有新版本${appVersionVO?.desc}")

    }
}