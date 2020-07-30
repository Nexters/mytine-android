package com.nexters.mytine.ui.home.icongroup.icon

import com.nexters.mytine.base.recyclerview.BaseItem
import com.nexters.mytine.data.entity.Routine

internal data class IconItem(val icon: Routine, override val itemId: String = icon.realId) : BaseItem
