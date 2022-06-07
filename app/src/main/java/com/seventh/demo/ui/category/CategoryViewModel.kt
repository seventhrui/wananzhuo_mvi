package com.seventh.demo.ui.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seventh.demo.core.SharedFlowEvents
import com.seventh.demo.core.setEvent
import com.seventh.demo.core.setState
import com.seventh.demo.data.vo.CourseGroupVO
import com.seventh.demo.network.Api
import com.seventh.demo.network.HttpResult
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CategoryViewModel: ViewModel() {
    private val _viewStates = MutableStateFlow(CategoryViewState())
    val viewStates = _viewStates.asStateFlow()
    private val _viewEvents = SharedFlowEvents<CategoryViewEvent>()
    val viewEvents = _viewEvents.asSharedFlow()

    fun dispatch(viewAction: CategoryViewAction) {
        when(viewAction) {
            is CategoryViewAction.GetList -> getList()
        }
    }

    private fun getList() {
        viewModelScope.launch {
            flow {
                emit(Api.service.courseList())
            }.onStart {
                _viewEvents.setEvent(CategoryViewEvent.ShowLoadingDialog)
            }.map {
                if (it.status == 0){
                    HttpResult.Success(it.data!!)
                } else {
                    throw Exception(it.message)
                }
            }.onEach {
                _viewStates.setState { copy(categoryList = it.result) }
            }.catch {
                _viewEvents.setEvent(
                    CategoryViewEvent.DismissLoadingDialog,
                    CategoryViewEvent.ShowToast("${it.message}")
                )
            }.onCompletion {
                _viewEvents.setEvent(
                    CategoryViewEvent.DismissLoadingDialog
                )
            }.collect()
        }
    }
}

data class CategoryViewState(
    val categoryList: List<CourseGroupVO> = emptyList(),
)

sealed class CategoryViewEvent {
    data class ShowToast(val message: String): CategoryViewEvent()
    object ShowLoadingDialog: CategoryViewEvent()
    object DismissLoadingDialog: CategoryViewEvent()
}

sealed class CategoryViewAction {
    object GetList: CategoryViewAction()
}