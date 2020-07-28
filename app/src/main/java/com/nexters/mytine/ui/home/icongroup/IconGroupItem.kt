package com.nexters.mytine.ui.home.icongroup

import com.nexters.mytine.base.recyclerview.BaseItem
import com.nexters.mytine.ui.home.icongroup.icon.IconItem

internal class IconGroupItem(val icons: List<IconItem>, override val itemId: String = icons.first().icon.id) : BaseItem
