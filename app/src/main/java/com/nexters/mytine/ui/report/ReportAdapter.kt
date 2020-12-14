package com.nexters.mytine.ui.report

import androidx.databinding.ViewDataBinding
import com.nexters.mytine.R
import com.nexters.mytine.base.recyclerview.BaseListAdapter
import com.nexters.mytine.base.recyclerview.BaseViewHolder
import com.nexters.mytine.databinding.ItemReportAllBinding
import com.nexters.mytine.ui.report.routine.ReportViewHolder

internal class ReportAdapter : BaseListAdapter<ReportItems>() {
    override fun getItemViewTypeByItem(item: ReportItems): Int {
        return when (item) {
            is ReportItems.EmptyItems -> R.layout.item_report_empty
            is ReportItems.Report -> R.layout.item_report_all
        }
    }

    override fun createViewHolder(binding: ViewDataBinding, viewType: Int): BaseViewHolder<ReportItems> {
        return when (viewType) {
            R.layout.item_report_all -> ReportViewHolder(binding as ItemReportAllBinding, viewModel as ReportViewModel) as BaseViewHolder<ReportItems>
            else -> super.createViewHolder(binding, viewType)
        }
    }
}
