package com.nexters.mytine.utils.databinding

import android.view.View
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.airbnb.lottie.LottieAnimationView

@BindingAdapter("android:visibility")
internal fun setVisibility(view: View, isVisible: Boolean) {
    view.isInvisible = !isVisible
}

@BindingAdapter("isVisible")
internal fun setIsVisible(view: View, isVisible: Boolean) {
    view.isVisible = isVisible
}

@BindingAdapter("setLottieAnimation")
internal fun setLottieAnimation(view: View, src: String) {
    (view as LottieAnimationView).setAnimation(src)
}
