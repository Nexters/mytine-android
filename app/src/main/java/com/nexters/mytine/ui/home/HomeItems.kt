package com.nexters.mytine.ui.home

import com.nexters.mytine.base.recyclerview.BaseItem
import com.nexters.mytine.data.entity.Routine

internal sealed class HomeItems(override val itemId: String) : BaseItem {
    data class RoutineItem(val routine: Routine) : HomeItems(routine.id.toString())
}
