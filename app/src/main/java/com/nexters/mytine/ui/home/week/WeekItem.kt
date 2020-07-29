package com.nexters.mytine.ui.home.week

import com.nexters.mytine.base.recyclerview.BaseItem
import java.time.LocalDate

internal class WeekItem(val date: LocalDate, override val itemId: String = date.dayOfMonth.toString()) : BaseItem
