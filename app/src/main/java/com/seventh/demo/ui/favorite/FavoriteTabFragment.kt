package com.seventh.demo.ui.favorite

import androidx.recyclerview.widget.LinearLayoutManager
import com.seventh.demo.adapter.ProjectAdapter
import com.seventh.demo.base.BaseFragment
import com.seventh.demo.databinding.FragmentFavoriteBinding

class FavoriteTabFragment: BaseFragment<FragmentFavoriteBinding>(FragmentFavoriteBinding::inflate) {

    private val projectAdapter = ProjectAdapter()

    override fun initView() {
        binding.rvFavorite.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        binding.rvFavorite.adapter = projectAdapter
    }


}