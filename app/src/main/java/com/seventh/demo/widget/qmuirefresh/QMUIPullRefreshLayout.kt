/*
 * Tencent is pleased to support the icon_right_arrow source community by making QMUI_Android available.
 *
 * Copyright (C) 2017-2018 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the MIT License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.seventh.demo.widget.qmuirefresh

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.view.animation.OvershootInterpolator
import android.widget.AbsListView
import android.widget.Scroller
import androidx.annotation.Nullable
import androidx.core.view.MotionEventCompat
import androidx.core.view.NestedScrollingParent
import androidx.core.view.NestedScrollingParentHelper
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.seventh.demo.R
import com.seventh.demo.extension.*
import kotlin.math.abs


/**
 * 下拉刷新控件, 作为容器，下拉时会将子 View 下移, 并拉出 RefreshView（表示正在刷新的 View）
 *
 *  * 可通过继承并覆写 [.createRefreshView] 方法实现自己的 RefreshView
 *  * 可通过 [.setRefreshOffsetCalculator] 自己决定在下拉过程中 RefreshView 的位置
 *
 * @author cginechen
 * @date 2016-12-11
 */
class QMUIPullRefreshLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.QMUIPullRefreshLayoutStyle
) : ViewGroup(context, attrs, defStyleAttr), NestedScrollingParent {
    private val mNestedScrollingParentHelper: NestedScrollingParentHelper
    internal var mIsRefreshing = false
    var targetView: View? = null
    private var mIRefreshView: IRefreshView? = null
    private var mRefreshView: View? = null
    private var mRefreshZIndex = -1
    private val mSystemTouchSlop: Int
    private val mTouchSlop: Int
    private var mListener: OnPullListener? = null
    private var onPullRefreshListener: OnPullRefreshListener? = null
    private var simpleRefreshListener: SimpleRefreshListener? = null
    private var mChildScrollUpCallback: OnChildScrollUpCallback? = null

    /**
     * RefreshView的初始offset
     */
    var refreshInitOffset: Int = 0
        private set

    /**
     * 刷新时RefreshView的offset
     */
    var refreshEndOffset: Int = 0
        private set

    /**
     * RefreshView当前offset
     */
    private var mRefreshCurrentOffset: Int = 0

    /**
     * 是否自动根据RefreshView的高度计算RefreshView的初始位置
     */
    private var mAutoCalculateRefreshInitOffset = true

    /**
     * 是否自动根据TargetView在刷新时的位置计算RefreshView的结束位置
     */
    private var mAutoCalculateRefreshEndOffset = true

    /**
     * 自动让TargetView的刷新位置与RefreshView高度相等
     */
    private var mEqualTargetRefreshOffsetToRefreshViewHeight = false

    /**
     * 当拖拽超过超过mAutoScrollToRefreshMinOffset时，自动滚动到刷新位置并触发刷新
     * mAutoScrollToRefreshMinOffset == - 1表示要mAutoScrollToRefreshMinOffset>=mTargetRefreshOffset
     */
    private var mAutoScrollToRefreshMinOffset = -1

    /**
     * TargetView(ListView或者ScrollView等)的初始位置
     */
    var targetInitOffset: Int = 0
        private set

    /**
     * 下拉时 TargetView（ListView 或者 ScrollView 等）当前的位置。
     */
    private var mTargetCurrentOffset: Int = 0
    /**
     * 刷新时TargetView(ListView或者ScrollView等)的位置
     */
    /**
     * mDragRate下拉的阻率
     */
    private var mDragRate: Float = 0.toFloat()

    private var mTargetRefreshOffset: Int = 0
    private var mDisableNestScrollImpl = false
    private var mEnableOverPull = true
    private var mNestedScrollInProgress: Boolean = false
    private var mActivePointerId = INVALID_POINTER
    var isDragging: Boolean = false
        private set
    private var mInitialDownY: Float = 0.toFloat()
    private var mInitialDownX: Float = 0.toFloat()
    private var mInitialMotionY: Float = 0.toFloat()
    private var mLastMotionY: Float = 0.toFloat()
    private var mRefreshOffsetCalculator: RefreshOffsetCalculator? = null
    private var mVelocityTracker: VelocityTracker? = null
    private val mMaxVelocity: Float
    private val mMiniVelocity: Float
    private val mScroller: Scroller
    private var mScrollFlag = 0
    private var mNestScrollDurationRefreshing = false
    private var mPendingRefreshDirectlyAction: Runnable? = null
    private var mSafeDisallowInterceptTouchEvent = false
    private var isRealCancel = false

    protected val scrollerFriction: Float
        get() = ViewConfiguration.getScrollFriction()

    var targetRefreshOffset: Int
        get() = mTargetRefreshOffset
        set(targetRefreshOffset) {
            mEqualTargetRefreshOffsetToRefreshViewHeight = false
            mTargetRefreshOffset = targetRefreshOffset
        }

    init {
        setWillNotDraw(false)

        val vc = ViewConfiguration.get(context)
        mMaxVelocity = vc.scaledMaximumFlingVelocity.toFloat()
        mMiniVelocity = vc.scaledMinimumFlingVelocity.toFloat()
        mSystemTouchSlop = vc.scaledTouchSlop
        //系统的值是8dp,如何配置？
        mTouchSlop = context.px2dp(mSystemTouchSlop.toFloat())

        mScroller = Scroller(getContext(), OvershootInterpolator())
        mScroller.setFriction(scrollerFriction)

        addRefreshView()
        ViewCompat.setChildrenDrawingOrderEnabled(this, true)

        mNestedScrollingParentHelper = NestedScrollingParentHelper(this)

        val array = context.obtainStyledAttributes(
            attrs,
            R.styleable.QMUIPullRefreshLayout, defStyleAttr, 0
        )

        try {
            mDragRate =
                array.getFloat(R.styleable.QMUIPullRefreshLayout_qmui_refresh_drag_rate, 0.65f)
            refreshInitOffset = array.getDimensionPixelSize(
                R.styleable.QMUIPullRefreshLayout_qmui_refresh_init_offset, Integer.MIN_VALUE
            )
            refreshEndOffset = array.getDimensionPixelSize(
                R.styleable.QMUIPullRefreshLayout_qmui_refresh_end_offset, Integer.MIN_VALUE
            )
            targetInitOffset = array.getDimensionPixelSize(
                R.styleable.QMUIPullRefreshLayout_qmui_target_init_offset, 0
            )
            mTargetRefreshOffset = array.getDimensionPixelSize(
                R.styleable.QMUIPullRefreshLayout_qmui_target_refresh_offset,
                context.dp2px(72f)
            )
            mAutoCalculateRefreshInitOffset =
                refreshInitOffset == Integer.MIN_VALUE || array.getBoolean(
                    R.styleable.QMUIPullRefreshLayout_qmui_auto_calculate_refresh_init_offset,
                    false
                )
            mAutoCalculateRefreshEndOffset =
                refreshEndOffset == Integer.MIN_VALUE || array.getBoolean(
                    R.styleable.QMUIPullRefreshLayout_qmui_auto_calculate_refresh_end_offset,
                    false
                )
            mEqualTargetRefreshOffsetToRefreshViewHeight = array.getBoolean(
                R.styleable.QMUIPullRefreshLayout_qmui_equal_target_refresh_offset_to_refresh_view_height,
                false
            )
        } finally {
            array.recycle()
        }
        mRefreshCurrentOffset = refreshInitOffset
        mTargetCurrentOffset = targetInitOffset
    }

    fun setOnPullListener(listener: OnPullListener) {
        this.mListener = listener
    }

    fun setOnPullRefreshListener(listener: OnPullRefreshListener) {
        this.onPullRefreshListener = listener
    }

    fun setRefreshListener(simpleRefreshListener: SimpleRefreshListener) {
        this.simpleRefreshListener = simpleRefreshListener
    }

    fun setDisableNestScrollImpl(disableNestScrollImpl: Boolean) {
        mDisableNestScrollImpl = disableNestScrollImpl
    }

    fun setDragRate(dragRate: Float) {
        // have no idea to change drag rate for nest scroll
        mDisableNestScrollImpl = true
        mDragRate = dragRate
    }

    fun setChildScrollUpCallback(childScrollUpCallback: OnChildScrollUpCallback) {
        mChildScrollUpCallback = childScrollUpCallback
    }

    fun setAutoScrollToRefreshMinOffset(autoScrollToRefreshMinOffset: Int) {
        mAutoScrollToRefreshMinOffset = autoScrollToRefreshMinOffset
    }

    /**
     * 覆盖该方法以实现自己的 RefreshView。
     *
     * @return 自定义的 RefreshView, 注意该 View 必须实现 [IRefreshView] 接口
     */
    protected fun createRefreshView(): View {
        return CommonRefreshView(context)
    }

    private fun addRefreshView() {
        if (mRefreshView == null) {
            mRefreshView = createRefreshView()
        }
        if (mRefreshView !is IRefreshView) {
            throw RuntimeException("refreshView must be a instance of IRefreshView")
        }
        mIRefreshView = mRefreshView as IRefreshView?
        if (mRefreshView?.layoutParams == null) {
            mRefreshView?.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        }
        addView(mRefreshView)
    }

    /**
     * 设置在下拉过程中 RefreshView 的偏移量
     */
    fun setRefreshOffsetCalculator(refreshOffsetCalculator: RefreshOffsetCalculator) {
        mRefreshOffsetCalculator = refreshOffsetCalculator
    }

    override fun getChildDrawingOrder(childCount: Int, i: Int): Int {
        if (mRefreshZIndex < 0) {
            return i
        }
        // 最后才绘制mRefreshView
        if (i == mRefreshZIndex) {
            return childCount - 1
        }
        return if (i > mRefreshZIndex) {
            i - 1
        } else i
    }

    /**
     * child view call, to ensure disallowInterceptTouchEvent make sense
     *
     *
     * how to optimize this...
     */
    fun openSafeDisallowInterceptTouchEvent() {
        mSafeDisallowInterceptTouchEvent = true
    }

    override fun requestDisallowInterceptTouchEvent(b: Boolean) {

        if (mSafeDisallowInterceptTouchEvent) {
            super.requestDisallowInterceptTouchEvent(b)
            mSafeDisallowInterceptTouchEvent = false
        }

        // if this is a List < L or another view that doesn't support nested
        // scrolling, ignore this request so that the vertical scroll event
        // isn't stolen

        if (Build.VERSION.SDK_INT < 21 && targetView is AbsListView || targetView != null && !ViewCompat.isNestedScrollingEnabled(
                targetView!!
            )
        ) {
            // Nope.
        } else {
            super.requestDisallowInterceptTouchEvent(b)
        }
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        ensureTargetView()
        if (targetView == null) {
            Log.d(TAG, "onMeasure: mTargetView == null")
            return
        }
        val targetMeasureWidthSpec = View.MeasureSpec.makeMeasureSpec(
            measuredWidth - paddingLeft - paddingRight, View.MeasureSpec.EXACTLY
        )
        val targetMeasureHeightSpec = View.MeasureSpec.makeMeasureSpec(
            measuredHeight - paddingTop - paddingBottom, View.MeasureSpec.EXACTLY
        )
        targetView?.measure(targetMeasureWidthSpec, targetMeasureHeightSpec)
        measureChild(mRefreshView, widthMeasureSpec, heightMeasureSpec)
        mRefreshZIndex = -1
        for (i in 0 until childCount) {
            if (getChildAt(i) === mRefreshView) {
                mRefreshZIndex = i
                break
            }
        }

        val refreshViewHeight = mRefreshView?.measuredHeight.toNonNullInt()
        if (mAutoCalculateRefreshInitOffset) {
            if (refreshInitOffset != -refreshViewHeight) {
                refreshInitOffset = -refreshViewHeight
                mRefreshCurrentOffset = refreshInitOffset
            }

        }
        if (mEqualTargetRefreshOffsetToRefreshViewHeight) {
            mTargetRefreshOffset = refreshViewHeight
        }
        if (mAutoCalculateRefreshEndOffset) {
            refreshEndOffset = (mTargetRefreshOffset - refreshViewHeight) / 2
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val width = measuredWidth
        val height = measuredHeight
        if (childCount == 0) {
            return
        }
        ensureTargetView()
        if (targetView == null) {
            Log.d(TAG, "onLayout: mTargetView == null")
            return
        }

        val childLeft = paddingLeft
        val childTop = paddingTop
        val childWidth = width - paddingLeft - paddingRight
        val childHeight = height - paddingTop - paddingBottom
        targetView?.layout(
            childLeft, childTop + mTargetCurrentOffset,
            childLeft + childWidth, childTop + childHeight + mTargetCurrentOffset
        )
        val refreshViewWidth = mRefreshView?.measuredWidth.toNonNullInt()
        val refreshViewHeight = mRefreshView?.measuredHeight.toNonNullInt()
        mRefreshView?.layout(
            width / 2 - refreshViewWidth / 2, mRefreshCurrentOffset,
            width / 2 + refreshViewWidth / 2, mRefreshCurrentOffset + refreshViewHeight
        )
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        ensureTargetView()

        val action = ev.action
        val pointerIndex: Int

        if (!isEnabled || canChildScrollUp() || mNestedScrollInProgress) {
            return false
        }
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                isDragging = false
                mActivePointerId = ev.getPointerId(0)
                pointerIndex = ev.findPointerIndex(mActivePointerId)
                if (pointerIndex < 0) {
                    return false
                }
                mInitialDownX = ev.getX(pointerIndex)
                mInitialDownY = ev.getY(pointerIndex)
            }

            MotionEvent.ACTION_MOVE -> {
                pointerIndex = ev.findPointerIndex(mActivePointerId)
                if (pointerIndex < 0) {
                    Log.e(TAG, "Got ACTION_MOVE event but have an invalid active pointer id.")
                    return false
                }

                val x = ev.getX(pointerIndex)
                val y = ev.getY(pointerIndex)
                startDragging(x, y)
            }

            MotionEvent.ACTION_POINTER_UP -> onSecondaryPointerUp(ev)

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isDragging = false
                mActivePointerId = INVALID_POINTER
            }
            else -> {
            }
        }

        return isDragging
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        val action = ev.action
        val pointerIndex: Int

        if (!isEnabled || canChildScrollUp() || mNestedScrollInProgress) {
            Log.d(
                TAG, "fast end onTouchEvent: isEnabled = " + isEnabled + "; canChildScrollUp = "
                        + canChildScrollUp() + " ; mNestedScrollInProgress = " + mNestedScrollInProgress
            )
            return false
        }

        acquireVelocityTracker(ev)

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                isDragging = false
                mScrollFlag = 0
                if (!mScroller.isFinished) {
                    mScroller.abortAnimation()
                }
                mActivePointerId = ev.getPointerId(0)
            }

            MotionEvent.ACTION_MOVE -> {
                pointerIndex = ev.findPointerIndex(mActivePointerId)
                if (pointerIndex < 0) {
                    Log.e(
                        TAG,
                        "onTouchEvent Got ACTION_MOVE event but have an invalid active pointer id."
                    )
                    return false
                }
                val x = ev.getX(pointerIndex)
                val y = ev.getY(pointerIndex)
                startDragging(x, y)

                if (isDragging) {
                    val dy = (y - mLastMotionY) * mDragRate
                    if (dy >= 0) {
                        isRealCancel = true
                        moveTargetView(dy, true)
                    } else {
                        val move = moveTargetView(dy, true)
                        val delta = Math.abs(dy) - Math.abs(move)
                        if (delta > 0) {
                            // 重新dispatch一次down事件，使得列表可以继续滚动
                            ev.action = MotionEvent.ACTION_DOWN
                            // 立刻dispatch一个大于mSystemTouchSlop的move事件，防止触发TargetView
                            var offsetLoc = (mSystemTouchSlop + 1).toFloat()
                            if (delta > offsetLoc) {
                                offsetLoc = delta
                            }
                            ev.offsetLocation(0f, offsetLoc)
                            super.dispatchTouchEvent(ev)
                            ev.action = action
                            // 再dispatch一次move事件，消耗掉所有dy
                            ev.offsetLocation(0f, -offsetLoc)
                            super.dispatchTouchEvent(ev)
                        }
                    }
                    mLastMotionY = y
                }
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                pointerIndex = ev.actionIndex
                if (pointerIndex < 0) {
                    Log.e(TAG, "Got ACTION_POINTER_DOWN event but have an invalid action index.")
                    return false
                }
                mActivePointerId = ev.getPointerId(pointerIndex)
            }

            MotionEvent.ACTION_POINTER_UP -> onSecondaryPointerUp(ev)

            MotionEvent.ACTION_UP -> {
                pointerIndex = ev.findPointerIndex(mActivePointerId)
                if (pointerIndex < 0) {
                    Log.e(TAG, "Got ACTION_UP event but don't have an active pointer id.")
                    return false
                }

                if (isDragging) {
                    isRealCancel = true
                    isDragging = false
                    mVelocityTracker?.computeCurrentVelocity(1000, mMaxVelocity)
                    var vy = mVelocityTracker?.getYVelocity(mActivePointerId).toNonNullFloat()
                    if (abs(vy) < mMiniVelocity) {
                        vy = 0f
                    }
                    finishPull(vy.toInt())
                }
                mActivePointerId = INVALID_POINTER
                releaseVelocityTracker()
                return false
            }
            MotionEvent.ACTION_CANCEL -> {
                releaseVelocityTracker()
                return false
            }
            else -> {
            }
        }

        return true
    }

    private fun ensureTargetView() {
        if (targetView == null) {
            for (i in 0 until childCount) {
                val view = getChildAt(i)
                if (view != mRefreshView) {
                    onSureTargetView(view)
                    targetView = view
                    break
                }
            }
        }
        if (targetView != null && mPendingRefreshDirectlyAction != null) {
            val runnable = mPendingRefreshDirectlyAction
            mPendingRefreshDirectlyAction = null
            runnable?.run()
        }
    }

    /**
     * 确定TargetView, 提供机会给子类来做一些初始化的操作
     */
    protected fun onSureTargetView(targetView: View) {

    }

    protected fun onFinishPull(
        vy: Int, refreshInitOffset: Int, refreshEndOffset: Int, refreshViewHeight: Int,
        targetCurrentOffset: Int, targetInitOffset: Int, targetRefreshOffset: Int
    ) {

    }

    private fun finishPull(vy: Int) {
        info(
            "finishPull: vy = " + vy + " ; mTargetCurrentOffset = " + mTargetCurrentOffset +
                    " ; mTargetRefreshOffset = " + mTargetRefreshOffset + " ; mTargetInitOffset = " + targetInitOffset +
                    " ; mScroller.isFinished() = " + mScroller.isFinished
        )
        val miniVy = vy / 1000 // 向下拖拽时， 速度不能太大
        onFinishPull(
            miniVy, refreshInitOffset, refreshEndOffset, mRefreshView?.height.toNonNullInt(),
            mTargetCurrentOffset, targetInitOffset, mTargetRefreshOffset
        )
        if (mTargetCurrentOffset >= mTargetRefreshOffset) {
            isRealCancel = false
            if (miniVy > 0) {
                mScrollFlag = FLAG_NEED_SCROLL_TO_REFRESH_POSITION or FLAG_NEED_DO_REFRESH
                mScroller.fling(
                    0, mTargetCurrentOffset, 0, miniVy,
                    0, 0, targetInitOffset, Integer.MAX_VALUE
                )
                invalidate()
            } else if (miniVy < 0) {
                mScroller.fling(
                    0, mTargetCurrentOffset, 0, vy,
                    0, 0, Integer.MIN_VALUE, Integer.MAX_VALUE
                )
                if (mScroller.finalY < targetInitOffset) {
                    mScrollFlag = FLAG_NEED_DELIVER_VELOCITY
                } else if (mScroller.finalY < mTargetRefreshOffset) {
                    val dy = targetInitOffset - mTargetCurrentOffset
                    mScroller.startScroll(0, mTargetCurrentOffset, 0, dy)
                } else if (mScroller.finalY == mTargetRefreshOffset) {
                    mScrollFlag = FLAG_NEED_DO_REFRESH
                } else {
                    mScroller.startScroll(
                        0,
                        mTargetCurrentOffset,
                        0,
                        mTargetRefreshOffset - mTargetCurrentOffset
                    )
                    mScrollFlag = FLAG_NEED_DO_REFRESH
                }
                invalidate()
            } else {
                if (mTargetCurrentOffset > mTargetRefreshOffset) {
                    mScroller.startScroll(
                        0,
                        mTargetCurrentOffset,
                        0,
                        mTargetRefreshOffset - mTargetCurrentOffset
                    )
                }
                mScrollFlag = FLAG_NEED_DO_REFRESH
                invalidate()
            }
        } else {
            if (miniVy > 0) {
                mScroller.fling(
                    0,
                    mTargetCurrentOffset,
                    0,
                    miniVy,
                    0,
                    0,
                    targetInitOffset,
                    Integer.MAX_VALUE
                )
                if (mScroller.finalY > mTargetRefreshOffset) {
                    mScrollFlag = FLAG_NEED_SCROLL_TO_REFRESH_POSITION or FLAG_NEED_DO_REFRESH
                } else if (mAutoScrollToRefreshMinOffset >= 0 && mScroller.finalY > mAutoScrollToRefreshMinOffset) {
                    mScroller.startScroll(
                        0,
                        mTargetCurrentOffset,
                        0,
                        mTargetRefreshOffset - mTargetCurrentOffset
                    )
                    mScrollFlag = FLAG_NEED_DO_REFRESH
                } else {
                    mScrollFlag = FLAG_NEED_SCROLL_TO_INIT_POSITION
                }
                invalidate()
            } else if (miniVy < 0) {
                mScrollFlag = 0
                mScroller.fling(
                    0,
                    mTargetCurrentOffset,
                    0,
                    vy,
                    0,
                    0,
                    Integer.MIN_VALUE,
                    Integer.MAX_VALUE
                )
                if (mScroller.finalY < targetInitOffset) {
                    mScrollFlag = FLAG_NEED_DELIVER_VELOCITY
                } else {
                    mScroller.startScroll(
                        0,
                        mTargetCurrentOffset,
                        0,
                        targetInitOffset - mTargetCurrentOffset
                    )
                    mScrollFlag = 0
                }
                invalidate()
            } else {
                if (mTargetCurrentOffset == targetInitOffset) {
                    return
                }
                if (mAutoScrollToRefreshMinOffset >= 0 && mTargetCurrentOffset >= mAutoScrollToRefreshMinOffset) {
                    mScroller.startScroll(
                        0,
                        mTargetCurrentOffset,
                        0,
                        mTargetRefreshOffset - mTargetCurrentOffset
                    )
                    mScrollFlag = FLAG_NEED_DO_REFRESH
                } else {
                    mScroller.startScroll(
                        0,
                        mTargetCurrentOffset,
                        0,
                        targetInitOffset - mTargetCurrentOffset
                    )
                    mScrollFlag = 0
                }
                invalidate()
            }
        }
    }

    /**
     * 全量触发下拉刷新
     */
    protected fun onRefresh() {
        if (mIsRefreshing) {
            return
        }
        mIsRefreshing = true
        mIRefreshView?.doRefresh()
        mListener?.onRefresh()
        onPullRefreshListener?.onRefresh()
        simpleRefreshListener?.onRefresh()
    }

    fun finishRefresh() {
        mIsRefreshing = false
        mIRefreshView?.stop()
        mScrollFlag = FLAG_NEED_SCROLL_TO_INIT_POSITION
        mScroller.forceFinished(true)
        invalidate()
    }


    /**
     * 延时主动触发下拉刷新
     * @param delay
     */
    @JvmOverloads
    fun setToRefreshDirectly(delay: Long = 0) {
        if (targetView != null) {
            postDelayed({
                setTargetViewToTop(targetView!!)
                onRefresh()
                mScrollFlag = FLAG_NEED_SCROLL_TO_REFRESH_POSITION
                invalidate()
            }, delay)

        } else {
            mPendingRefreshDirectlyAction.run { setToRefreshDirectly(delay) }
        }
    }


    fun setEnableOverPull(enableOverPull: Boolean) {
        mEnableOverPull = enableOverPull
    }

    protected fun setTargetViewToTop(targetView: View) {
        if (targetView is RecyclerView) {
            targetView.scrollToPosition(0)
        } else if (targetView is AbsListView) {
            val listView = targetView
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                listView.setSelectionFromTop(0, 0)
            } else {
                listView.setSelection(0)
            }
        } else {
            targetView.scrollTo(0, 0)
        }
    }


    private fun onSecondaryPointerUp(ev: MotionEvent) {
        val pointerIndex = MotionEventCompat.getActionIndex(ev)
        val pointerId = ev.getPointerId(pointerIndex)
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            val newPointerIndex = if (pointerIndex == 0) 1 else 0
            mActivePointerId = ev.getPointerId(newPointerIndex)
        }
    }

    fun reset() {
        moveTargetViewTo(targetInitOffset, false)
        mIRefreshView?.stop()
        mIsRefreshing = false
        mScroller.forceFinished(true)
        mScrollFlag = 0
    }

    protected fun startDragging(x: Float, y: Float) {
        val dx = x - mInitialDownX
        val dy = y - mInitialDownY
        val isYDrag = isYDrag(dx, dy)
        if (isYDrag && (dy > mTouchSlop || dy < -mTouchSlop && mTargetCurrentOffset > targetInitOffset) && !isDragging) {
            mInitialMotionY = mInitialDownY + mTouchSlop
            mLastMotionY = mInitialMotionY
            isDragging = true
        }
    }

    protected fun isYDrag(dx: Float, dy: Float): Boolean {
        return Math.abs(dy) > Math.abs(dx)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        reset()
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        if (!enabled) {
            reset()
            invalidate()
        }
    }

    fun canChildScrollUp(): Boolean {
        return if (mChildScrollUpCallback != null) {
            mChildScrollUpCallback?.canChildScrollUp(this, targetView).toNonNullBoolean()
        } else defaultCanScrollUp(targetView)
    }

    override fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int): Boolean {
        info("onStartNestedScroll: nestedScrollAxes = $nestedScrollAxes")
        return !mDisableNestScrollImpl && isEnabled && nestedScrollAxes and ViewCompat.SCROLL_AXIS_VERTICAL != 0
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int) {
        info("onNestedScrollAccepted: axes = $axes")
        mScroller.abortAnimation()
        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes)
        mNestedScrollInProgress = true
        isDragging = true
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
        info("onNestedPreScroll: dx = $dx ; dy = $dy")
        val parentCanConsume = mTargetCurrentOffset - targetInitOffset
        if (dy > 0 && parentCanConsume > 0) {
            if (dy >= parentCanConsume) {
                consumed[1] = parentCanConsume
                moveTargetViewTo(targetInitOffset, true)
            } else {
                consumed[1] = dy
                moveTargetView((-dy).toFloat(), true)
            }
        }
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int
    ) {
        info(
            "onNestedScroll: dxConsumed = " + dxConsumed + " ; dyConsumed = " + dyConsumed +
                    " ; dxUnconsumed = " + dxUnconsumed + " ; dyUnconsumed = " + dyUnconsumed
        )
        if (dyUnconsumed < 0 && !canChildScrollUp() && mScroller.isFinished && mScrollFlag == 0) {
            moveTargetView((-dyUnconsumed).toFloat(), true)
        }
    }

    override fun getNestedScrollAxes(): Int {
        return mNestedScrollingParentHelper.nestedScrollAxes
    }

    override fun onStopNestedScroll(child: View) {
        info("onStopNestedScroll: mNestedScrollInProgress = $mNestedScrollInProgress")
        mNestedScrollingParentHelper.onStopNestedScroll(child)
        if (mNestedScrollInProgress) {
            mNestedScrollInProgress = false
            isDragging = false
            if (!mNestScrollDurationRefreshing) {
                finishPull(0)
            }

        }
    }

    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
        info(
            "onNestedPreFling: mTargetCurrentOffset = " + mTargetCurrentOffset +
                    " ; velocityX = " + velocityX + " ; velocityY = " + velocityY
        )
        if (mTargetCurrentOffset > targetInitOffset) {
            mNestedScrollInProgress = false
            isDragging = false
            if (!mNestScrollDurationRefreshing) {
                finishPull((-velocityY).toInt())
            }
            return true
        }
        return false
    }

    override fun onNestedFling(
        target: View,
        velocityX: Float,
        velocityY: Float,
        consumed: Boolean
    ): Boolean {
        try {
            return super.onNestedFling(target, velocityX, velocityY, consumed)
        } catch (e: Throwable) {
            // android 24及以上ViewGroup会继续往上派发， 23以及以下直接返回false
            // 低于5.0的机器和RecyclerView配合工作时，部分机型会调用这个方法，但是ViewGroup并没有实现这个方法，会报错，这里catch一下
            e.printStackTrace()
        }

        return false
    }

    private fun moveTargetView(dy: Float, isDragging: Boolean): Int {
        val target = (mTargetCurrentOffset + dy).toInt()
        return moveTargetViewTo(target, isDragging)
    }

    private fun moveTargetViewTo(
        target: Int,
        isDragging: Boolean,
        calculateAnyWay: Boolean = false
    ): Int {
        var target = target
        target =
            calculateTargetOffset(target, targetInitOffset, mTargetRefreshOffset, mEnableOverPull)
        var offset = 0
        if (target != mTargetCurrentOffset || calculateAnyWay) {
            offset = target - mTargetCurrentOffset
            ViewCompat.offsetTopAndBottom(targetView!!, offset)
            mTargetCurrentOffset = target
            val total = mTargetRefreshOffset - targetInitOffset
            if (isDragging) {
                mIRefreshView?.onPull(
                    Math.min(mTargetCurrentOffset - targetInitOffset, total), total,
                    mTargetCurrentOffset - mTargetRefreshOffset
                )
                mListener?.onPull(
                    Math.min(mTargetCurrentOffset - targetInitOffset, total), total,
                    mTargetCurrentOffset - mTargetRefreshOffset
                )
                onPullRefreshListener?.onPull(
                    Math.min(mTargetCurrentOffset - targetInitOffset, total), total,
                    mTargetCurrentOffset - mTargetRefreshOffset
                )
            }
            onMoveTargetView(mTargetCurrentOffset)
            mListener?.onMoveTarget(mTargetCurrentOffset)

            //默认是下拉跟随样式
            if (mRefreshOffsetCalculator == null) {
                mRefreshOffsetCalculator = QMUIFollowRefreshOffsetCalculator()
            }
            val newRefreshOffset = mRefreshOffsetCalculator?.calculateRefreshOffset(
                refreshInitOffset, refreshEndOffset, mRefreshView?.height.toNonNullInt(),
                mTargetCurrentOffset, targetInitOffset, mTargetRefreshOffset
            )
            if (newRefreshOffset != mRefreshCurrentOffset) {
                ViewCompat.offsetTopAndBottom(
                    mRefreshView!!,
                    newRefreshOffset.toNonNullInt() - mRefreshCurrentOffset
                )
                mRefreshCurrentOffset = newRefreshOffset.toNonNullInt()
                onMoveRefreshView(mRefreshCurrentOffset)
                mListener?.onMoveRefreshView(mRefreshCurrentOffset)
            }
        }
        return offset
    }

    protected fun calculateTargetOffset(
        target: Int,
        targetInitOffset: Int,
        targetRefreshOffset: Int,
        enableOverPull: Boolean
    ): Int {
        var target = target
        target = Math.max(target, targetInitOffset)
        if (!enableOverPull) {
            target = Math.min(target, targetRefreshOffset)
        }
        return target
    }

    private fun acquireVelocityTracker(event: MotionEvent) {
        if (null == mVelocityTracker) {
            mVelocityTracker = VelocityTracker.obtain()
        }
        mVelocityTracker?.addMovement(event)
    }

    private fun releaseVelocityTracker() {
        mVelocityTracker?.clear()
        mVelocityTracker?.recycle()
        mVelocityTracker = null
        //无效下拉
        if (isRealCancel) {
            onPullRefreshListener?.cancelPull()
            mIRefreshView?.onCancel()
        }
    }

    protected fun onMoveTargetView(offset: Int) {

    }

    protected fun onMoveRefreshView(offset: Int) {

    }


    private fun hasFlag(flag: Int): Boolean {
        return mScrollFlag and flag == flag
    }

    private fun removeFlag(flag: Int) {
        mScrollFlag = mScrollFlag and flag.inv()
    }

    override fun computeScroll() {
        if (mScroller.computeScrollOffset()) {
            val offsetY = mScroller.currY
            moveTargetViewTo(offsetY, false)
            if (offsetY <= 0 && hasFlag(FLAG_NEED_DELIVER_VELOCITY)) {
                deliverVelocity()
                mScroller.forceFinished(true)
            }
            invalidate()
        } else if (hasFlag(FLAG_NEED_SCROLL_TO_INIT_POSITION)) {
            removeFlag(FLAG_NEED_SCROLL_TO_INIT_POSITION)
            if (mTargetCurrentOffset != targetInitOffset) {
                mScroller.startScroll(
                    0,
                    mTargetCurrentOffset,
                    0,
                    targetInitOffset - mTargetCurrentOffset
                )
            }
            invalidate()
        } else if (hasFlag(FLAG_NEED_SCROLL_TO_REFRESH_POSITION)) {
            removeFlag(FLAG_NEED_SCROLL_TO_REFRESH_POSITION)
            if (mTargetCurrentOffset != mTargetRefreshOffset) {
                mScroller.startScroll(
                    0,
                    mTargetCurrentOffset,
                    0,
                    mTargetRefreshOffset - mTargetCurrentOffset
                )
            } else {
                moveTargetViewTo(mTargetRefreshOffset, isDragging = false, calculateAnyWay = true)
            }
            invalidate()
        } else if (hasFlag(FLAG_NEED_DO_REFRESH)) {
            removeFlag(FLAG_NEED_DO_REFRESH)
            onRefresh()
            moveTargetViewTo(mTargetRefreshOffset, isDragging = false, calculateAnyWay = true)
        } else {
            deliverVelocity()
        }
    }

    private fun deliverVelocity() {
        if (hasFlag(FLAG_NEED_DELIVER_VELOCITY)) {
            removeFlag(FLAG_NEED_DELIVER_VELOCITY)
            if (mScroller.currVelocity > mMiniVelocity) {
                info("deliver velocity: " + mScroller.currVelocity)
                // if there is a velocity, pass it on
                if (targetView is RecyclerView) {
                    (targetView as RecyclerView).fling(0, mScroller.currVelocity.toInt())
                } else if (targetView is AbsListView && Build.VERSION.SDK_INT >= 21) {
                    (targetView as AbsListView).fling(mScroller.currVelocity.toInt())
                }
            }
        }
    }

    private fun info(msg: String) {
        Log.i(TAG, msg)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val action = ev.action
        if (action == MotionEvent.ACTION_DOWN) {
            mNestScrollDurationRefreshing =
                mIsRefreshing || mScrollFlag and FLAG_NEED_DO_REFRESH != 0
        } else if (mNestScrollDurationRefreshing) {
            if (action == MotionEvent.ACTION_MOVE) {
                if (!mIsRefreshing && mScroller.isFinished && mScrollFlag == 0) {
                    // 这里必须要 dispatch 一次 down 事件，否则不能触发 NestScroll，具体可参考 RecyclerView
                    // down 过程中会触发 onStopNestedScroll，mNestScrollDurationRefreshing 必须在之后
                    // 置为false，否则会触发 finishPull
                    ev.offsetLocation(0f, (-mSystemTouchSlop - 1).toFloat())
                    ev.action = MotionEvent.ACTION_DOWN
                    super.dispatchTouchEvent(ev)
                    mNestScrollDurationRefreshing = false
                    ev.action = action
                    // offset touch slop, 避免触发点击事件
                    ev.offsetLocation(0f, (mSystemTouchSlop + 1).toFloat())
                }
            } else {
                mNestScrollDurationRefreshing = false
            }
        }

        return super.dispatchTouchEvent(ev)
    }

    interface OnPullListener {

        fun onMoveTarget(offset: Int)

        fun onMoveRefreshView(offset: Int)

        fun onPull(offset: Int, total: Int, overPull: Int)

        fun onRefresh()
    }

    interface OnPullRefreshListener {

        fun cancelPull()

        fun onPull(offset: Int, total: Int, overPull: Int)

        fun onRefresh()
    }

    interface SimpleRefreshListener {
        fun onRefresh()
    }


    interface OnChildScrollUpCallback {
        fun canChildScrollUp(parent: QMUIPullRefreshLayout, @Nullable child: View?): Boolean
    }

    interface RefreshOffsetCalculator {

        /**
         * 通过 targetView 的当前位置、targetView 的初始和刷新位置以及 refreshView 的初始与结束位置计算 RefreshView 的位置。
         *
         * @param refreshInitOffset   RefreshView 的初始 offset。
         * @param refreshEndOffset    刷新时 RefreshView 的 offset。
         * @param refreshViewHeight   RerfreshView 的高度
         * @param targetCurrentOffset 下拉时 TargetView（ListView 或者 ScrollView 等）当前的位置。
         * @param targetInitOffset    TargetView（ListView 或者 ScrollView 等）的初始位置。
         * @param targetRefreshOffset 刷新时 TargetView（ListView 或者 ScrollView等）的位置。
         * @return RefreshView 当前的位置。
         */
        fun calculateRefreshOffset(
            refreshInitOffset: Int, refreshEndOffset: Int, refreshViewHeight: Int,
            targetCurrentOffset: Int, targetInitOffset: Int, targetRefreshOffset: Int
        ): Int
    }

    interface IRefreshView {
        fun stop()

        fun doRefresh()

        fun onPull(offset: Int, total: Int, overPull: Int)

        fun onCancel()
    }

    companion object {

        private const val TAG = "QMUIPullRefreshLayout"
        private const val INVALID_POINTER = -1
        private const val FLAG_NEED_SCROLL_TO_INIT_POSITION = 1
        private const val FLAG_NEED_SCROLL_TO_REFRESH_POSITION = 1 shl 1
        private const val FLAG_NEED_DO_REFRESH = 1 shl 2
        private const val FLAG_NEED_DELIVER_VELOCITY = 1 shl 3

        fun defaultCanScrollUp(view: View?): Boolean {
            if (view == null) {
                return false
            }
            if (Build.VERSION.SDK_INT < 14) {
                if (view is AbsListView) {
                    val absListView = view as AbsListView?
                    return absListView?.childCount.toNonNullInt() > 0 && (absListView?.firstVisiblePosition.toNonNullInt() > 0 || absListView?.getChildAt(
                        0
                    )?.top.toNonNullInt() < absListView?.paddingTop.toNonNullInt())
                } else {
                    return ViewCompat.canScrollVertically(view, -1) || view.scrollY > 0
                }
            } else {
                return ViewCompat.canScrollVertically(view, -1)
            }
        }
    }

}