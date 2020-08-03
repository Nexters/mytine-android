package com.nexters.mytine.ui.home

import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.nexters.mytine.base.recyclerview.BaseViewHolder
import com.nexters.mytine.databinding.ItemHomeRoutineGroupBinding
import com.nexters.mytine.ui.home.icongroup.IconGroupAdapter
import com.nexters.mytine.ui.home.week.WeekAdapter

internal class RoutineGroupViewHolder(
    val binding: ItemHomeRoutineGroupBinding,
    val viewModel: HomeViewModel
) : BaseViewHolder<HomeItems>(binding) {

    private val weekAdapter = WeekAdapter()
    private val iconGroupAdapter = IconGroupAdapter()

    init {
        binding.rvWeek.run {
            layoutManager = FlexboxLayoutManager(context, FlexDirection.ROW, FlexWrap.NOWRAP).apply {
                justifyContent = JustifyContent.SPACE_BETWEEN
            }
            adapter = weekAdapter
        }
        weekAdapter.setViewHolderViewModel(viewModel)

        binding.rvIconGroup.run {
            layoutManager = LinearLayoutManager(context)
            adapter = iconGroupAdapter
        }
        iconGroupAdapter.setViewHolderViewModel(viewModel)
    }

    override fun bind(item: HomeItems) {
        super.bind(item)

        if (item is HomeItems.RoutineGroupItem) {
            weekAdapter.submitList(item.weekItems)
            iconGroupAdapter.submitList(item.iconGroupItems)
        }
    }
}
