package com.nexters.mytine.ui.onboarding

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.nexters.mytine.base.viewmodel.BaseViewModel

internal class OnBoardingViewModel @ViewModelInject constructor() : BaseViewModel() {
    var isLast = MutableLiveData<Boolean>().apply { false }
}
