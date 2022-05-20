package com.seventh.demo.widget.navigationbar

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import kotlin.collections.ArrayList

class ViewPager2FragmentAdapter : FragmentStateAdapter {

    private var mFragmentList: MutableList<Fragment>

    constructor(fragment: Fragment, fragmentList: MutableList<Fragment>) : super(fragment) {
        this.mFragmentList = fragmentList
    }

    constructor(fragmentActivity: FragmentActivity, fragmentList: MutableList<Fragment>) : super(fragmentActivity) {
        this.mFragmentList = fragmentList
    }

    override fun getItemCount(): Int {
        return mFragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return mFragmentList[position]
    }

}