package com.seventh.demo.ui.main

import android.view.Gravity
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.seventh.demo.R
import com.seventh.demo.base.BaseAppCompatActivity
import com.seventh.demo.databinding.ActivityMainBinding
import com.seventh.demo.extension.colorById
import com.seventh.demo.extension.dimenPixelOffset
import com.seventh.demo.extension.strById
import com.seventh.demo.ui.category.CategoryTabFragment
import com.seventh.demo.ui.favorite.FavoriteTabFragment
import com.seventh.demo.ui.home.HomeTabFragment
import com.seventh.demo.ui.mine.MineTabFragment
import com.seventh.demo.widget.navigationbar.LottieBarItem
import com.seventh.demo.widget.navigationbar.LottieBottomBarLayout
import com.seventh.demo.widget.navigationbar.ViewPager2FragmentAdapter

enum class HomePageTabType(val tabName: String) {
    Home("home"), Category("category"), Favorite("favorite"), UserCenter("userCenter")
}

class MainActivity: BaseAppCompatActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    private var currentTabPage: HomePageTabType? = HomePageTabType.Home

    override fun overrideStatusBar(isHideStatusBar: Boolean, is_M_LightMode: Boolean, color: Int) =
        super.overrideStatusBar(isHideStatusBar = true, is_M_LightMode = true, color = color)

    override fun initView() {
        initHomeTabs()
    }

    override fun initViewEvents() {

    }

    override fun initViewStates() {

    }

    private fun initHomeTabs() {
        val tabFragments = mutableListOf<Fragment>().apply {
            // 首页
            with(HomeTabFragment()) {
                add(this)
                binding.lottieBottomBar.addView(
                    generateLottieBarItem(
                        tabType = HomePageTabType.Home.tabName,
                        lottie_title = strById(R.string.str_navigation_tab_home),
                        lottie_json_data = "tab_lottie_home.json"
                    )
                )
            }
            // 分类
            with(CategoryTabFragment()) {
                add(this)
                binding.lottieBottomBar.addView(
                    generateLottieBarItem(
                        tabType = HomePageTabType.Category.tabName,
                        lottie_title = strById(R.string.str_navigation_tab_category),
                        lottie_json_data = "tab_lottie_home.json"
                    )
                )
            }
            // 收藏
            with(FavoriteTabFragment()) {
                add(this)
                binding.lottieBottomBar.addView(
                    generateLottieBarItem(
                        tabType = HomePageTabType.Favorite.tabName,
                        lottie_title = strById(R.string.str_navigation_tab_favorite),
                        lottie_json_data = "tab_lottie_home.json"
                    )
                )
            }
            // 个人中心
            with(MineTabFragment()) {
                add(this)
                binding.lottieBottomBar.addView(
                    generateLottieBarItem(
                        tabType = HomePageTabType.UserCenter.tabName,
                        lottie_title = strById(R.string.str_navigation_tab_mine),
                        lottie_json_data = "tab_lottie_home.json"
                    )
                )
            }
        }

        binding.lottieBottomBar.notifyChildViewsUpdate()

        val vpTabAdapter = ViewPager2FragmentAdapter(this, tabFragments)
        binding.vpTabs.adapter = vpTabAdapter
        binding.vpTabs.isUserInputEnabled = false
        binding.vpTabs.offscreenPageLimit = tabFragments.size

        binding.lottieBottomBar.setViewPager(binding.vpTabs)
        binding.lottieBottomBar.setOnItemSelectedListener(object :
            LottieBottomBarLayout.OnItemSelectedListener {
            override fun onItemSelected(
                bottomBarItem: LottieBarItem?,
                previousPosition: Int,
                currentPosition: Int
            ) {
                when(bottomBarItem?.getTag(R.id.tab_types) as String) {
                    HomePageTabType.Home.tabName -> {
                        currentTabPage = HomePageTabType.Home
                    }
                    HomePageTabType.Category.tabName -> {
                        currentTabPage = HomePageTabType.Favorite
                    }
                    HomePageTabType.Favorite.tabName -> {
                        currentTabPage = HomePageTabType.Favorite
                    }
                    HomePageTabType.UserCenter.tabName -> {
                        currentTabPage = HomePageTabType.UserCenter
                    }
                    else -> {
                        currentTabPage = HomePageTabType.Home
                    }
                }
            }
        })
    }

    private fun generateLottieBarItem(
        tabType: String,
        lottie_title: String,
        lottie_json_data: String,
    ): LottieBarItem {
        return LottieBarItem(this).apply {
            layoutParams =
                LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                    weight = 1F
                    gravity = Gravity.CENTER_VERTICAL
                }
            setmIconWidth(dimenPixelOffset(R.dimen.dp_30))
            setmIconHeight(dimenPixelOffset(R.dimen.dp_30))

            setTabTitle(lottie_title)
            setTabTitleSelectTextColor(colorById(R.color.home_color_tab_select))
            setTabTitleUnSelectTextColor(colorById(R.color.home_color_tab_unselect))
            setTabTitleTextSize(dimenPixelOffset(R.dimen.sp_10))

            setLottieDataJson(lottie_json_data)

            setTag(R.id.tab_types, tabType)

            setupConfig()
        }
    }
}