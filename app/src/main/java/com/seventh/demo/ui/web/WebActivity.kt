package com.seventh.demo.ui.web

import com.seventh.demo.base.BaseAppCompatActivity
import com.seventh.demo.databinding.ActivityWebBinding
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.WebChromeClient

class WebActivity: BaseAppCompatActivity<ActivityWebBinding>(ActivityWebBinding::inflate) {

    override fun initView() {
        binding.wbContent.webChromeClient = object: WebChromeClient() {

        }
        binding.wbContent.apply {
            settings.javaScriptEnabled = true
            settingsExtension?.setDisplayCutoutEnable(true)
            settings.javaScriptCanOpenWindowsAutomatically = true
            settings.allowContentAccess = true
            settings.loadWithOverviewMode = true
            settings.setAppCacheEnabled(true)
            settings.domStorageEnabled = true
            settings.mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        binding.wbContent.loadUrl(intent.getStringExtra("url"))
    }

    override fun onDestroy() {
        QbSdk.clearAllWebViewCache(this, true)
        super.onDestroy()
    }
}