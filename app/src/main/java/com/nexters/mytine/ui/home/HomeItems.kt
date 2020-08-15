package com.nexters.mytine.ui.home

import com.nexters.mytine.base.recyclerview.BaseItem
import com.nexters.mytine.data.entity.Routine

internal sealed class HomeItems(override val itemId: String) : BaseItem {
    class TabBarItem : HomeItems("tabBar")

    class Retrospect : HomeItems("retrospect")

    class EmptyRoutineItem : HomeItems("intro")

    sealed class RoutineItem(open val routine: Routine) : HomeItems(routine.id) {
        data class EnabledRoutineItem(override val routine: Routine) : RoutineItem(routine)
        data class CompletedRoutineItem(override val routine: Routine) : RoutineItem(routine)

        fun isVisibleGoal(): Boolean = !routine.goal.isNullOrBlank()
    }
}
