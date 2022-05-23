package com.seventh.demo.widget.qmuirefresh

import android.content.Context
import android.view.ViewGroup
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.seventh.demo.R
import com.seventh.demo.extension.dimenPixelOffset

class CommonRefreshView(context: Context) : LottieAnimationView(context),
    QMUIPullRefreshLayout.IRefreshView {

    companion object {
        private const val TRIM_OFFSET = 0.4f
    }

    init {
        alpha = 0f

        imageAssetsFolder = "images/"
        setAnimation("common_pullrefresh.json")

        val layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val wh = context.dimenPixelOffset(R.dimen.dp_65)
        layoutParams.width = wh
        layoutParams.height = wh
        setLayoutParams(layoutParams)
    }

    override fun onPull(offset: Int, total: Int, overPull: Int) {
        alpha = 1f
        if (isAnimating) {
            return
        }
        repeatCount = 1

        var rotate = TRIM_OFFSET * offset / total
        if (overPull > 0) {
            rotate += TRIM_OFFSET * overPull / total
        }
        progress = rotate
    }

    override fun onCancel() {
        alpha = 0f
        cancelAnimation()
    }

    override fun stop() {
        alpha = 0f
        cancelAnimation()
    }

    override fun doRefresh() {
        alpha = 1f
        repeatCount = LottieDrawable.INFINITE
        progress = progress

        playAnimation()
    }

}
