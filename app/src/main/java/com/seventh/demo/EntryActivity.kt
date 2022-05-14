package com.seventh.demo

import android.content.Intent
import android.view.View
import com.seventh.demo.base.BaseAppCompatActivity
import com.seventh.demo.databinding.ActivityEntryBinding
import com.seventh.demo.ui.login.LoginActivity
import com.seventh.demo.ui.setting.SettingActivity

class EntryActivity : BaseAppCompatActivity<ActivityEntryBinding>(ActivityEntryBinding::inflate) {

    fun onLogin(view: View) {
        startActivity(Intent(this, LoginActivity::class.java))
    }

    fun onSetting(view: View) {
        startActivity(Intent(this, SettingActivity::class.java))
    }


}