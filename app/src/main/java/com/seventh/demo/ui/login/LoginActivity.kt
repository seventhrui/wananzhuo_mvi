package com.seventh.demo.ui.login

import android.app.ProgressDialog
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.orhanobut.logger.Logger
import com.seventh.demo.core.observeEvent
import com.seventh.demo.core.observeState
import com.seventh.demo.core.showToast
import com.seventh.demo.databinding.ActivityLoginBinding

class LoginActivity: AppCompatActivity() {
    lateinit var viewBinding: ActivityLoginBinding
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        initView()
        initViewStates()
        initViewEvents()
    }

    private fun initView() {
        viewBinding.etUsername.addTextChangedListener {
            viewModel.dispatch(LoginViewAction.UpdateUserName(it.toString()))
        }

        viewBinding.etPassword.addTextChangedListener {
            viewModel.dispatch(LoginViewAction.UpdatePassword(it.toString()))
        }

        viewBinding.btnLogin.setOnClickListener {
            viewModel.dispatch(LoginViewAction.Login)
        }
    }

    private fun initViewStates() {
        viewModel.viewStates.let { states ->
            states.observeState(this, LoginViewState::userName) {
                Logger.e("username：${it}")
                viewBinding.etUsername.setText(it)
                viewBinding.etUsername.setSelection(it.length)
            }
            states.observeState(this, LoginViewState::password) {
                viewBinding.etPassword.setText(it)
                viewBinding.etPassword.setSelection(it.length)
            }
            states.observeState(this, LoginViewState::isLoginEnable) {
                viewBinding.btnLogin.isEnabled = it
                viewBinding.btnLogin.alpha = if (it) 1f else 0.5f
            }
            states.observeState(this, LoginViewState::passwordTipVisible) {

            }
        }
    }

    private fun initViewEvents() {
        viewModel.viewEvents.observeEvent(this) {
            when(it) {
                is LoginViewEvent.ShowToast -> it.message.showToast()
                is LoginViewEvent.ShowLoadingDialog -> showLoadingDialog()
                is LoginViewEvent.DismissLoadingDialog -> dismissLoadingDialog()
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
}