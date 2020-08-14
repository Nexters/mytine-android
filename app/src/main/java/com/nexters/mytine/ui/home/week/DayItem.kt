package com.nexters.mytine.ui.home.week

import androidx.lifecycle.MutableLiveData
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

data class DayItem(
    val date: LocalDate,
    val isRetrospectWrite: Boolean,
    val isSelected: Boolean
) {
    fun weekName(): String {
        return date.dayOfWeek.getDisplayName(TextStyle.NARROW, Locale.KOREAN)
    }
}
