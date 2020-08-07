package com.nexters.mytine.utils.databinding

import android.view.View
import androidx.core.view.isInvisible
import androidx.databinding.BindingAdapter

@BindingAdapter("android:visibility")
internal fun setVisibility(view: View, isVisible: Boolean) {
    view.isInvisible = !isVisible
}
