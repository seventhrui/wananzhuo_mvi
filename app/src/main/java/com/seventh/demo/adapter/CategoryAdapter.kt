package com.seventh.demo.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.seventh.demo.R
import com.seventh.demo.data.vo.CourseGroupVO

class CategoryAdapter(data: ArrayList<CourseGroupVO> = ArrayList()): BaseQuickAdapter<CourseGroupVO, BaseViewHolder>(
    R.layout.item_category_group, data) {

    @SuppressLint("NotifyDataSetChanged")
    override fun convert(holder: BaseViewHolder, item: CourseGroupVO) {
        var adapter = CategoryGroupAdapter()
        adapter.setList(item.children)
        holder.getView<RecyclerView>(R.id.rv_group).layoutManager = FlexboxLayoutManager(context).apply {
            justifyContent = JustifyContent.FLEX_START
        }
        holder.getView<RecyclerView>(R.id.rv_group).adapter = adapter

        adapter.setOnItemClickListener { adapter, view, position ->

        }
    }

}