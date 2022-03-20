package com.nexters.mytine.base.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavArgs
import androidx.navigation.NavDirections
import com.nexters.mytine.ui.EmptyNavArgs
import com.nexters.mytine.utils.LiveEvent
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch

internal abstract class BaseViewModel : ViewModel() {
    val toast = LiveEvent<String>()
    val navDirections = LiveEvent<NavDirections>()
    val navArgsChannel = MutableStateFlow<NavArgs>(EmptyNavArgs)

    fun navArgs(navArgs: NavArgs) {
        navArgsChannel.value = navArgs
    }

    inline fun <reified T : NavArgs> navArgs(): Flow<T> {
        return navArgsChannel.filterIsInstance()
    }
}
