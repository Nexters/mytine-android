package com.nexters.mytine.ui.write

import com.nexters.mytine.R
import com.nexters.mytine.base.recyclerview.BaseListAdapter

internal class WeekAdapter : BaseListAdapter<WeekItem>() {
    override fun getItemViewTypeByItem(item: WeekItem): Int {
        return R.layout.item_write_week
    }
}
