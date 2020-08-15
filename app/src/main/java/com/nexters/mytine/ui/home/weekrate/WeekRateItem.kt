package com.nexters.mytine.ui.home.weekrate

import com.nexters.mytine.base.recyclerview.BaseItem

internal class WeekRateItem(val dayRate: DayRateItem, override val itemId: String = dayRate.date.dayOfMonth.toString()) : BaseItem
