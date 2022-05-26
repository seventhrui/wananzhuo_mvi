package com.seventh.demo.widget.navigationbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.seventh.demo.R;

import java.util.Locale;


public class LottieBarItem extends LinearLayout {

    private final Context mContext;
    private String lottieDataJson;
    private String lottieImagePath;

    private int iconMarginBottom = 0;//图标距离底部的距离

    private BottomBarLottieAnimationView lottieAnimationView;
    private int mIconWidth, mIconHeight;//图标的尺寸
    private TextView mTvUnread;
    private TextView mTvNotify;
    private TextView mTvMsg;
    private TextView mTextView;

    private int mUnreadTextSize = 10; //未读数默认字体大小10sp
    private int mMsgTextSize = 6; //消息默认字体大小6sp
    private int unreadNumThreshold = 99;//未读数阈值
    private int mUnreadTextColor;//未读数字体颜色
    private Drawable mUnreadTextBg;
    private int mMsgTextColor;
    private Drawable mMsgTextBg;
    private Drawable mNotifyPointBg;

    private String tabTitle;
    private int tabTitleSelectTextColor;
    private int tabTitleUnSelectTextColor;
    private int tabTitleTextSize;

    public LottieBarItem(Context context) {
        this(context, null);
    }

    public LottieBarItem(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LottieBarItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

        initView();

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NavigationBarLottieItem);
        initAttrs(typedArray); //初始化属性
        typedArray.recycle();

