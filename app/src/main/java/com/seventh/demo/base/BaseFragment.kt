package com.seventh.demo.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<VB : ViewBinding>(private val bindingInflater: (inflater: LayoutInflater) -> VB) :
    Fragment() {

    protected lateinit var mContext: Context

    private var _binding: VB? = null
    protected val binding get() = _binding as VB
    protected val isBindingDestroy get() = _binding == null

    protected open fun initView() {}

    protected open fun initViewStates() {}

    protected open fun initViewEvents() {}

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = bindingInflater.invoke(layoutInflater)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initViewStates()
        initViewEvents()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}