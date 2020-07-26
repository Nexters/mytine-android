package com.nexters.mytine.ui.home

import com.nexters.mytine.base.recyclerview.BaseItem
import com.nexters.mytine.data.entity.Routine
import com.nexters.mytine.ui.home.icongroup.IconGroupItem
import com.nexters.mytine.ui.home.week.WeekItem

internal sealed class HomeItems(override val itemId: String) : BaseItem {
    data class RoutineGroupItem(
        val weekItems: List<WeekItem>,
        val iconGroupItems: List<IconGroupItem>
    ) : HomeItems("routineGroup")

    class TabBarItem : HomeItems("tabBar")

    class Retrospect : HomeItems("retrospect")

    data class RoutineItem(val routine: Routine) : HomeItems(routine.id)
}
