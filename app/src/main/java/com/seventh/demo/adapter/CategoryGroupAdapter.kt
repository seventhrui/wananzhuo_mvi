package com.seventh.demo.adapter

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.seventh.demo.R
import com.seventh.demo.data.vo.CourseGroupVO
import com.seventh.demo.data.vo.CourseVO

class CategoryGroupAdapter(data: ArrayList<CourseVO> = ArrayList()): BaseQuickAdapter<CourseVO, BaseViewHolder>(
    R.layout.item_category_name, data) {

    @SuppressLint("NotifyDataSetChanged")
    override fun convert(holder: BaseViewHolder, item: CourseVO) {
        holder.setText(R.id.tv_name, item.name)
    }

}