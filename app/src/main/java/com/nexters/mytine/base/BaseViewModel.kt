package com.nexters.mytine.base

import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import com.nexters.mytine.utils.LiveEvent

internal abstract class BaseViewModel : ViewModel() {
    val toast = LiveEvent<String>()
    val navDirections = LiveEvent<NavDirections>()
}
