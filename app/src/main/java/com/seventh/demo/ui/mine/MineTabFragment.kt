package com.seventh.demo.ui.mine

import android.content.Intent
import androidx.fragment.app.viewModels
import com.seventh.demo.base.BaseFragment
import com.seventh.demo.core.observeEvent
import com.seventh.demo.core.observeState
import com.seventh.demo.core.showToast
import com.seventh.demo.databinding.FragmentMineBinding
import com.seventh.demo.ui.login.LoginActivity
import com.seventh.demo.widget.dialog.AppUpdateDialog

class MineTabFragment: BaseFragment<FragmentMineBinding>(FragmentMineBinding::inflate) {
    private val viewModel by viewModels<MineViewModel>()

    override fun initView() {

    }

    override fun initViewEvents() {
        viewModel.viewEvents.observeEvent(this) {
            when(it) {
                is MineViewEvent.ShowLoadingDialog -> showLoading()
                is MineViewEvent.DismissLoadingDialog -> dismissLoading()
                is MineViewEvent.ShowToast -> it.message.showToast()
            }
        }

        binding.tvTest.setOnClickListener {
            viewModel.dispatch(MineViewAction.GetAppVersion)
        }

        binding.tvLogin.setOnClickListener {
            startActivity(Intent(mContext, LoginActivity::class.java))
        }
    }

    override fun initViewStates() {
        viewModel.viewStates.let { states ->
            states.observeState(this, MineViewState::appVersionVO) {
                it?.let{
                    AppUpdateDialog.newInstance(it).show(childFragmentManager)
                }
            }
        }
    }

}