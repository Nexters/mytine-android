package com.nexters.mytine.ui.home.icongroup

import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.nexters.mytine.base.recyclerview.BaseViewHolder
import com.nexters.mytine.databinding.ItemHomeRoutineGroupIconGroupBinding
import com.nexters.mytine.ui.home.HomeViewModel
import com.nexters.mytine.ui.home.icongroup.icon.IconAdapter

internal class IconGroupViewHolder(
    val binding: ItemHomeRoutineGroupIconGroupBinding,
    val viewModel: HomeViewModel
) : BaseViewHolder<IconGroupItem>(binding) {

    private val iconAdapter = IconAdapter()

    init {
        binding.rvIcon.run {
            layoutManager = FlexboxLayoutManager(context, FlexDirection.ROW, FlexWrap.NOWRAP).apply {
                justifyContent = JustifyContent.SPACE_BETWEEN
            }
            adapter = iconAdapter
        }
        iconAdapter.setViewHolderViewModel(viewModel)
    }

    override fun bind(item: IconGroupItem) {
        super.bind(item)

        iconAdapter.submitList(item.icons)
    }
}
