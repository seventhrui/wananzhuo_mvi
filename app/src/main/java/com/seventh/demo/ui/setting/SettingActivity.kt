package com.seventh.demo.ui.setting

import androidx.activity.viewModels
import com.orhanobut.logger.Logger
import com.seventh.demo.base.BaseAppCompatActivity
import com.seventh.demo.core.observeEvent
import com.seventh.demo.core.observeState
import com.seventh.demo.core.showToast
import com.seventh.demo.databinding.ActivitySettingBinding
import com.seventh.demo.data.vo.AppVersionVO

class SettingActivity: BaseAppCompatActivity<ActivitySettingBinding>(ActivitySettingBinding::inflate) {
    private val viewModel by viewModels<SettingViewModel>()

    override fun initView() {
        binding.btnUpdate.setOnClickListener {
            viewModel.dispatch(SettingViewAction.UpdateVersion)
        }
    }

    override fun initViewStates() {
        viewModel.viewStates.let { states ->
            states.observeState(this, SettingViewState::appVersion) {
                if (it!=null) {
                    showUploadDialog(it)
                }
            }
        }
    }

    override fun initViewEvents() {
        viewModel.viewEvents.observeEvent(this) {
            when(it) {
                is SettingViewEvent.ShowToast -> it.message.showToast()
                is SettingViewEvent.ShowLoadingDialog -> showLoading()
                is SettingViewEvent.DismissLoadingDialog -> dismissLoading()
            }
        }
    }

    private fun showUploadDialog(appVersionVO: AppVersionVO) {
        Logger.e("有新版本${appVersionVO?.desc}")

    }
}