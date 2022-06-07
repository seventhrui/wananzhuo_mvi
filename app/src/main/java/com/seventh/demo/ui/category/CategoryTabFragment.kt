package com.seventh.demo.ui.category

import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.seventh.demo.adapter.CategoryAdapter
import com.seventh.demo.base.BaseFragment
import com.seventh.demo.core.observeEvent
import com.seventh.demo.core.observeState
import com.seventh.demo.core.showToast
import com.seventh.demo.databinding.FragmentCategoryBinding
import com.seventh.demo.widget.decoration.MySectionDecoration
import com.seventh.demo.widget.qmuirefresh.QMUIPullRefreshLayout

class CategoryTabFragment :
    BaseFragment<FragmentCategoryBinding>(FragmentCategoryBinding::inflate) {
    private val viewModel by viewModels<CategoryViewModel>()

    private var categoryAdapter = CategoryAdapter()

    override fun initView() {
        binding.rvCategory.layoutManager =
            LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        binding.rvCategory.adapter = categoryAdapter
        (binding.rvCategory.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
    }

    override fun initData() {
        viewModel.dispatch(CategoryViewAction.GetList)
    }

    override fun initViewEvents() {
        viewModel.viewEvents.observeEvent(this) {
            when (it) {
                is CategoryViewEvent.ShowToast -> it.message.showToast()
                is CategoryViewEvent.ShowLoadingDialog -> showLoading()
                is CategoryViewEvent.DismissLoadingDialog -> dismissLoading()
            }
        }

        binding.qrlCategory.setRefreshListener(object :
            QMUIPullRefreshLayout.SimpleRefreshListener {
            override fun onRefresh() {
                initData()
            }
        })
    }

    override fun initViewStates() {
        viewModel.viewStates.let { states ->
            states.observeState(this, CategoryViewState::categoryList) {
                if (it.isNotEmpty() && categoryAdapter.data!=it) {
                    binding.rvCategory.addItemDecoration(MySectionDecoration(mContext, it))
                    categoryAdapter.setList(it)
                }
            }
        }
    }

    override fun dismissLoading() {
        super.dismissLoading()
        binding.qrlCategory.finishRefresh()
    }
}
