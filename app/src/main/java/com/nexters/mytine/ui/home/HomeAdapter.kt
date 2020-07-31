package com.nexters.mytine.ui.home

import androidx.databinding.ViewDataBinding
import com.nexters.mytine.R
import com.nexters.mytine.base.recyclerview.BaseListAdapter
import com.nexters.mytine.base.recyclerview.BaseViewHolder
import com.nexters.mytine.data.entity.Routine
import com.nexters.mytine.databinding.ItemHomeRoutineGroupBinding

internal class HomeAdapter : BaseListAdapter<HomeItems>() {
    override fun getItemViewTypeByItem(item: HomeItems): Int {
        return when (item) {
            is HomeItems.RoutineGroupItem -> R.layout.item_home_routine_group
            is HomeItems.TabBarItem -> R.layout.item_home_tab_bar
            is HomeItems.RoutineItem -> {
                if (item.routine.status == Routine.Status.ENABLE)
                    R.layout.item_home_routine
                else
                    R.layout.item_home_routine_completed
            }
            is HomeItems.Retrospect -> R.layout.item_home_retrospect
        }
    }

    override fun createViewHolder(binding: ViewDataBinding, viewType: Int): BaseViewHolder<HomeItems> {
        return when (viewType) {
            R.layout.item_home_routine_group -> RoutineGroupViewHolder(
                binding as ItemHomeRoutineGroupBinding,
                viewModel as HomeViewModel
            )
            else -> super.createViewHolder(binding, viewType)
        }
    }
}
