package com.nexters.mytine.ui.onboarding

import com.nexters.mytine.R
import com.nexters.mytine.base.recyclerview.BaseListAdapter

internal class PagerRecyclerViewAdapter : BaseListAdapter<OnBoardingItem>() {
    override fun getItemViewTypeByItem(item: OnBoardingItem): Int {
        return when (item) {
            is OnBoardingItem.OnBoarding -> R.layout.item_on_boarding
        }
    }
}
