package com.seventh.demo.ui.home

import android.content.Intent
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.LinearLayoutManager
import com.orhanobut.logger.Logger
import com.seventh.demo.adapter.ArticleListAdapter
import com.seventh.demo.adapter.BannerAdsAdapter
import com.seventh.demo.base.BaseFragment
import com.seventh.demo.core.observeEvent
import com.seventh.demo.core.observeState
import com.seventh.demo.core.showToast
import com.seventh.demo.databinding.FragmentHomeBinding
import com.seventh.demo.extension.getImageBitmapByUrl
import com.seventh.demo.ui.main.MainViewModel
import com.seventh.demo.ui.web.WebActivity
import com.seventh.demo.widget.qmuirefresh.QMUIPullRefreshLayout
import com.youth.banner.indicator.RectangleIndicator
import com.youth.banner.listener.OnPageChangeListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HomeTabFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {
    private val viewModel by viewModels<HomeViewModel>()
    private val mainViewModel: MainViewModel by activityViewModels()

    private lateinit var bannerAdsAdapter: BannerAdsAdapter
    private var articleListAdapter = ArticleListAdapter()

    private var firstBannerLoad = true

    override fun onResume() {
        super.onResume()
        Logger.e("onResume")
        if (!firstBannerLoad)
            binding.homeBanner.start()
    }

    override fun onPause() {
        super.onPause()
        Logger.e("onPause")
        binding.homeBanner.stop()
    }

    override fun initView() {
        bannerAdsAdapter = BannerAdsAdapter(mContext)
        binding.homeBanner.apply {
            setLoopTime(1500)
            setAdapter(bannerAdsAdapter)
            indicator = RectangleIndicator(mContext)
            addBannerLifecycleObserver(this@HomeTabFragment)
            setOnBannerListener { data, position ->

            }
            addOnPageChangeListener(object : OnPageChangeListener {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    GlobalScope.launch(Dispatchers.IO) {
                        val bitmap =
                            mContext.getImageBitmapByUrl(viewModel.viewStates.value.bannerList[position].imagePath)
                        bitmap?.let { bmp ->
                            Palette
                                .from(bmp)
                                .maximumColorCount(5)
                                .setRegion(0, 0, 900, 500)
                                .generate {
                                    it?.let { palette ->
                                        var mostPopularSwatch: Palette.Swatch? = null
                                        for (swatch in palette.swatches) {
                                            if (mostPopularSwatch == null
                                                || swatch.population > mostPopularSwatch.population
                                            ) {
                                                mostPopularSwatch = swatch
                                            }
                                        }
                                        mostPopularSwatch?.let { swatch ->
                                            val luminance =
                                                ColorUtils.calculateLuminance(swatch.rgb)
                                            // If the luminance value is lower than 0.5, we consider it as dark.
                                            mainViewModel.statusLightMode.value = luminance >= 0.5
                                            overrideStatusBar(isHideStatusBar = true, is_M_LightMode = luminance >= 0.5)
                                        }
                                    }
                                }
                        }
                    }
                }

                override fun onPageSelected(position: Int) {

                }

                override fun onPageScrollStateChanged(state: Int) {

                }

            })
        }
        binding.rvHome.setHasFixedSize(true)
        binding.rvHome.layoutManager =
            LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        binding.rvHome.adapter = articleListAdapter
    }

    override fun initData() {
        viewModel.dispatch(HomeViewAction.GetBanner)
        viewModel.dispatch(HomeViewAction.GetListRefresh)
    }

    override fun initViewEvents() {
        viewModel.viewEvents.observeEvent(this) {
            when (it) {
                is HomeViewEvent.ShowToast -> it.message.showToast()
                is HomeViewEvent.ShowLoadingDialog -> showLoading()
                is HomeViewEvent.DismissLoadingDialog -> dismissLoading()
            }
        }

        binding.qrlHome.setRefreshListener(object : QMUIPullRefreshLayout.SimpleRefreshListener {
            override fun onRefresh() {
                initData()
            }
        })

        articleListAdapter.loadMoreModule.checkDisableLoadMoreIfNotFullPage()
        articleListAdapter.loadMoreModule.setOnLoadMoreListener {
            viewModel.dispatch(HomeViewAction.GetListMore)
        }

        articleListAdapter.setOnItemClickListener { adapter, view, position ->
            startActivity(
                Intent(mContext, WebActivity::class.java).apply {
                    putExtra("url", viewModel.viewStates.value.articleList[position].link)
                }
            )
        }
    }

    override fun initViewStates() {
        viewModel.viewStates.let { states ->
            states.observeState(this, HomeViewState::bannerList) {
                Logger.e("bannerList, 长度：${it.size}")
                it.let { it1 ->
                    bannerAdsAdapter.setDatas(it1)
                    firstBannerLoad = false
                }
            }
            states.observeState(this, HomeViewState::articleList) {
                Logger.e("articleList, 长度：${it.size}")
                it.let { it1 ->
                    articleListAdapter.setList(it1)
                }
            }
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

    }

    override fun dismissLoading() {
        super.dismissLoading()
        binding.qrlHome.finishRefresh()
        articleListAdapter.loadMoreModule.loadMoreComplete()
    }
}