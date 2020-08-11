package com.nexters.mytine.ui.home.weekrate

import com.nexters.mytine.R
import com.nexters.mytine.base.recyclerview.BaseListAdapter

internal class WeekRateAdapter: BaseListAdapter<WeekRateItem>() {
    override fun getItemViewTypeByItem(item: WeekRateItem): Int {
        return R.layout.item_week_rate
    }
}