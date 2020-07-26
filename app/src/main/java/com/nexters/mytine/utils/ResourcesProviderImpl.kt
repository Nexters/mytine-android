package com.nexters.mytine.utils

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

internal class ResourcesProviderImpl @Inject constructor(@ApplicationContext private val context: Context) : ResourcesProvider {
    override fun getString(resId: Int): String {
        return context.getString(resId)
    }
}
