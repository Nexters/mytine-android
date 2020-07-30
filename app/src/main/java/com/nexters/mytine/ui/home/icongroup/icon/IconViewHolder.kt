package com.nexters.mytine.ui.home.icongroup.icon

import com.nexters.mytine.base.recyclerview.BaseViewHolder
import com.nexters.mytine.data.entity.Routine
import com.nexters.mytine.databinding.ItemHomeRoutineGroupIconGroupIconBinding
import com.nexters.mytine.ui.home.HomeViewModel

internal class IconViewHolder(
    val binding: ItemHomeRoutineGroupIconGroupIconBinding,
    val viewModel: HomeViewModel
) : BaseViewHolder<IconItem>(binding) {

    override fun bind(item: IconItem) {
        when (item.icon.status) {
            Routine.Status.ENABLE, Routine.Status.SUCCESS -> super.bind(item)
        }
    }
}
