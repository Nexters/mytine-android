package com.nexters.mytine.utils.navigation

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.navigation.NavDirections

internal data class BackDirections(
    @IdRes val destinationId: Int = -1,
    val inclusive: Boolean = false
) : NavDirections {
    override val actionId: Int
        get() = throw IllegalArgumentException("BackDirections should only be handled by BaseFramgent")
    override val arguments: Bundle
        get() = throw IllegalArgumentException("BackDirections should only be handled by BaseFramgent")
}
