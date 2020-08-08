package com.nexters.mytine.ui.home

import com.nexters.mytine.R
import com.nexters.mytine.base.recyclerview.BaseListAdapter

internal class HomeAdapter : BaseListAdapter<HomeItems>() {
    override fun getItemViewTypeByItem(item: HomeItems): Int {
        return when (item) {
            is HomeItems.TabBarItem -> R.layout.item_home_tab_bar
            is HomeItems.RoutineItem.EnabledRoutineItem -> R.layout.item_home_routine
            is HomeItems.RoutineItem.CompletedRoutineItem -> R.layout.item_home_routine_completed
            is HomeItems.Retrospect -> R.layout.item_home_retrospect
        }
    }
}
