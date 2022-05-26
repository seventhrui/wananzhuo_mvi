package com.seventh.demo.widget.navigationbar

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.seventh.demo.R
import java.util.*

class LottieBarItem @JvmOverloads constructor(
    private val mContext: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(
    mContext, attrs, defStyleAttr
) {
    private var lottieDataJson: String? = null
    private var lottieImagePath: String? = null
    private var iconMarginBottom = 0 //图标距离底部的距离
    var lottieAnimationView: BottomBarLottieAnimationView? = null
        private set
    private var mIconWidth = 0
    private var mIconHeight //图标的尺寸
            = 0
    private var mTvUnread: TextView? = null
    private var mTvNotify: TextView? = null
    private var mTvMsg: TextView? = null
    var textView: TextView? = null
        private set
    private var mUnreadTextSize = 10 //未读数默认字体大小10sp
    private var mMsgTextSize = 6 //消息默认字体大小6sp
    var unreadNumThreshold = 99 //未读数阈值
    private var mUnreadTextColor //未读数字体颜色
            = 0
    private var mUnreadTextBg: Drawable? = null
    private var mMsgTextColor = 0
    private var mMsgTextBg: Drawable? = null
    private var mNotifyPointBg: Drawable? = null
    private var tabTitle: String? = null
    private var tabTitleSelectTextColor = 0
    private var tabTitleUnSelectTextColor = 0
    private var tabTitleTextSize = 0
    private fun initAttrs(typedArray: TypedArray) {
        lottieDataJson =
            typedArray.getString(R.styleable.NavigationBarLottieItem_navigation_lottie_json_data)
        lottieImagePath =
            typedArray.getString(R.styleable.NavigationBarLottieItem_navigation_lottie_image_path)
        mIconWidth = typedArray.getDimensionPixelSize(
            R.styleable.NavigationBarLottieItem_navigation_lottie_iconWidth,
            0
        )
        mIconHeight = typedArray.getDimensionPixelSize(
            R.styleable.NavigationBarLottieItem_navigation_lottie_iconHeight,
            0
        )
        iconMarginBottom = typedArray.getDimensionPixelSize(
            R.styleable.NavigationBarLottieItem_navigation_lottie_iconMarginBottom,
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                iconMarginBottom.toFloat(),
                resources.displayMetrics
            ).toInt()
        )
        mUnreadTextSize = typedArray.getDimensionPixelSize(
            R.styleable.NavigationBarLottieItem_navigation_lottie_unreadTextSize,
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                mUnreadTextSize.toFloat(),
                resources.displayMetrics
            ).toInt()
        )
        mUnreadTextColor = typedArray.getColor(
            R.styleable.NavigationBarLottieItem_navigation_lottie_unreadTextColor,
            -0x1
        )
        mUnreadTextBg =
            typedArray.getDrawable(R.styleable.NavigationBarLottieItem_navigation_lottie_unreadTextBg)
        mMsgTextSize = typedArray.getDimensionPixelSize(
            R.styleable.NavigationBarLottieItem_navigation_lottie_msgTextSize,
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                mMsgTextSize.toFloat(),
                resources.displayMetrics
            ).toInt()
        )
        mMsgTextColor = typedArray.getColor(
            R.styleable.NavigationBarLottieItem_navigation_lottie_msgTextColor,
            -0x1
        )
        mMsgTextBg =
            typedArray.getDrawable(R.styleable.NavigationBarLottieItem_navigation_lottie_msgTextBg)
        mNotifyPointBg =
            typedArray.getDrawable(R.styleable.NavigationBarLottieItem_navigation_lottie_notifyPointBg)
        unreadNumThreshold = typedArray.getInteger(
            R.styleable.NavigationBarLottieItem_navigation_lottie_unreadThreshold,
            99
        )
        tabTitle = typedArray.getString(R.styleable.NavigationBarLottieItem_navigation_lottie_title)
        tabTitleTextSize = typedArray.getDimensionPixelSize(
            R.styleable.NavigationBarLottieItem_navigation_lottie_titleTextSize,
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12f, resources.displayMetrics)
                .toInt()
        )
        tabTitleSelectTextColor = typedArray.getColor(
            R.styleable.NavigationBarLottieItem_navigation_lottie_titleSelectTextColor,
            Color.BLACK
        )
        tabTitleUnSelectTextColor = typedArray.getColor(
            R.styleable.NavigationBarLottieItem_navigation_lottie_titleUnSelectTextColor,
            Color.BLACK
        )
    }

    fun setLottieDataJson(lottieDataJson: String?) {
        this.lottieDataJson = lottieDataJson
    }

    fun setLottieImagePath(lottieImagePath: String?) {
        this.lottieImagePath = lottieImagePath
    }

    fun setIconMarginBottom(iconMarginBottom: Int) {
        this.iconMarginBottom = iconMarginBottom
    }

    fun setmIconWidth(mIconWidth: Int) {
        this.mIconWidth = mIconWidth
    }

    fun setmIconHeight(mIconHeight: Int) {
        this.mIconHeight = mIconHeight
    }

    fun setmUnreadTextSize(mUnreadTextSize: Int) {
        this.mUnreadTextSize = mUnreadTextSize
    }

    fun setmMsgTextSize(mMsgTextSize: Int) {
        this.mMsgTextSize = mMsgTextSize
    }

    fun setmUnreadTextColor(mUnreadTextColor: Int) {
        this.mUnreadTextColor = mUnreadTextColor
    }

    fun setmUnreadTextBg(mUnreadTextBg: Drawable?) {
        this.mUnreadTextBg = mUnreadTextBg
    }

    fun setmMsgTextColor(mMsgTextColor: Int) {
        this.mMsgTextColor = mMsgTextColor
    }

    fun setmMsgTextBg(mMsgTextBg: Drawable?) {
        this.mMsgTextBg = mMsgTextBg
    }

    fun setmNotifyPointBg(mNotifyPointBg: Drawable?) {
        this.mNotifyPointBg = mNotifyPointBg
    }

    fun setTabTitle(tabTitle: String?) {
        this.tabTitle = tabTitle
    }

    fun setTabTitleSelectTextColor(tabTitleSelectTextColor: Int) {
        this.tabTitleSelectTextColor = tabTitleSelectTextColor
    }

    fun setTabTitleUnSelectTextColor(tabTitleUnSelectTextColor: Int) {
        this.tabTitleUnSelectTextColor = tabTitleUnSelectTextColor
    }

    fun setTabTitleTextSize(tabTitleTextSize: Int) {
        this.tabTitleTextSize = tabTitleTextSize
    }

    /**
     * 检查传入的值是否完善
     */
    private fun checkValues() {
        if (mUnreadTextBg == null) {
            mUnreadTextBg = resources.getDrawable(R.drawable.navigationbar_shape_unread)
        }
        if (mMsgTextBg == null) {
            mMsgTextBg = resources.getDrawable(R.drawable.navigationbar_shape_msg)
        }
        if (mNotifyPointBg == null) {
            mNotifyPointBg = resources.getDrawable(R.drawable.navigationbar_shape_notify_point)
        }
    }

    fun setupConfig() {
        checkValues()
        orientation = VERTICAL
        gravity = Gravity.CENTER

        //lottie
        if (!TextUtils.isEmpty(lottieDataJson)) {
            lottieAnimationView!!.setAnimation(lottieDataJson)
        }
        if (TextUtils.isEmpty(lottieImagePath)) {
            lottieAnimationView!!.imageAssetsFolder = "images/"
        }
        val imageLayoutParams = lottieAnimationView!!.layoutParams as FrameLayout.LayoutParams
        //icon位置和宽度
        if (mIconWidth != 0) {
            imageLayoutParams.width = mIconWidth
        }
        if (mIconHeight != 0) {
            imageLayoutParams.height = mIconHeight
        }
        if (iconMarginBottom != 0) {
            imageLayoutParams.bottomMargin = iconMarginBottom
        }
        lottieAnimationView!!.layoutParams = imageLayoutParams
        mTvUnread!!.setTextSize(TypedValue.COMPLEX_UNIT_PX, mUnreadTextSize.toFloat()) //设置未读数的字体大小
        mTvUnread!!.setTextColor(mUnreadTextColor) //设置未读数字体颜色
        mTvUnread!!.background = mUnreadTextBg //设置未读数背景
        mTvMsg!!.setTextSize(TypedValue.COMPLEX_UNIT_PX, mMsgTextSize.toFloat()) //设置提示文字的字体大小
        mTvMsg!!.setTextColor(mMsgTextColor) //设置提示文字的字体颜色
        mTvMsg!!.background = mMsgTextBg //设置提示文字的背景颜色
        mTvNotify!!.background = mNotifyPointBg //设置提示点的背景颜色
        textView!!.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTitleTextSize.toFloat())
        textView!!.text = tabTitle
        textView!!.setTextColor(tabTitleUnSelectTextColor)
    }

    private fun initView() {
        inflate(mContext, R.layout.navigationbar_lottie_item, this)
        lottieAnimationView = findViewById(R.id.lottie_view)
        mTvUnread = findViewById(R.id.tv_unred_num)
        mTvMsg = findViewById(R.id.tv_msg)
        mTvNotify = findViewById(R.id.tv_point)
        textView = findViewById(R.id.tv_text)
    }

    fun setStatus(isSelected: Boolean) {
        // 不做动画倒放，直接设置为第一帧默认
        if (isSelected) {
            lottieAnimationView!!.cancelAnimation()
            lottieAnimationView!!.speed = 1.0f
            lottieAnimationView!!.playAnimation()
            // 更改选中项Title颜色
            textView!!.setTextColor(tabTitleSelectTextColor)
        } else {
            lottieAnimationView!!.cancelAnimation()
            lottieAnimationView!!.progress = 0f
            // 更改未选中项Title颜色
            textView!!.setTextColor(tabTitleUnSelectTextColor)
        }
    }

    private fun setTvVisiable(tv: TextView?) {
        //都设置为不可见
        mTvUnread!!.visibility = GONE
        mTvMsg!!.visibility = GONE
        mTvNotify!!.visibility = GONE
        tv!!.visibility = VISIBLE //设置为可见
    }

    /**
     * 设置未读数
     */
    fun setUnreadNum(unreadNum: Int) {
        setTvVisiable(mTvUnread)
        if (unreadNum <= 0) {
            mTvUnread!!.visibility = GONE
        } else if (unreadNum <= unreadNumThreshold) {
            mTvUnread!!.text = unreadNum.toString()
        } else {
            mTvUnread!!.text = String.format(Locale.CHINA, "%d+", unreadNumThreshold)
        }
    }

    fun setMsg(msg: String?) {
        setTvVisiable(mTvMsg)
        mTvMsg!!.text = msg
    }

    fun hideMsg() {
        mTvMsg!!.visibility = GONE
    }

    fun showNotify() {
        setTvVisiable(mTvNotify)
    }

    fun hideNotify() {
        mTvNotify!!.visibility = GONE
    }

    init {
        initView()
        val typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.NavigationBarLottieItem)
        initAttrs(typedArray) //初始化属性
        typedArray.recycle()
        setupConfig() //初始化相关操作
    }
}