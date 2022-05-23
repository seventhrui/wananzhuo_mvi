package com.seventh.demo.ui.home

import android.util.Log
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.seventh.demo.adapter.ArticleListAdapter
import com.seventh.demo.adapter.BannerAdsAdapter
import com.seventh.demo.base.BaseFragment
import com.seventh.demo.core.observeEvent
import com.seventh.demo.core.observeState
import com.seventh.demo.core.showToast
import com.seventh.demo.data.vo.ArticleVO
import com.seventh.demo.data.vo.BannerVo
import com.seventh.demo.databinding.FragmentHomeBinding
import com.youth.banner.indicator.RectangleIndicator

class HomeTabFragment: BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {
    private val viewModel by viewModels<HomeViewModel>()

    private var bannerList = ArrayList<BannerVo>()
    private lateinit var bannerAdsAdapter: BannerAdsAdapter
    private var articleList = ArrayList<ArticleVO>()
    private var articleListAdapter = ArticleListAdapter(articleList)

    override fun onResume() {
        super.onResume()
        Log.e("HOMEFRAGMENT", "onResume")
    }

    override fun initView() {
        bannerAdsAdapter = BannerAdsAdapter(mContext)
        binding.homeBanner.setAdapter(bannerAdsAdapter)
        binding.homeBanner.indicator = RectangleIndicator(mContext)
        binding.homeBanner.setOnBannerListener{ data, position ->

        }
        bannerAdsAdapter.setDatas(bannerList)

        binding.rvHome.layoutManager = LinearLayoutManager(mContext)
        binding.rvHome.adapter = articleListAdapter
    }

    override fun initData() {
        viewModel.dispatch(HomeViewAction.GetBanner)
        viewModel.dispatch(HomeViewAction.GetList)
    }

    override fun initViewEvents() {
        viewModel.viewEvents.observeEvent(this) {
            when(it) {
                is HomeViewEvent.ShowToast -> it.message.showToast()
                is HomeViewEvent.ShowLoadingDialog -> showLoading()
                is HomeViewEvent.DismissLoadingDialog -> dismissLoading()
            }
        }
    }

    override fun initViewStates() {
        viewModel.viewStates.let { states ->
            states.observeState(this, HomeViewState::bannerList) {
                it.let { it1 ->
                    bannerList.addAll(it1)
                    bannerAdsAdapter.notifyDataSetChanged()
                }
            }
            states.observeState(this, HomeViewState::articleList) {
                Log.e("getlist", "长度：${it.size}")
                it.let {
                    it1 -> articleList.addAll(it1)
                    articleListAdapter.notifyDataSetChanged()
                }
            }
        }
    }
}