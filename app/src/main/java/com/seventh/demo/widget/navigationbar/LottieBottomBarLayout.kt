package com.seventh.demo.widget.navigationbar

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.seventh.demo.R

class LottieBottomBarLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private var mViewPager: ViewPager2? = null
    private var mChildCount //子条目个数
            = 0
    private val mItemViews: MutableList<LottieBarItem> = ArrayList()
    private var mCurrentItem //当前条目的索引
            = 0
    private var mSmoothScroll: Boolean
    override fun onFinishInflate() {
        super.onFinishInflate()
        notifyChildViewsUpdate()
    }

    override fun setOrientation(orientation: Int) {
        super.setOrientation(orientation)
    }

    fun setViewPager(viewPager: ViewPager2?) {
        mViewPager = viewPager
        notifyChildViewsUpdate()
    }

    fun notifyChildViewsUpdate() {
        mChildCount = childCount
        if (mChildCount == 0) {
            return
        }
        if (mViewPager != null) {
            require(mViewPager!!.adapter!!.itemCount == mChildCount) { "LinearLayout的子View数量必须和ViewPager条目数量一致" }
        }
        for (i in 0 until mChildCount) {
            if (getChildAt(i) is LottieBarItem) {
                val lottieBarItem = getChildAt(i) as LottieBarItem
                mItemViews.add(lottieBarItem)
                //设置点击监听
                lottieBarItem.setOnClickListener(MyOnClickListener(i))
            } else {
                throw IllegalArgumentException("BottomBarLayout的子View必须是BottomBarItem")
            }
        }
        mItemViews[mCurrentItem].setStatus(true) //设置选中项
        if (mViewPager != null) {
            mViewPager!!.registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                }

                override fun onPageSelected(position: Int) {
                    if (position >= mItemViews.size) return
                    resetState()
                    mItemViews[position].setStatus(true)
                    if (onItemSelectedListener != null) {
                        onItemSelectedListener!!.onItemSelected(
                            getBottomItem(position),
                            mCurrentItem,
                            position
                        )
                    }
                    mCurrentItem = position //记录当前位置
                }

                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                }
            })
        }
    }

    private inner class MyOnClickListener(private val currentIndex: Int) : OnClickListener {
        override fun onClick(v: View) {
            //回调点击的位置
            if (mViewPager != null) {
                //有设置viewPager
                if (currentIndex == mCurrentItem) {
                    //如果还是同个页签，使用setCurrentItem不会回调OnPageSelecte(),所以在此处需要回调点击监听
                    if (onItemSelectedListener != null) {
                        onItemSelectedListener!!.onItemSelected(
                            getBottomItem(currentIndex),
                            mCurrentItem,
                            currentIndex
                        )
                    }
                } else {
                    mViewPager!!.setCurrentItem(currentIndex, mSmoothScroll)
                }
            } else {
                //没有设置viewPager
                if (onItemSelectedListener != null) {
                    onItemSelectedListener!!.onItemSelected(
                        getBottomItem(currentIndex),
                        mCurrentItem,
                        currentIndex
                    )
                }
                updateTabState(currentIndex)
            }
        }
    }

    private fun updateTabState(position: Int) {
        if (position >= mItemViews.size) return
        resetState()
        mCurrentItem = position
        mItemViews[mCurrentItem].setStatus(true)
    }

    /**
     * 重置当前按钮的状态
     */
    private fun resetState() {
        if (mCurrentItem >= mItemViews.size) return
        mItemViews[mCurrentItem].setStatus(false)
    }

    /**
     * 设置未读数
     *
     * @param position  底部标签的下标
     * @param unreadNum 未读数
     */
    fun setUnread(position: Int, unreadNum: Int) {
        if (position >= mItemViews.size) return
        mItemViews[position].setUnreadNum(unreadNum)
    }

    /**
     * 设置提示消息
     *
     * @param position 底部标签的下标
     * @param msg      未读数
     */
    fun setMsg(position: Int, msg: String?) {
        if (position >= mItemViews.size) return
        mItemViews[position].setMsg(msg)
    }

    /**
     * 隐藏提示消息
     *
     * @param position 底部标签的下标
     */
    fun hideMsg(position: Int) {
        if (position >= mItemViews.size) return
        mItemViews[position].hideMsg()
    }

    /**
     * 显示提示的小红点
     *
     * @param position 底部标签的下标
     */
    fun showNotify(position: Int) {
        if (position >= mItemViews.size) return
        mItemViews[position].showNotify()
    }

    /**
     * 隐藏提示的小红点
     *
     * @param position 底部标签的下标
     */
    fun hideNotify(position: Int) {
        if (position >= mItemViews.size) return
        mItemViews[position].hideNotify()
    }

    var currentItem: Int
        get() = mCurrentItem
        set(currentItem) {
            if (mViewPager != null) {
                mViewPager!!.setCurrentItem(currentItem, mSmoothScroll)
            } else {
                updateTabState(currentItem)
            }
        }

    fun setSmoothScroll(smoothScroll: Boolean) {
        mSmoothScroll = smoothScroll
    }

    fun getBottomItem(position: Int): LottieBarItem {
        return mItemViews[position]
    }

    private var onItemSelectedListener: OnItemSelectedListener? = null

    interface OnItemSelectedListener {
        fun onItemSelected(
            bottomBarItem: LottieBarItem?,
            previousPosition: Int,
            currentPosition: Int
        )
    }

    fun setOnItemSelectedListener(onItemSelectedListener: OnItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener
    }

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.NavigationBarLottieLayout)
        mSmoothScroll = ta.getBoolean(
            R.styleable.NavigationBarLottieLayout_navigation_lottie_smoothScroll,
            false
        )
        ta.recycle()
    }
}