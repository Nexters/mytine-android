package com.nexters.mytine.utils.extensions

import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

internal fun <T : Any, L : LiveData<T>> LifecycleOwner.observe(liveData: L, action: (T) -> Unit) = liveData.observe(
    this,
    Observer { action(it) }
)

internal fun <T : Any, L : LiveData<T>> Fragment.observe(liveData: L, action: (T) -> Unit) = liveData.observe(
    viewLifecycleOwner,
    Observer { action(it) }
)
