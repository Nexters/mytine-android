package com.nexters.mytine.ui.write

import com.nexters.mytine.base.recyclerview.BaseItem
import java.time.DayOfWeek

internal data class WeekItem(
    val dayOfWeek: DayOfWeek,
    val selected: Boolean = false,
    override val itemId: String = dayOfWeek.toString()
) : BaseItem {
    fun weekName(): String {
        return dayOfWeek.name
    }
}
