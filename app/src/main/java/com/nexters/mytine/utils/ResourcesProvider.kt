package com.nexters.mytine.utils

import androidx.annotation.StringRes

internal interface ResourcesProvider {
    fun getString(@StringRes resId: Int): String
}
