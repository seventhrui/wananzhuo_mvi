package com.seventh.demo.widget.navigationbar

import android.content.Context
import com.airbnb.lottie.LottieAnimationView
import android.os.Parcelable
import android.util.AttributeSet

class BottomBarLottieAnimationView : LottieAnimationView {
    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }

    override fun onSaveInstanceState(): Parcelable? {
        var parcelable = super.onSaveInstanceState()
        parcelable = null
        return null
    }
}