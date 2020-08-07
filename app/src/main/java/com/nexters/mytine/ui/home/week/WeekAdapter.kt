package com.nexters.mytine.ui.home.week

import com.nexters.mytine.R
import com.nexters.mytine.base.recyclerview.BaseListAdapter

internal class WeekAdapter : BaseListAdapter<WeekItem>() {
    override fun getItemViewTypeByItem(item: WeekItem): Int {
        return R.layout.item_home_week
    }
}
