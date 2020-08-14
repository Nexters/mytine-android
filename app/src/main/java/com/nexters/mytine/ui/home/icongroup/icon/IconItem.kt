package com.nexters.mytine.ui.home.icongroup.icon

import com.nexters.mytine.base.recyclerview.BaseItem
import com.nexters.mytine.data.entity.Routine

internal data class IconItem(val routine: Routine, override val itemId: String = routine.realId) : BaseItem {
    fun isVisibility() = when (routine.status) {
        Routine.Status.SUCCESS -> true
        else -> false
    }
    fun isNotTodayRoutine() = when(routine.status) {
        Routine.Status.DISABLE -> true
        else -> false
    }
}
