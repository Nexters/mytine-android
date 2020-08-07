package com.nexters.mytine.ui.home.icongroup

import androidx.databinding.ViewDataBinding
import com.nexters.mytine.R
import com.nexters.mytine.base.recyclerview.BaseListAdapter
import com.nexters.mytine.base.recyclerview.BaseViewHolder
import com.nexters.mytine.databinding.ItemHomeIconGroupBinding
import com.nexters.mytine.ui.home.HomeViewModel

internal class IconGroupAdapter : BaseListAdapter<IconGroupItem>() {
    override fun getItemViewTypeByItem(item: IconGroupItem): Int {
        return R.layout.item_home_icon_group
    }

    override fun createViewHolder(binding: ViewDataBinding, viewType: Int): BaseViewHolder<IconGroupItem> {
        return IconGroupViewHolder(binding as ItemHomeIconGroupBinding, viewModel as HomeViewModel)
    }
}
