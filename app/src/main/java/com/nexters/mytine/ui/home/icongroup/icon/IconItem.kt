package com.nexters.mytine.ui.home.icongroup.icon

import com.nexters.mytine.base.recyclerview.BaseItem
import com.nexters.mytine.data.entity.Routine

internal data class IconItem(val routine: Routine, override val itemId: String = routine.realId) : BaseItem {
    fun visibility(): Boolean = when (routine.status) {
        Routine.Status.ENABLE, Routine.Status.SUCCESS -> true
        else -> false
    }
}
