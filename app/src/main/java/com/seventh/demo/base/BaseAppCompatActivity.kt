package com.seventh.demo.base

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.seventh.demo.widget.statusbar.StatusBarCompat
import com.seventh.demo.R
import com.seventh.demo.extension.colorById

abstract class BaseAppCompatActivity<VB : ViewBinding>(private val bindingInflater: (inflater: LayoutInflater) -> VB) :
    AppCompatActivity() {
    protected lateinit var binding: VB

    protected open fun initView() {}

    protected open fun initViewStates() {}

    protected open fun initViewEvents() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = bindingInflater.invoke(layoutInflater)
        setContentView(binding.root)
        //状态栏重写
        overrideStatusBar(is_M_LightMode = true)

        initView()
        initViewStates()
        initViewEvents()
        initData()
    }

    protected open fun initData() {}

    protected open fun showLoading() {}

    protected open fun dismissLoading() {}

    /**
     * 状态栏重写
     * @param isHideStatusBar 是否要隐藏 状态栏占位 默认false
     * @param is_M_LightMode 日间模式 状态栏 字体图标 为黑色 默认true, false为darkMode
     * @param color 配合暗色模式使用，状态栏 字体图标 为白色，默认 white走is_M_LightMode模式
     */
    protected open fun overrideStatusBar(
        isHideStatusBar: Boolean = false,
        is_M_LightMode: Boolean = true,
        @ColorInt color: Int = colorById(R.color.white),
    ) {
        //6.0 M 以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isHideStatusBar) {
                StatusBarCompat.translucentStatusBar(this, true)
            } else {
                StatusBarCompat.setStatusBarColor(this, color)
            }
            if (is_M_LightMode) {
                StatusBarCompat.setStatusBarLightMode(this)
            } else {
                StatusBarCompat.setStatusBarDarkMode(this)
            }
        } else {
            StatusBarCompat.setStatusBarColor(this, color, 80)
        }
    }
}