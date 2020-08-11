package com.nexters.mytine.ui.home

import com.nexters.mytine.R
import com.nexters.mytine.base.recyclerview.BaseListAdapter
import com.nexters.mytine.base.recyclerview.BaseViewHolder

internal class HomeAdapter : BaseListAdapter<HomeItems>() {
    override fun getItemViewTypeByItem(item: HomeItems): Int {
        return when (item) {
            is HomeItems.TabBarItem -> R.layout.item_home_tab_bar
            is HomeItems.EmptyRoutineItem -> R.layout.item_home_routine_empty
            is HomeItems.RoutineItem.EnabledRoutineItem -> R.layout.item_home_routine
            is HomeItems.RoutineItem.CompletedRoutineItem -> R.layout.item_home_routine_completed
            is HomeItems.Retrospect -> R.layout.item_home_retrospect
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<HomeItems>, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.itemView.id
    }
}
