package com.nexters.mytine.ui.home.icongroup.icon

import androidx.databinding.ViewDataBinding
import com.nexters.mytine.R
import com.nexters.mytine.base.recyclerview.BaseListAdapter
import com.nexters.mytine.base.recyclerview.BaseViewHolder
import com.nexters.mytine.databinding.ItemHomeRoutineGroupIconGroupIconBinding
import com.nexters.mytine.ui.home.HomeViewModel

internal class IconAdapter : BaseListAdapter<IconItem>() {
    override fun getItemViewTypeByItem(item: IconItem): Int {
        return R.layout.item_home_routine_group_icon_group_icon
    }

    override fun createViewHolder(binding: ViewDataBinding, viewType: Int): BaseViewHolder<IconItem> {
        return IconViewHolder(binding as ItemHomeRoutineGroupIconGroupIconBinding, viewModel as HomeViewModel)
    }
}
