package com.seventh.demo.ui.category

import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.seventh.demo.adapter.ProjectAdapter
import com.seventh.demo.base.BaseAppCompatActivity
import com.seventh.demo.core.observeEvent
import com.seventh.demo.core.observeState
import com.seventh.demo.core.showToast
import com.seventh.demo.databinding.ActivityProjectListBinding
import com.seventh.demo.ui.web.WebActivity
import com.seventh.demo.widget.qmuirefresh.QMUIPullRefreshLayout

class ProjectListActivity: BaseAppCompatActivity<ActivityProjectListBinding>(ActivityProjectListBinding::inflate) {
    private val viewModel by viewModels<ProjectViewModel>()

    private val projectAdapter = ProjectAdapter()

    override fun initView() {
        binding.rvProject.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvProject.adapter = projectAdapter
    }

    override fun initData() {
        val cid = intent.getIntExtra("cid", 0)
        viewModel.dispatch(ProjectViewAction.UpdateCID(cid))

        viewModel.dispatch(ProjectViewAction.GetListRefresh)
    }

    override fun initViewEvents() {
        viewModel.viewEvents.observeEvent(this) {
            when(it) {
                is ProjectViewEvent.ShowToast -> it.message.showToast()
                is ProjectViewEvent.ShowLoadingDialog -> showLoading()
                is ProjectViewEvent.DismissLoadingDialog -> dismissLoading()
            }
        }

        binding.qrlProject.setRefreshListener(object : QMUIPullRefreshLayout.SimpleRefreshListener {
            override fun onRefresh() {
                viewModel.dispatch(ProjectViewAction.GetListRefresh)
            }
        })

        projectAdapter.loadMoreModule.checkDisableLoadMoreIfNotFullPage()
        projectAdapter.loadMoreModule.setOnLoadMoreListener {
            viewModel.dispatch(ProjectViewAction.GetListLoadMore)
        }

        projectAdapter.setOnItemClickListener { adapter, view, position ->
            startActivity(Intent(this@ProjectListActivity, WebActivity::class.java).apply {
                putExtra("url", viewModel.viewStates.value.projectList[position].link)
            })
        }
    }

    override fun initViewStates() {
        viewModel.viewStates.let { states ->
            states.observeState(this, ProjectViewState::projectList) {
                if (it.isNotEmpty() && projectAdapter.data!=it) {
                    projectAdapter.setList(it)
                }
            }
        }
    }

    override fun dismissLoading() {
        super.dismissLoading()
        binding.qrlProject.finishRefresh()
        projectAdapter.loadMoreModule.isLoadEndMoreGone
    }
}