package com.nexters.mytine.ui.onboarding

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import com.nexters.mytine.base.viewmodel.BaseViewModel
import com.nexters.mytine.utils.LiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class OnBoardingViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : BaseViewModel() {
    var isLast = MutableLiveData<Boolean>().apply { value = false }
    var skipContents = MutableLiveData<String>().apply { value = "다음" }

    val isSkip = LiveEvent<Unit>()

    fun startOnBoarding() {
        if (!sharedPreferences.getBoolean(KEY_ON_BOARDING, false)) {
            sharedPreferences.edit().putBoolean(KEY_ON_BOARDING, true).apply()
        } else {
            startProutine()
        }
    }

    fun setSkipContentsValue() {
        skipContents.value = if (isLast.value!!) "프루틴 시작하기" else "다음"
    }

    fun onClickSkip() {
        if (isLast.value!!) {
            startProutine()
        } else {
            isSkip.value = Unit
        }
    }

    fun startProutine() {
        navDirections.value = OnBoardingFragmentDirections.actionOnBoardingFragmentToHomeFragment()
    }

    companion object {
        const val KEY_ON_BOARDING = "KEY_ON_BOARDING"
    }
}
