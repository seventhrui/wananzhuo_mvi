package com.seventh.demo.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.seventh.demo.R
import com.seventh.demo.data.vo.CourseGroupVO

class CategoryAdapter(data: ArrayList<CourseGroupVO> = ArrayList()): BaseQuickAdapter<CourseGroupVO, BaseViewHolder>(
    R.layout.item_category_group, data) {

    @SuppressLint("NotifyDataSetChanged")
    override fun convert(holder: BaseViewHolder, item: CourseGroupVO) {
        var adapter = CategoryGroupAdapter()
        adapter.setList(item.children)
        holder.getView<RecyclerView>(R.id.rv_group).layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL)
        holder.getView<RecyclerView>(R.id.rv_group).adapter = adapter
    }

}