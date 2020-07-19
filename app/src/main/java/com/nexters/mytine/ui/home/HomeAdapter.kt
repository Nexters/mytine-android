package com.nexters.mytine.ui.home

import com.nexters.mytine.R
import com.nexters.mytine.base.recyclerview.BaseListAdapter

internal class HomeAdapter : BaseListAdapter<HomeItems>() {
    override fun getItemViewTypeByItem(item: HomeItems): Int {
        return when (item) {
            is HomeItems.RoutineItem -> R.layout.item_home_routine
        }
    }
}
