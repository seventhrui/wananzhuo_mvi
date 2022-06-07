package com.seventh.demo.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.seventh.demo.R
import com.seventh.demo.data.vo.ProjectVO
import com.seventh.demo.utils.TimeUtils

class ProjectAdapter(data: ArrayList<ProjectVO> = ArrayList()): BaseQuickAdapter<ProjectVO, BaseViewHolder>(
    R.layout.item_project_list, data), LoadMoreModule {

    override fun convert(holder: BaseViewHolder, item: ProjectVO) {
        holder.setText(R.id.tv_title, item.title)
            .setText(R.id.tv_author_1, "作者:${item.author}")
            .setText(R.id.tv_publish_time, TimeUtils.getStringByFormat(item.publishTime, TimeUtils.dateFormatYMD))
    }
}