package com.nexters.mytine.ui.home.week

import com.nexters.mytine.base.recyclerview.BaseItem
import java.time.DayOfWeek

internal class WeekItem(val dayOfWeek: DayOfWeek, override val itemId: String = dayOfWeek.name) : BaseItem
