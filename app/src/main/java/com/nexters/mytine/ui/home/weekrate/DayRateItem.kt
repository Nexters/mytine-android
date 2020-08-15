package com.nexters.mytine.ui.home.weekrate

import com.nexters.mytine.R
import java.time.LocalDate

data class DayRateItem(
    val date: LocalDate,
    val rate: Float
) {
    companion object {
        private const val HIGH = 0.8
        private const val NORMAL = 0.5
    }

    fun isVisible() = date <= LocalDate.now()

    fun getRateColor() = when {
        rate >= HIGH -> R.color.minty_green
        rate >= NORMAL -> R.color.macaroni_and_cheese
        else -> R.color.warm_pink
    }
}
