package com.nexters.mytine.ui.splash

import androidx.lifecycle.viewModelScope
import com.nexters.mytine.base.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@HiltViewModel
internal class SplashViewModel @Inject constructor() : BaseViewModel() {
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
