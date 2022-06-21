package com.seventh.demo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.seventh.demo.R
import com.seventh.demo.data.vo.BannerVo
import com.youth.banner.adapter.BannerAdapter

class BannerAdsAdapter(private val context: Context): BannerAdapter<BannerVo, BannerAdsAdapter.BannerVH>(
    emptyList()) {

    override fun onCreateHolder(parent: ViewGroup, viewType: Int): BannerVH {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_rv_banner_ads, parent, false)
        return BannerVH(itemView)
    }

    private var imgWidth = 0
    private var imgHeight = 0

    fun updateBannerSize(width : Int, height : Int){
        this.imgWidth = width
        this.imgHeight = height
        notifyDataSetChanged()
    }

    override fun onBindView(
        holder: BannerAdsAdapter.BannerVH,
        data: BannerVo,
        position: Int,
        size: Int,
    ) {
        if(imgWidth > 0 && imgHeight > 0){
            holder.ivImage.load(data.imagePath){
                size(imgWidth, imgHeight)
            }
        } else{
            holder.ivImage.load(data.imagePath)
        }
    }

    inner class BannerVH(itemView : View) : RecyclerView.ViewHolder(itemView){
        val ivImage : ImageView = itemView.findViewById(R.id.iv_icon)
    }
}