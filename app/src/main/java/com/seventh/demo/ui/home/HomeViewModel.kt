package com.seventh.demo.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seventh.demo.core.SharedFlowEvents
import com.seventh.demo.core.setEvent
import com.seventh.demo.core.setState
import com.seventh.demo.data.vo.ArticleVO
import com.seventh.demo.data.vo.BannerVo
import com.seventh.demo.network.Api
import com.seventh.demo.network.HttpResult
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.lang.Exception

class HomeViewModel: ViewModel() {
    private val _viewStates = MutableStateFlow(HomeViewState())
    val viewStates = _viewStates.asStateFlow()
    private val _viewEvents = SharedFlowEvents<HomeViewEvent>()
    val viewEvents = _viewEvents.asSharedFlow()

    private var currentPage = 1

    fun dispatch(viewAction: HomeViewAction) {
        when(viewAction) {
            is HomeViewAction.GetBanner -> getBanner()
            is HomeViewAction.GetListRefresh -> getListRefresh()
            is HomeViewAction.GetListMore -> getListMore()
        }
    }

    private fun getBanner() {
        viewModelScope.launch {
            flow {
                emit(Api.service.bannerList())
            }.onStart {
                _viewEvents.setEvent(HomeViewEvent.ShowLoadingDialog)
            }.map {
                if (it.status == 0){
                    HttpResult.Success(it.data!!)
                } else {
                    throw Exception(it.message)
                }
            }.onEach {
                _viewStates.setState { copy(bannerList = it.result) }
            }.catch {
                Log.e("HomeViewModel", "getBanner:${it.message}")
                _viewEvents.setEvent(
                    HomeViewEvent.ShowToast("${it.message}")
                )
            }.onCompletion {
                _viewEvents.setEvent(
                    HomeViewEvent.DismissLoadingDialog
                )
            }
            .collect()
        }
    }

    private fun getListRefresh() {
        viewModelScope.launch {
            flow {
                currentPage = 1
                emit(Api.service.articleList(currentPage, 20))
            }.onStart {
                _viewEvents.setEvent(HomeViewEvent.ShowLoadingDialog)
            }.map {
                if (it.status == 0){
                    HttpResult.Success(it.data!!)
                } else {
                    throw Exception(it.message)
                }
            }.onEach {
                _viewStates.setState { copy(articleList = it.result.datas) }
            }.catch {
                Log.e("HomeViewModel", "getList:${it.message}")
                _viewEvents.setEvent(
                    HomeViewEvent.DismissLoadingDialog,
                    HomeViewEvent.ShowToast("${it.message}")
                )
            }.onCompletion {
                _viewEvents.setEvent(
                    HomeViewEvent.DismissLoadingDialog
                )
            }.collect()
        }
    }

    private fun getListMore() {
        viewModelScope.launch {
            flow {
                currentPage += 1
                emit(Api.service.articleList(currentPage, 20))
            }.onStart {
                _viewEvents.setEvent(HomeViewEvent.ShowLoadingDialog)
            }.map {
                if (it.status == 0){
                    HttpResult.Success(it.data!!)
                } else {
                    throw Exception(it.message)
                }
            }.onEach {
                _viewStates.setState { copy(articleList = this.articleList + it.result.datas) }
            }.catch {
                Log.e("HomeViewModel", "getList:${it.message}")
                _viewEvents.setEvent(
                    HomeViewEvent.ShowToast("${it.message}")
                )
            }.onCompletion {
                _viewEvents.setEvent(
                    HomeViewEvent.DismissLoadingDialog
                )
            }.collect()
        }
    }
}

data class HomeViewState(
    val bannerList: List<BannerVo> = emptyList(),
    val articleList: List<ArticleVO> = emptyList()
)

sealed class HomeViewEvent {
    data class ShowToast(val message: String): HomeViewEvent()
    object ShowLoadingDialog: HomeViewEvent()
    object DismissLoadingDialog: HomeViewEvent()
}

sealed class HomeViewAction {
    object GetBanner: HomeViewAction()
    object GetListRefresh: HomeViewAction()
    object GetListMore: HomeViewAction()
}