package com.seventh.demo.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

abstract class BaseAppCompatActivity<VB: ViewBinding>(private val bindingInflater: (inflater: LayoutInflater) -> VB): AppCompatActivity() {
    protected lateinit var binding: VB

    protected open fun initView() {}

    protected open fun initViewStates() {}

    protected open fun initViewEvents() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = bindingInflater.invoke(layoutInflater)
        setContentView(binding.root)

        initView()
        initViewStates()
        initViewEvents()
    }

    protected open fun showLoading() {}

    protected open fun dismissLoading() {}
}