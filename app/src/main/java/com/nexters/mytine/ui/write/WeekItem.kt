package com.nexters.mytine.ui.write

import com.nexters.mytine.base.recyclerview.BaseItem
import java.time.DayOfWeek

internal data class WeekItem(val dayOfWeek: DayOfWeek, override val itemId: String = dayOfWeek.toString()) : BaseItem {
    fun title(): String {
        return dayOfWeek.name
    }
}
