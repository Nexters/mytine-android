package com.nexters.mytine.ui.home

import com.nexters.mytine.base.BaseViewModel

internal class HomeViewModel : BaseViewModel() {
    fun onClickWrite() {
        navDirections.value = HomeFragmentDirections.actionHomeFragmentToWriteFragment()
    }
}
