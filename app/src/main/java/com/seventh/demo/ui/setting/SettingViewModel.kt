package com.seventh.demo.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import com.seventh.demo.core.SharedFlowEvents
import com.seventh.demo.core.setEvent
import com.seventh.demo.core.setState
import com.seventh.demo.network.Api
import com.seventh.demo.vo.AppVersionVO
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SettingViewModel: ViewModel() {
    private val _viewStates = MutableStateFlow(SettingViewState())
    val viewStates = _viewStates.asStateFlow()
    private val _viewEvents = SharedFlowEvents<SettingViewEvent>()
    val viewEvents = _viewEvents.asSharedFlow()

    fun dispatch(viewAction: SettingViewAction) {
        when(viewAction) {
            is SettingViewAction.UpdateVersion -> updateVersion()
        }
    }

    private fun updateVersion() {
        viewModelScope.launch {
            flow {
                emit(Api.service.checkAppUpdate())
            }.onStart {
                _viewEvents.setEvent(SettingViewEvent.ShowLoadingDialog)
            }.onEach {
                _viewStates.setState { copy(appVersion = it.data!!) }
                _viewEvents.setEvent(
                    SettingViewEvent.DismissLoadingDialog,
                    SettingViewEvent.ShowToast(it.message)
                )
            }.catch {
                Logger.e("请求失败, %s", it)
                _viewEvents.setEvent(
                    SettingViewEvent.DismissLoadingDialog,
                    SettingViewEvent.ShowToast("请求失败！")
                )
            }.collect()
        }
    }

}

data class SettingViewState(
    val appVersion: AppVersionVO? = null
)

sealed class SettingViewEvent {
    data class ShowToast(val message: String): SettingViewEvent()
    object ShowLoadingDialog: SettingViewEvent()
    object DismissLoadingDialog: SettingViewEvent()
    object DismissUpdateDialog: SettingViewEvent()
}

sealed class SettingViewAction {
    object UpdateVersion: SettingViewAction()
}