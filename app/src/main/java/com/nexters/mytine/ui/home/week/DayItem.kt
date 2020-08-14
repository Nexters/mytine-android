package com.nexters.mytine.ui.home.week

import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

data class DayItem(
    val date: LocalDate,
    val isRetrospectWrite: Boolean
) {
    fun weekName(): String {
        return date.dayOfWeek.getDisplayName(TextStyle.NARROW, Locale.KOREAN)
    }
}
