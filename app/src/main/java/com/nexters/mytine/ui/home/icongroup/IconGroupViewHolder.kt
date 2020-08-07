package com.nexters.mytine.ui.home.icongroup

import androidx.recyclerview.widget.LinearLayoutManager
import com.nexters.mytine.base.recyclerview.BaseViewHolder
import com.nexters.mytine.databinding.ItemHomeIconGroupBinding
import com.nexters.mytine.ui.home.HomeViewModel
import com.nexters.mytine.ui.home.icongroup.icon.IconAdapter

internal class IconGroupViewHolder(
    val binding: ItemHomeIconGroupBinding,
    val viewModel: HomeViewModel
) : BaseViewHolder<IconGroupItem>(binding) {

    private val iconAdapter = IconAdapter()

    init {
        binding.rvIcon.run {
            layoutManager = LinearLayoutManager(context)
            adapter = iconAdapter
        }
        iconAdapter.setViewHolderViewModel(viewModel)
    }

    override fun bind(item: IconGroupItem) {
        super.bind(item)

        iconAdapter.submitList(item.icons)
    }
}
