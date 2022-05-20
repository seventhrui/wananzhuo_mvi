package com.seventh.demo.widget.navigationbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.viewpager2.widget.ViewPager2;

import com.seventh.demo.R;

import java.util.ArrayList;
import java.util.List;

public class LottieBottomBarLayout extends LinearLayout {

    private ViewPager2 mViewPager;
    private int mChildCount;//子条目个数
    private List<LottieBarItem> mItemViews = new ArrayList<>();
    private int mCurrentItem;//当前条目的索引
    private boolean mSmoothScroll;

    public LottieBottomBarLayout(Context context) {
        this(context, null);
    }

    public LottieBottomBarLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LottieBottomBarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.NavigationBarLottieLayout);
        mSmoothScroll = ta.getBoolean(R.styleable.NavigationBarLottieLayout_navigation_lottie_smoothScroll, false);
        ta.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        notifyChildViewsUpdate();
    }

    @Override
    public void setOrientation(int orientation) {
        super.setOrientation(orientation);
    }

    public void setViewPager(ViewPager2 viewPager) {
        this.mViewPager = viewPager;
        notifyChildViewsUpdate();
    }

    public void notifyChildViewsUpdate() {
        mChildCount = getChildCount();
        if (mChildCount == 0) {
            return;
        }

        if (mViewPager != null) {
            if (mViewPager.getAdapter().getItemCount() != mChildCount) {
                throw new IllegalArgumentException("LinearLayout的子View数量必须和ViewPager条目数量一致");
            }
        }

        for (int i = 0; i < mChildCount; i++) {
            if (getChildAt(i) instanceof LottieBarItem) {
                LottieBarItem lottieBarItem = (LottieBarItem) getChildAt(i);
                mItemViews.add(lottieBarItem);
                //设置点击监听
                lottieBarItem.setOnClickListener(new MyOnClickListener(i));
            } else {
                throw new IllegalArgumentException("BottomBarLayout的子View必须是BottomBarItem");
            }
        }

        mItemViews.get(mCurrentItem).setStatus(true);//设置选中项

        if (mViewPager != null) {
            mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }

                @Override
                public void onPageSelected(int position) {
                    if (position >= mItemViews.size()) return;
                    resetState();
                    mItemViews.get(position).setStatus(true);
                    if (onItemSelectedListener != null) {
                        onItemSelectedListener.onItemSelected(getBottomItem(position), mCurrentItem, position);
                    }
                    mCurrentItem = position;//记录当前位置
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    super.onPageScrollStateChanged(state);
                }
            });
        }
    }

    private class MyOnClickListener implements OnClickListener {

        private int currentIndex;

        public MyOnClickListener(int i) {
            this.currentIndex = i;
        }

        @Override
        public void onClick(View v) {
            //回调点击的位置
            if (mViewPager != null) {
                //有设置viewPager
                if (currentIndex == mCurrentItem) {
                    //如果还是同个页签，使用setCurrentItem不会回调OnPageSelecte(),所以在此处需要回调点击监听
                    if (onItemSelectedListener != null) {
                        onItemSelectedListener.onItemSelected(getBottomItem(currentIndex), mCurrentItem, currentIndex);
                    }
                } else {
                    mViewPager.setCurrentItem(currentIndex, mSmoothScroll);
                }
            } else {
                //没有设置viewPager
                if (onItemSelectedListener != null) {
                    onItemSelectedListener.onItemSelected(getBottomItem(currentIndex), mCurrentItem, currentIndex);
                }

                updateTabState(currentIndex);
            }
        }
    }

    private void updateTabState(int position) {
        if (position >= mItemViews.size()) return;
        resetState();
        mCurrentItem = position;
        mItemViews.get(mCurrentItem).setStatus(true);
    }

    /**
     * 重置当前按钮的状态
     */
    private void resetState() {
        if (mCurrentItem >= mItemViews.size()) return;

        mItemViews.get(mCurrentItem).setStatus(false);
    }

    public void setCurrentItem(int currentItem) {
        if (mViewPager != null) {
            mViewPager.setCurrentItem(currentItem, mSmoothScroll);
        } else {
            updateTabState(currentItem);
        }
    }

    /**
     * 设置未读数
     *
     * @param position  底部标签的下标
     * @param unreadNum 未读数
     */
    public void setUnread(int position, int unreadNum) {
        if (position >= mItemViews.size()) return;
        mItemViews.get(position).setUnreadNum(unreadNum);
    }

    /**
     * 设置提示消息
     *
     * @param position 底部标签的下标
     * @param msg      未读数
     */
    public void setMsg(int position, String msg) {
        if (position >= mItemViews.size()) return;
        mItemViews.get(position).setMsg(msg);
    }

    /**
     * 隐藏提示消息
     *
     * @param position 底部标签的下标
     */
    public void hideMsg(int position) {
        if (position >= mItemViews.size()) return;
        mItemViews.get(position).hideMsg();
    }

    /**
     * 显示提示的小红点
     *
     * @param position 底部标签的下标
     */
    public void showNotify(int position) {
        if (position >= mItemViews.size()) return;
        mItemViews.get(position).showNotify();
    }

    /**
     * 隐藏提示的小红点
     *
     * @param position 底部标签的下标
     */
    public void hideNotify(int position) {
        if (position >= mItemViews.size()) return;
        mItemViews.get(position).hideNotify();
    }

    public int getCurrentItem() {
        return mCurrentItem;
    }

    public void setSmoothScroll(boolean smoothScroll) {
        this.mSmoothScroll = smoothScroll;
    }

    public LottieBarItem getBottomItem(int position) {
        return mItemViews.get(position);
    }


    private OnItemSelectedListener onItemSelectedListener;

    public interface OnItemSelectedListener {
        void onItemSelected(LottieBarItem bottomBarItem, int previousPosition, int currentPosition);
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

}
