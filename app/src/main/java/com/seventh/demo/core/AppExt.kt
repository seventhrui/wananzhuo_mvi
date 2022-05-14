package com.seventh.demo.core

import com.hjq.toast.ToastUtils

fun String?.showToast() {
    if (!this.isNullOrEmpty())
        ToastUtils.show(this)
}