package com.nexters.mytine.ui.write

import com.nexters.mytine.base.recyclerview.BaseItem
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

internal data class WeekItem(
    val dayOfWeek: DayOfWeek,
    val selected: Boolean = false,
    override val itemId: String = dayOfWeek.toString()
) : BaseItem {
    fun weekName(): String {
        return dayOfWeek.getDisplayName(TextStyle.NARROW, Locale.KOREAN)
    }
}
