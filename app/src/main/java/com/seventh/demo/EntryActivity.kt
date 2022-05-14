package com.seventh.demo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.seventh.demo.databinding.ActivityEntryBinding
import com.seventh.demo.ui.login.LoginActivity
import com.seventh.demo.ui.setting.SettingActivity

class EntryActivity : AppCompatActivity() {
    lateinit var viewBinding: ActivityEntryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityEntryBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

    }

    fun onLogin(view: View) {
        startActivity(Intent(this, LoginActivity::class.java))
    }

    fun onSetting(view: View) {
        startActivity(Intent(this, SettingActivity::class.java))
    }


}