package com.seventh.demo.base

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding

abstract class BaseDialogFragment<VB: ViewBinding>(private val bindingInflater: (inflater: LayoutInflater)->VB): DialogFragment() {
    private var _binding: VB? = null
    protected val binding get() = _binding as VB
    protected val isBindingDestroy get() = _binding == null
    protected lateinit var mContext: Context

    abstract fun themeStyle(): Int

    abstract fun initWindow()

    abstract fun initView()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, themeStyle())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = bindingInflater.invoke(inflater)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun onStart() {
        super.onStart()
        initWindow()
    }

    fun show(fragmentManager: FragmentManager) {
        val fragmentTransaction = fragmentManager.beginTransaction()
        if (this.isAdded) {
            fragmentTransaction.remove(this).commit()
        }
        fragmentTransaction.add(this, System.currentTimeMillis().toString())
        fragmentTransaction.commitAllowingStateLoss()
    }

    /**
     * 关闭监听 一般priority弹窗使用
     */
    interface DismissCallback {
        fun onDismiss(dialog: DialogInterface)
    }

    private var dismissCallback: DismissCallback? = null

    fun setDismissCallback(dismissCallback: DismissCallback?) {
        this.dismissCallback = dismissCallback
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dismissCallback?.let {
            it.onDismiss(dialog)
        }
    }

    /**
     * 关闭弹窗
     */
    fun invokeDismiss() {
        dismissAllowingStateLoss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}