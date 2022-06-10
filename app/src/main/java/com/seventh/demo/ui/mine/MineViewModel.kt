package com.seventh.demo.ui.mine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seventh.demo.core.SharedFlowEvents
import com.seventh.demo.core.setEvent
import com.seventh.demo.core.setState
import com.seventh.demo.data.vo.AppVersionVO
import com.seventh.demo.network.Api
import com.seventh.demo.network.HttpResult
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.lang.Exception

class MineViewModel: ViewModel() {
    private val _viewStates = MutableStateFlow(MineViewState())
    val viewStates = _viewStates.asStateFlow()
    private val _viewEvents = SharedFlowEvents<MineViewEvent>()
    val viewEvents = _viewEvents.asSharedFlow()

    fun dispatch(viewAction: MineViewAction) {
        when(viewAction) {
            is MineViewAction.GetAppVersion -> getAppVersion()
        }
    }

    private fun getAppVersion() {
        viewModelScope.launch {
            flow {
                emit(Api.service.checkAppUpdate())
            }.onStart {
                _viewEvents.setEvent(MineViewEvent.ShowLoadingDialog)
            }.map {
                if (it.status == 0){
                    HttpResult.Success(it.data!!)
                } else {
                    throw Exception(it.message)
                }
            }.onEach {
                _viewStates.setState { copy(appVersionVO= it.result) }
            }.catch {
                _viewEvents.setEvent(
                    MineViewEvent.ShowToast("${it.message}"),

                )
            }.onCompletion {
                _viewEvents.setEvent(MineViewEvent.DismissLoadingDialog)
            }.collect()
        }
    }
}

data class MineViewState(
    val appVersionVO: AppVersionVO? = null
)

sealed class MineViewEvent {
    data class ShowToast(val message: String) : MineViewEvent()
    object ShowLoadingDialog : MineViewEvent()
    object DismissLoadingDialog : MineViewEvent()
}

sealed class MineViewAction {
    object GetAppVersion: MineViewAction()
}