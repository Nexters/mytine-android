package com.nexters.mytine.ui.report

import com.nexters.mytine.R
import com.nexters.mytine.base.recyclerview.BaseListAdapter

internal class ReportAdapter : BaseListAdapter<ReportItems>() {
    override fun getItemViewTypeByItem(item: ReportItems): Int {
        return when (item) {
            is ReportItems.EmptyItems -> R.layout.item_report_empty
        }
    }
}
