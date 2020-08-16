package com.nexters.mytine.ui.home

import java.time.LocalDate

data class WeekOfMonth(
    val startDate: LocalDate,
    val endDate: LocalDate,
    val id: String = startDate.toString() + endDate.toString()
) {
    fun startDateStr(): String {
        return "${startDate.monthValue}월 ${startDate.dayOfMonth}일"
    }

    fun endDateStr(): String {
        return "${endDate.monthValue}월 ${endDate.dayOfMonth}일"
    }

    fun isSelectedWeek(currentWeek: WeekOfMonth) = (startDate == currentWeek.startDate)
}
