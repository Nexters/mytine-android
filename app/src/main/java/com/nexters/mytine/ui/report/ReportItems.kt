package com.nexters.mytine.ui.report

import com.nexters.mytine.base.recyclerview.BaseItem

sealed class ReportItems : BaseItem {
    class EmptyItems(override val itemId: String = "empty") : ReportItems()
}
