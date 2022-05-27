package com.seventh.demo.adapter

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.seventh.demo.R
import com.seventh.demo.data.vo.CourseGroupVO

class CategoryAdapter(data: ArrayList<CourseGroupVO> = ArrayList()): BaseQuickAdapter<CourseGroupVO, BaseViewHolder>(
    R.layout.item_category_title, data) {

    @SuppressLint("NotifyDataSetChanged")
    override fun convert(holder: BaseViewHolder, item: CourseGroupVO) {
        holder.setText(R.id.tv_name, item.name)
    }

}