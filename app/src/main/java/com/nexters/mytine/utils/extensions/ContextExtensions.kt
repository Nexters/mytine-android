package com.nexters.mytine.utils.extensions

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment

internal fun Context?.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

internal fun Fragment.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    context?.toast(message, duration)
}