        setupConfig();//初始化相关操作
    }

    private void initAttrs(TypedArray typedArray) {
        lottieDataJson = typedArray.getString(R.styleable.NavigationBarLottieItem_navigation_lottie_json_data);
        lottieImagePath = typedArray.getString(R.styleable.NavigationBarLottieItem_navigation_lottie_image_path);

        mIconWidth = typedArray.getDimensionPixelSize(R.styleable.NavigationBarLottieItem_navigation_lottie_iconWidth, 0);
        mIconHeight = typedArray.getDimensionPixelSize(R.styleable.NavigationBarLottieItem_navigation_lottie_iconHeight, 0);
        iconMarginBottom = typedArray.getDimensionPixelSize(R.styleable.NavigationBarLottieItem_navigation_lottie_iconMarginBottom, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, iconMarginBottom, getResources().getDisplayMetrics()));

        mUnreadTextSize = typedArray.getDimensionPixelSize(R.styleable.NavigationBarLottieItem_navigation_lottie_unreadTextSize, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mUnreadTextSize, getResources().getDisplayMetrics()));
        mUnreadTextColor = typedArray.getColor(R.styleable.NavigationBarLottieItem_navigation_lottie_unreadTextColor, 0xFFFFFFFF);
        mUnreadTextBg = typedArray.getDrawable(R.styleable.NavigationBarLottieItem_navigation_lottie_unreadTextBg);

        mMsgTextSize = typedArray.getDimensionPixelSize(R.styleable.NavigationBarLottieItem_navigation_lottie_msgTextSize, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mMsgTextSize, getResources().getDisplayMetrics()));
        mMsgTextColor = typedArray.getColor(R.styleable.NavigationBarLottieItem_navigation_lottie_msgTextColor, 0xFFFFFFFF);
        mMsgTextBg = typedArray.getDrawable(R.styleable.NavigationBarLottieItem_navigation_lottie_msgTextBg);

        mNotifyPointBg = typedArray.getDrawable(R.styleable.NavigationBarLottieItem_navigation_lottie_notifyPointBg);

        unreadNumThreshold = typedArray.getInteger(R.styleable.NavigationBarLottieItem_navigation_lottie_unreadThreshold, 99);

        tabTitle = typedArray.getString(R.styleable.NavigationBarLottieItem_navigation_lottie_title);
        tabTitleTextSize = typedArray.getDimensionPixelSize(R.styleable.NavigationBarLottieItem_navigation_lottie_titleTextSize, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));

        tabTitleSelectTextColor = typedArray.getColor(R.styleable.NavigationBarLottieItem_navigation_lottie_titleSelectTextColor, Color.BLACK);
        tabTitleUnSelectTextColor = typedArray.getColor(R.styleable.NavigationBarLottieItem_navigation_lottie_titleUnSelectTextColor, Color.BLACK);
    }

    public void setLottieDataJson(String lottieDataJson) {
        this.lottieDataJson = lottieDataJson;
    }

    public void setLottieImagePath(String lottieImagePath) {
        this.lottieImagePath = lottieImagePath;
    }

    public void setIconMarginBottom(int iconMarginBottom) {
        this.iconMarginBottom = iconMarginBottom;
    }

    public void setmIconWidth(int mIconWidth) {
        this.mIconWidth = mIconWidth;
    }

    public void setmIconHeight(int mIconHeight) {
        this.mIconHeight = mIconHeight;
    }

    public void setmUnreadTextSize(int mUnreadTextSize) {
        this.mUnreadTextSize = mUnreadTextSize;
    }

    public void setmMsgTextSize(int mMsgTextSize) {
        this.mMsgTextSize = mMsgTextSize;
    }

    public void setmUnreadTextColor(int mUnreadTextColor) {
        this.mUnreadTextColor = mUnreadTextColor;
    }

    public void setmUnreadTextBg(Drawable mUnreadTextBg) {
        this.mUnreadTextBg = mUnreadTextBg;
    }

    public void setmMsgTextColor(int mMsgTextColor) {
        this.mMsgTextColor = mMsgTextColor;
    }

    public void setmMsgTextBg(Drawable mMsgTextBg) {
        this.mMsgTextBg = mMsgTextBg;
    }

    public void setmNotifyPointBg(Drawable mNotifyPointBg) {
        this.mNotifyPointBg = mNotifyPointBg;
    }

    public void setTabTitle(String tabTitle) {
        this.tabTitle = tabTitle;
    }

    public void setTabTitleSelectTextColor(int tabTitleSelectTextColor) {
        this.tabTitleSelectTextColor = tabTitleSelectTextColor;
    }

    public void setTabTitleUnSelectTextColor(int tabTitleUnSelectTextColor) {
        this.tabTitleUnSelectTextColor = tabTitleUnSelectTextColor;
    }

    public void setTabTitleTextSize(int tabTitleTextSize) {
        this.tabTitleTextSize = tabTitleTextSize;
    }

    /**
     * 检查传入的值是否完善
     */
    private void checkValues() {
        if (mUnreadTextBg == null) {
            mUnreadTextBg = getResources().getDrawable(R.drawable.navigationbar_shape_unread);
        }

        if (mMsgTextBg == null) {
            mMsgTextBg = getResources().getDrawable(R.drawable.navigationbar_shape_msg);
        }

        if (mNotifyPointBg == null) {
            mNotifyPointBg = getResources().getDrawable(R.drawable.navigationbar_shape_notify_point);
        }
    }

    public void setupConfig() {
        checkValues();
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);

        //lottie
        if (!TextUtils.isEmpty(lottieDataJson)) {
            lottieAnimationView.setAnimation(lottieDataJson);
        }

        if (TextUtils.isEmpty(lottieImagePath)) {
            lottieAnimationView.setImageAssetsFolder("images/");
        }

        FrameLayout.LayoutParams imageLayoutParams = (FrameLayout.LayoutParams) lottieAnimationView.getLayoutParams();
        //icon位置和宽度
        if (mIconWidth != 0) {
            imageLayoutParams.width = mIconWidth;
        }
        if (mIconHeight != 0) {
            imageLayoutParams.height = mIconHeight;
        }
        if (iconMarginBottom != 0) {
            imageLayoutParams.bottomMargin = iconMarginBottom;
        }

        lottieAnimationView.setLayoutParams(imageLayoutParams);

        mTvUnread.setTextSize(TypedValue.COMPLEX_UNIT_PX, mUnreadTextSize);//设置未读数的字体大小
        mTvUnread.setTextColor(mUnreadTextColor);//设置未读数字体颜色
        mTvUnread.setBackground(mUnreadTextBg);//设置未读数背景

        mTvMsg.setTextSize(TypedValue.COMPLEX_UNIT_PX, mMsgTextSize);//设置提示文字的字体大小
        mTvMsg.setTextColor(mMsgTextColor);//设置提示文字的字体颜色
        mTvMsg.setBackground(mMsgTextBg);//设置提示文字的背景颜色

        mTvNotify.setBackground(mNotifyPointBg);//设置提示点的背景颜色

        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTitleTextSize);
        mTextView.setText(tabTitle);
        mTextView.setTextColor(tabTitleUnSelectTextColor);
    }

    @NonNull
    private void initView() {
        View.inflate(mContext, R.layout.navigationbar_lottie_item, this);
        lottieAnimationView = findViewById(R.id.lottie_view);
        mTvUnread = findViewById(R.id.tv_unred_num);
        mTvMsg = findViewById(R.id.tv_msg);
        mTvNotify = findViewById(R.id.tv_point);
        mTextView = findViewById(R.id.tv_text);
    }

    public BottomBarLottieAnimationView getLottieAnimationView() {
        return lottieAnimationView;
    }

    public TextView getTextView() {
        return mTextView;
    }

    public void setStatus(boolean isSelected) {
        // 不做动画倒放，直接设置为第一帧默认
        if (isSelected) {
            lottieAnimationView.cancelAnimation();
            lottieAnimationView.setSpeed(1.0f);
            lottieAnimationView.playAnimation();
            // 更改选中项Title颜色
            mTextView.setTextColor(tabTitleSelectTextColor);
        } else {
            lottieAnimationView.cancelAnimation();
            lottieAnimationView.setProgress(0f);
            // 更改未选中项Title颜色
            mTextView.setTextColor(tabTitleUnSelectTextColor);
        }
    }

    private void setTvVisiable(TextView tv) {
        //都设置为不可见
        mTvUnread.setVisibility(GONE);
        mTvMsg.setVisibility(GONE);
        mTvNotify.setVisibility(GONE);

        tv.setVisibility(VISIBLE);//设置为可见
    }

    public int getUnreadNumThreshold() {
        return unreadNumThreshold;
    }

    public void setUnreadNumThreshold(int unreadNumThreshold) {
        this.unreadNumThreshold = unreadNumThreshold;
    }

    /**
     * 设置未读数
     */
    public void setUnreadNum(int unreadNum) {
        setTvVisiable(mTvUnread);
        if (unreadNum <= 0) {
            mTvUnread.setVisibility(GONE);
        } else if (unreadNum <= unreadNumThreshold) {
            mTvUnread.setText(String.valueOf(unreadNum));
        } else {
            mTvUnread.setText(String.format(Locale.CHINA, "%d+", unreadNumThreshold));
        }
    }

    public void setMsg(String msg) {
        setTvVisiable(mTvMsg);
        mTvMsg.setText(msg);
    }

    public void hideMsg() {
        mTvMsg.setVisibility(GONE);
    }

    public void showNotify() {
        setTvVisiable(mTvNotify);
    }

    public void hideNotify() {
        mTvNotify.setVisibility(GONE);
    }
}
