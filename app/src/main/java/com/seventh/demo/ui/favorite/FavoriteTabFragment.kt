package com.seventh.demo.ui.favorite

import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.seventh.demo.adapter.ArticleListAdapter
import com.seventh.demo.base.BaseFragment
import com.seventh.demo.core.observeEvent
import com.seventh.demo.core.observeState
import com.seventh.demo.core.showToast
import com.seventh.demo.databinding.FragmentFavoriteBinding
import com.seventh.demo.ui.category.ProjectViewAction
import com.seventh.demo.ui.category.ProjectViewEvent
import com.seventh.demo.widget.qmuirefresh.QMUIPullRefreshLayout

class FavoriteTabFragment: BaseFragment<FragmentFavoriteBinding>(FragmentFavoriteBinding::inflate) {
    private val viewModel by viewModels<FavoriteViewModel>()

    private val articleListAdapter = ArticleListAdapter()

    override fun initView() {
        binding.rvFavorite.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        binding.rvFavorite.adapter = articleListAdapter
    }

    override fun initData() {
        viewModel.dispatch(FavoriteViewAction.GetListRefresh)
    }

    override fun initViewEvents() {
        viewModel.viewEvents.observeEvent(this) {
            when(it) {
                is FavoriteViewEvent.ShowToast -> it.message.showToast()
                is FavoriteViewEvent.ShowLoadingDialog -> showLoading()
                is FavoriteViewEvent.DismissLoadingDialog -> dismissLoading()
            }
        }

        binding.qrlProject.setRefreshListener(object : QMUIPullRefreshLayout.SimpleRefreshListener {
            override fun onRefresh() {
                viewModel.dispatch(FavoriteViewAction.GetListRefresh)
            }
        })

        articleListAdapter.loadMoreModule.checkDisableLoadMoreIfNotFullPage()
        articleListAdapter.loadMoreModule.setOnLoadMoreListener {
            viewModel.dispatch(FavoriteViewAction.GetListLoadMore)
        }
    }

    override fun initViewStates() {
        viewModel.viewStates.let { states ->
            states.observeState(this, FavoriteViewState::projectList) {
                if (it.datas.isNotEmpty() && articleListAdapter.data!=it.datas) {
                    articleListAdapter.setList(it.datas)
                }
            }
        }
    }

    override fun dismissLoading() {
        super.dismissLoading()
        binding.qrlProject.finishRefresh()
        articleListAdapter.loadMoreModule.loadMoreComplete()
    }
}