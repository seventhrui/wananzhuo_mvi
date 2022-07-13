package com.seventh.demo.ui.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seventh.demo.core.SharedFlowEvents
import com.seventh.demo.core.setEvent
import com.seventh.demo.core.setState
import com.seventh.demo.data.vo.ArticleListVO
import com.seventh.demo.network.Api
import com.seventh.demo.network.HttpResult
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FavoriteViewModel: ViewModel() {
    private val _viewStates = MutableStateFlow(FavoriteViewState())
    val viewStates = _viewStates.asStateFlow()
    private val _viewEvents = SharedFlowEvents<FavoriteViewEvent>()
    val viewEvents = _viewEvents.asSharedFlow()

    private var currentPage = 0

    fun dispatch(viewAction: FavoriteViewAction) {
        when(viewAction) {
            is FavoriteViewAction.GetListRefresh -> getListRefresh()
            is FavoriteViewAction.GetListLoadMore -> getListLoadMore()
        }
    }

    private fun getListRefresh() {
        viewModelScope.launch {
            flow {
                currentPage = 0
                emit(Api.service.collectList(currentPage, 20))
            }.onStart {
                _viewEvents.setEvent(FavoriteViewEvent.ShowLoadingDialog)
            }.map {
                if (it.status == 0){
                    HttpResult.Success(it.data!!)
                } else {
                    throw Exception(it.message)
                }
            }.onEach {
                _viewStates.setState { copy(projectList = it.result) }
            }.catch {
                _viewEvents.setEvent(
                    FavoriteViewEvent.ShowToast("${it.message}")
                )
            }.onCompletion {
                _viewEvents.setEvent(
                    FavoriteViewEvent.DismissLoadingDialog
                )
            }.collect()
        }
    }

    private fun getListLoadMore() {
        viewModelScope.launch {
            flow {
                currentPage += 1
                emit(Api.service.collectList(currentPage, 20))
            }.onStart {
                _viewEvents.setEvent(FavoriteViewEvent.ShowLoadingDialog)
            }.map {
                if (it.status == 0) {
                    HttpResult.Success(it.data!!)
                } else {
                    throw Exception(it.message)
                }
            }.onEach {
                _viewStates.setState { copy(projectList = ArticleListVO(this.projectList.datas + it.result.datas)) }
            }.catch {
                _viewEvents.setEvent(
                    FavoriteViewEvent.ShowToast("${it.message}")
                )
            }.onCompletion {
                _viewEvents.setEvent(
                    FavoriteViewEvent.DismissLoadingDialog
                )
            }.collect()
        }
    }
}

data class FavoriteViewState(
    val projectList: ArticleListVO = ArticleListVO(emptyList())
)

sealed class FavoriteViewEvent {
    data class ShowToast(val message: String): FavoriteViewEvent()
    object ShowLoadingDialog: FavoriteViewEvent()
    object DismissLoadingDialog: FavoriteViewEvent()
}

sealed class FavoriteViewAction {
    object GetListRefresh: FavoriteViewAction()
    object GetListLoadMore: FavoriteViewAction()
}