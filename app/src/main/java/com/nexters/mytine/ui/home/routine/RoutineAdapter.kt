package com.nexters.mytine.ui.home.routine

import com.nexters.mytine.R
import com.nexters.mytine.base.recyclerview.BaseListAdapter

internal class RoutineAdapter : BaseListAdapter<RoutineItems>() {
    override fun getItemViewTypeByItem(item: RoutineItems): Int {
        return when (item) {
            is RoutineItems.RoutineItem -> R.layout.item_routine
        }
    }
}
