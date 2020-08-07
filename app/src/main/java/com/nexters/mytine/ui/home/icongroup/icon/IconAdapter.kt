package com.nexters.mytine.ui.home.icongroup.icon

import com.nexters.mytine.R
import com.nexters.mytine.base.recyclerview.BaseListAdapter

internal class IconAdapter : BaseListAdapter<IconItem>() {
    override fun getItemViewTypeByItem(item: IconItem): Int {
        return R.layout.item_home_icon_group_icon
    }
}
