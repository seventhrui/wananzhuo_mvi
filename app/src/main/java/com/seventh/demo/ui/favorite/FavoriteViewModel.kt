package com.seventh.demo.ui.favorite

import androidx.lifecycle.ViewModel
import com.seventh.demo.core.SharedFlowEvents
import com.seventh.demo.data.vo.ProjectVO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class FavoriteViewModel: ViewModel() {
    private val _viewStates = MutableStateFlow(FavoriteViewState())
    val viewStates = _viewStates.asStateFlow()
    private val _viewEvents = SharedFlowEvents<FavoriteViewEvent>()
    val viewEvents = _viewEvents.asSharedFlow()

    fun dispatch(viewAction: FavoriteViewAction) {
        when(viewAction) {
            is FavoriteViewAction.GetListRefresh -> getListRefresh()
            is FavoriteViewAction.GetListLoadMore -> getListLoadMore()
        }
    }

    private fun getListRefresh() {

    }

    private fun getListLoadMore() {

    }
}

data class FavoriteViewState(
    val projectList: List<ProjectVO> = emptyList()
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