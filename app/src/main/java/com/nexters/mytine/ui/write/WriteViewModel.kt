package com.nexters.mytine.ui.write

import com.nexters.mytine.base.BaseViewModel
import com.nexters.mytine.utils.navigation.BackDirections

internal class WriteViewModel : BaseViewModel() {
    fun onClickWrite() {
        navDirections.value = BackDirections()
    }
}
