package com.seventh.demo.ui.login

import android.content.Intent
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import com.seventh.demo.base.BaseAppCompatActivity
import com.seventh.demo.core.observeEvent
import com.seventh.demo.core.observeState
import com.seventh.demo.core.showToast
import com.seventh.demo.databinding.ActivityLoginBinding
import com.seventh.demo.ui.main.MainActivity

class LoginActivity: BaseAppCompatActivity<ActivityLoginBinding>(ActivityLoginBinding::inflate) {

    private val viewModel by viewModels<LoginViewModel>()

    override fun initView() {
        binding.etUsername.addTextChangedListener {
            viewModel.dispatch(LoginViewAction.UpdateUserName(it.toString()))
        }

        binding.etPassword.addTextChangedListener {
            viewModel.dispatch(LoginViewAction.UpdatePassword(it.toString()))
        }

        binding.btnLogin.setOnClickListener {
            viewModel.dispatch(LoginViewAction.Login)
        }
    }

    override fun initViewEvents() {
        viewModel.viewEvents.observeEvent(this) {
            when(it) {
                is LoginViewEvent.ShowToast -> it.message.showToast()
                is LoginViewEvent.ShowLoadingDialog -> showLoading()
                is LoginViewEvent.DismissLoadingDialog -> dismissLoading()
                is LoginViewEvent.LoginSuccess -> gotoMainActivity()
            }
        }
    }

    override fun initViewStates() {
        viewModel.viewStates.let { states ->
            states.observeState(this, LoginViewState::userName) {
                binding.etUsername.setText(it)
                binding.etUsername.setSelection(it.length)
            }
            states.observeState(this, LoginViewState::password) {
                binding.etPassword.setText(it)
                binding.etPassword.setSelection(it.length)
            }
            states.observeState(this, LoginViewState::isLoginEnable) {
                binding.btnLogin.isEnabled = it
                binding.btnLogin.alpha = if (it) 1f else 0.5f
            }
            states.observeState(this, LoginViewState::passwordTipVisible) {

            }
        }
    }

    private fun gotoMainActivity() {
        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
    }
}