package com.nexters.mytine.ui.home

interface TestCallbackListener {
    fun isRightSwipeable(position: Int): Boolean
    fun isLeftSwipeable(position: Int): Boolean

    fun onLeftClicked(position: Int)
    fun onRightClicked(position: Int)
}
