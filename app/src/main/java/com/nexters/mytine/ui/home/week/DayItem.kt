package com.nexters.mytine.ui.home.week

import java.time.LocalDate

data class DayItem(
    val date: LocalDate,
    val isRetrospectWrite: Boolean
)
