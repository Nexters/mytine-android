package com.nexters.mytine.ui.home.weekofmonth

import com.nexters.mytine.base.recyclerview.BaseItem
import com.nexters.mytine.ui.home.WeekOfMonth

data class WeekOfMonthItem(
    val weekOfMonth: WeekOfMonth,
    override val itemId: String = weekOfMonth.id
) : BaseItem
