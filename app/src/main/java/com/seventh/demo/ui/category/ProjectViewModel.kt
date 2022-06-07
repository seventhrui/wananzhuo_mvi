package com.seventh.demo.ui.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seventh.demo.core.SharedFlowEvents
import com.seventh.demo.core.setEvent
import com.seventh.demo.core.setState
import com.seventh.demo.data.vo.ProjectVO
import com.seventh.demo.network.Api
import com.seventh.demo.network.HttpResult
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProjectViewModel: ViewModel() {
    private val _viewStates = MutableStateFlow(ProjectViewState())
    val viewStates = _viewStates.asStateFlow()
    private val _viewEvents = SharedFlowEvents<ProjectViewEvent>()
    val viewEvents = _viewEvents.asSharedFlow()

    private var currentPage = 1

    fun dispatch(viewAction: ProjectViewAction) {
        when(viewAction) {
            is ProjectViewAction.UpdateCID -> updateCid(viewAction.cid)
            is ProjectViewAction.GetListRefresh -> getListRefresh()
            is ProjectViewAction.GetListLoadMore -> getListLoadMore()
        }
    }

    private fun updateCid(cid: Int) {
        _viewStates.setState { copy(cid = cid) }
    }

    private fun getListRefresh() {
        viewModelScope.launch {
            flow {
                currentPage = 1
                emit(Api.service.projectList(currentPage, 20, _viewStates.value.cid))
            }.onStart {
                _viewEvents.setEvent(ProjectViewEvent.ShowLoadingDialog)
            }.map {
                if (it.status == 0){
                    HttpResult.Success(it.data!!)
                } else {
                    throw Exception(it.message)
                }
            }.onEach {
                _viewStates.setState { copy(projectList = it.result.datas) }
            }.catch {
                _viewEvents.setEvent(
                    ProjectViewEvent.DismissLoadingDialog,
                    ProjectViewEvent.ShowToast("${it.message}")
                )
            }.onCompletion {
                _viewEvents.setEvent(
                    ProjectViewEvent.DismissLoadingDialog
                )
            }.collect()
        }
    }

    private fun getListLoadMore() {
        viewModelScope.launch {
            flow {
                currentPage += 1
                emit(Api.service.projectList(currentPage, 20, _viewStates.value.cid))
            }.onStart {
                _viewEvents.setEvent(ProjectViewEvent.ShowLoadingDialog)
            }.map {
                if (it.status == 0) {
                    HttpResult.Success(it.data!!)
                } else {
                    throw Exception(it.message)
                }
            }.onEach {
                _viewStates.setState { copy(projectList = this.projectList + it.result.datas) }
            }.catch {
                _viewEvents.setEvent(
                    ProjectViewEvent.DismissLoadingDialog,
                    ProjectViewEvent.ShowToast("${it.message}")
                )
            }.onCompletion {
                _viewEvents.setEvent(
                    ProjectViewEvent.DismissLoadingDialog
                )
            }.collect()
        }
    }

}

data class ProjectViewState(
    val cid: Int = 0,
    val projectList: List<ProjectVO> = emptyList(),
)

sealed class ProjectViewEvent {
    data class ShowToast(val message: String): ProjectViewEvent()
    object ShowLoadingDialog: ProjectViewEvent()
    object DismissLoadingDialog: ProjectViewEvent()
}

sealed class ProjectViewAction {
    data class UpdateCID(val cid: Int): ProjectViewAction()
    object GetListRefresh: ProjectViewAction()
    object GetListLoadMore: ProjectViewAction()
}