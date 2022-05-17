package com.seventh.demo.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import com.seventh.demo.core.SharedFlowEvents
import com.seventh.demo.core.setEvent
import com.seventh.demo.core.setState
import com.seventh.demo.data.store.AppUserUtil
import com.seventh.demo.network.Api
import com.seventh.demo.network.HttpResult
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.lang.Exception

class LoginViewModel: ViewModel() {
    private val _viewStates = MutableStateFlow(LoginViewState())
    val viewStates = _viewStates.asStateFlow()
    private val _viewEvents = SharedFlowEvents<LoginViewEvent>()
    val viewEvents = _viewEvents.asSharedFlow()

    fun dispatch(viewAction: LoginViewAction) {
        when(viewAction) {
            is LoginViewAction.UpdateUserName -> updateUserName(viewAction.userName)
            is LoginViewAction.UpdatePassword -> updatePassword(viewAction.password)
            is LoginViewAction.Login -> login()
        }
    }

    private fun updateUserName(userName: String) {
        _viewStates.setState { copy(userName = userName) }
    }

    private fun updatePassword(password: String) {
        _viewStates.setState { copy(password = password) }
    }

    private fun login() {
        viewModelScope.launch {
            flow {
                var params = HashMap<String, Any>()
                params["username"] = viewStates.value.userName
                params["password"] = viewStates.value.password
                emit(Api.service.loginUser(params))
            }.onStart {
                _viewEvents.setEvent(LoginViewEvent.ShowLoadingDialog)
            }.map {
                if (it.status == 0){
                    HttpResult.Success(it.data!!)
                } else {
                    throw Exception(it.message)
                }
            }.onEach {
                AppUserUtil.onLogin(it.result)
                _viewEvents.setEvent(
                    LoginViewEvent.LoginSuccess,
                    LoginViewEvent.DismissLoadingDialog
                )
            }.catch {
                _viewStates.setState { copy(password = "") }
                Logger.e("登陆失败:${it}")
                _viewEvents.setEvent(
                    LoginViewEvent.DismissLoadingDialog,
                    LoginViewEvent.ShowToast("${it.message}")
                )
            }.collect()
        }
    }
}

data class LoginViewState(val userName: String = "", val password: String = "") {
    val isLoginEnable: Boolean
        get() = userName.isNotEmpty() && password.isNotEmpty()
    val passwordTipVisible: Boolean
        get() = password.length in 1..5
}

sealed class LoginViewEvent {
    data class ShowToast(val message: String): LoginViewEvent()
    object ShowLoadingDialog: LoginViewEvent()
    object DismissLoadingDialog: LoginViewEvent()
    object LoginSuccess: LoginViewEvent()
}

sealed class LoginViewAction {
    data class UpdateUserName(val userName: String): LoginViewAction()
    data class UpdatePassword(val password: String): LoginViewAction()
    object Login: LoginViewAction()
}
