package com.seventh.demo.ui.home

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.seventh.demo.adapter.ArticleListAdapter
import com.seventh.demo.adapter.BannerAdsAdapter
import com.seventh.demo.base.BaseFragment
import com.seventh.demo.core.observeEvent
import com.seventh.demo.core.observeState
import com.seventh.demo.core.showToast
import com.seventh.demo.databinding.FragmentHomeBinding
import com.seventh.demo.ui.web.WebActivity
import com.seventh.demo.widget.qmuirefresh.QMUIPullRefreshLayout
import com.youth.banner.indicator.RectangleIndicator

class HomeTabFragment: BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {
    private val viewModel by viewModels<HomeViewModel>()

    private lateinit var bannerAdsAdapter: BannerAdsAdapter
    private var articleListAdapter = ArticleListAdapter()

    override fun initView() {
        bannerAdsAdapter = BannerAdsAdapter(mContext)
        binding.homeBanner.setAdapter(bannerAdsAdapter)
        binding.homeBanner.indicator = RectangleIndicator(mContext)
        binding.homeBanner.setOnBannerListener{ data, position ->

        }

        binding.rvHome.setHasFixedSize(true)
        binding.rvHome.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        binding.rvHome.adapter = articleListAdapter
    }

    override fun initData() {
        viewModel.dispatch(HomeViewAction.GetBanner)
        viewModel.dispatch(HomeViewAction.GetListRefresh)
    }

    override fun initViewEvents() {
        viewModel.viewEvents.observeEvent(this) {
            when(it) {
                is HomeViewEvent.ShowToast -> it.message.showToast()
                is HomeViewEvent.ShowLoadingDialog -> showLoading()
                is HomeViewEvent.DismissLoadingDialog -> dismissLoading()
            }
        }

        binding.qrlHome.setRefreshListener(object: QMUIPullRefreshLayout.SimpleRefreshListener {
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
                Log.e("bannerList", "长度：${it.size}")
                it.let { it1 ->
                    bannerAdsAdapter.setDatas(it1)
                }
            }
            states.observeState(this, HomeViewState::articleList) {
                Log.e("articleList", "长度：${it.size}")
                it.let { it1 ->
                    articleListAdapter.setList(it1)
                }
            }
        }
    }

    override fun dismissLoading() {
        super.dismissLoading()
        binding.qrlHome.finishRefresh()
        articleListAdapter.loadMoreModule.isLoadEndMoreGone
    }
}