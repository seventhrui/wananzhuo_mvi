package com.seventh.demo.widget.navigationbar;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;

import com.airbnb.lottie.LottieAnimationView;

public class BottomBarLottieAnimationView extends LottieAnimationView {
    public BottomBarLottieAnimationView(Context context) {
        super(context);
    }

    public BottomBarLottieAnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BottomBarLottieAnimationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable parcelable = super.onSaveInstanceState();
        parcelable = null;
        return null;
    }
}
