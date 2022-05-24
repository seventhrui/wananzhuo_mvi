package com.seventh.demo.adapter

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.seventh.demo.R
import com.seventh.demo.data.vo.ArticleVO
import com.seventh.demo.utils.TimeUtils

class ArticleListAdapter(data: ArrayList<ArticleVO> = ArrayList()): BaseQuickAdapter<ArticleVO, BaseViewHolder>(R.layout.item_article_list, data), LoadMoreModule {

    @SuppressLint("NotifyDataSetChanged")
    override fun convert(holder: BaseViewHolder, item: ArticleVO) {
        holder
            .setText(R.id.tv_author_1, if(item.author.isNotEmpty()) "${item.author[0]}" else "")
            .setText(R.id.tv_author_2, item.author)
            .setText(R.id.tv_publish_time, TimeUtils.getStringByFormat(item.publishTime, TimeUtils.dateFormatYMD))
            .setText(R.id.tv_title, item.title)
    }

}