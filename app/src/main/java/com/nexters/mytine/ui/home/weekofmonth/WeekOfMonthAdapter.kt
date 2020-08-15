package com.nexters.mytine.ui.home.weekofmonth

import com.nexters.mytine.R
import com.nexters.mytine.base.recyclerview.BaseListAdapter

internal class WeekOfMonthAdapter : BaseListAdapter<WeekOfMonthItem>() {
    override fun getItemViewTypeByItem(item: WeekOfMonthItem): Int {
        return R.layout.item_home_week_of_month
    }
}
