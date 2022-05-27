package com.seventh.demo.ui.category

import androidx.lifecycle.ViewModel
import com.seventh.demo.data.vo.CourseVO

class CategoryViewModel: ViewModel() {

}

data class CategoryViewState(
    val categoryList: List<CourseVO> = emptyList(),
)

sealed class CategoryViewEvent {

}

sealed class CategoryViewAction {

}