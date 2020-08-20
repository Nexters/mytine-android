package com.nexters.mytine.ui.splash

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.nexters.mytine.base.viewmodel.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal class SplashViewModel @ViewModelInject constructor() : BaseViewModel() {
    companion object {
        const val DELAY_OF_SPLASH = 1000L
    }

    init {
        viewModelScope.launch {
            delay(DELAY_OF_SPLASH)
            navDirections.value = SplashFragmentDirections.actionSplashFragmentToOnBoardingFragment()
        }
    }
}
