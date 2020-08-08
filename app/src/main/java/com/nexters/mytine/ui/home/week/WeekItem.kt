package com.nexters.mytine.ui.home.week

import com.nexters.mytine.base.recyclerview.BaseItem

internal class WeekItem(val day: DayItem, override val itemId: String = day.date.dayOfMonth.toString()) : BaseItem
