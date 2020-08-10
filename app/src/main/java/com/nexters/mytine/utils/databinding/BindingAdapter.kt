package com.nexters.mytine.utils.databinding

import android.view.View
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter

@BindingAdapter("android:visibility")
internal fun setVisibility(view: View, isVisible: Boolean) {
    view.isInvisible = !isVisible
}

@BindingAdapter("isVisible")
internal fun setIsVisible(view: View, isVisible: Boolean) {
    view.isVisible = isVisible
}
