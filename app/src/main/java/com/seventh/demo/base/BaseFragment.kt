package com.seventh.demo.base

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.orhanobut.logger.Logger
import com.seventh.demo.R
import com.seventh.demo.extension.colorById
import com.seventh.demo.widget.statusbar.StatusBarCompat

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
        initData()
    }

    protected open fun initData() {}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    protected open fun showLoading() {
    }

    protected open fun dismissLoading() {
    }

    /**
     * 状态栏重写
     * @param isHideStatusBar 是否要隐藏 状态栏占位 默认false
     * @param is_M_LightMode 日间模式 状态栏 字体图标 为黑色 默认true, false为darkMode
     * @param color 配合暗色模式使用，状态栏 字体图标 为白色，默认 white走is_M_LightMode模式
     */
    protected open fun overrideStatusBar(
        isHideStatusBar: Boolean = false,
        is_M_LightMode: Boolean = true,
        @ColorInt color: Int = mContext.colorById(R.color.white),
    ) {
        //6.0 M 以上
        activity?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (isHideStatusBar) {
                    StatusBarCompat.translucentStatusBar(it, true)
                } else {
                    StatusBarCompat.setStatusBarColor(it, color)
                }
                if (is_M_LightMode) {
                    StatusBarCompat.setStatusBarLightMode(it)
                } else {
                    StatusBarCompat.setStatusBarDarkMode(it)
                }
            } else {
                StatusBarCompat.setStatusBarColor(it, color, 80)
            }
        }
    }
}