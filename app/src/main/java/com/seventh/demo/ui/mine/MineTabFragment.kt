package com.seventh.demo.ui.mine

import androidx.fragment.app.viewModels
import com.seventh.demo.base.BaseFragment
import com.seventh.demo.core.observeEvent
import com.seventh.demo.core.observeState
import com.seventh.demo.core.showToast
import com.seventh.demo.databinding.FragmentMineBinding

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
    }

    override fun initViewStates() {
        viewModel.viewStates.let { states ->
            states.observeState(this, MineViewState::appVersionVO) {

            }

        }
    }

}