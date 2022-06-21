package com.seventh.demo.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {
    val statusLightMode = MutableLiveData<Boolean>()
}