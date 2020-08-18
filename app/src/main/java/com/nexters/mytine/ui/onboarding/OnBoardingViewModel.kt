package com.nexters.mytine.ui.onboarding

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.nexters.mytine.base.viewmodel.BaseViewModel

internal class OnBoardingViewModel @ViewModelInject constructor() : BaseViewModel() {
    var isLast = MutableLiveData<Boolean>().apply { value = false }
    var skipContents = MutableLiveData<String>().apply { value = "다음" }

    fun setSkipContentsValue() {
        skipContents.value = if (isLast.value!!) "프루틴 시작하기" else "다음"
    }
}
