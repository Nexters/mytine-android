package com.nexters.mytine.ui.home.routine

import com.nexters.mytine.base.recyclerview.BaseItem

internal sealed class RoutineItems(override val itemId: String) : BaseItem {
    data class RoutineItem(val routine: Routine) : RoutineItems(routine.id.toString())
}
