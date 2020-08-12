package com.nexters.mytine.ui.home

internal interface ItemTouchHelperListener {
    fun onItemSwipe(position: Int, direction: Int)
    fun isRightSwipeable(position: Int): Boolean
    fun isLeftSwipeable(position: Int): Boolean
}
