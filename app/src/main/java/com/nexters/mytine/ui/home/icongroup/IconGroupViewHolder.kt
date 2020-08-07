package com.nexters.mytine.ui.home.icongroup

import androidx.recyclerview.widget.GridLayoutManager
import com.nexters.mytine.base.recyclerview.BaseViewHolder
import com.nexters.mytine.databinding.ItemHomeRoutineGroupIconGroupBinding
import com.nexters.mytine.ui.home.HomeViewModel
import com.nexters.mytine.ui.home.icongroup.icon.IconAdapter

internal class IconGroupViewHolder(
    val binding: ItemHomeRoutineGroupIconGroupBinding,
    val viewModel: HomeViewModel
) : BaseViewHolder<IconGroupItem>(binding) {
    companion object {
        const val SPAN_SIZE = 7
    }

    private val iconAdapter = IconAdapter()

    init {
        binding.rvIcon.run {
            layoutManager = GridLayoutManager(context, SPAN_SIZE)
            adapter = iconAdapter
        }
        iconAdapter.setViewHolderViewModel(viewModel)
    }

    override fun bind(item: IconGroupItem) {
        super.bind(item)

        iconAdapter.submitList(item.icons)
    }
}
