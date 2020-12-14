package com.nexters.mytine.ui.report.routine

import androidx.recyclerview.widget.LinearLayoutManager
import com.nexters.mytine.base.recyclerview.BaseViewHolder
import com.nexters.mytine.databinding.ItemReportAllBinding
import com.nexters.mytine.ui.report.ReportItems
import com.nexters.mytine.ui.report.ReportViewModel

internal class ReportViewHolder(
    private val binding: ItemReportAllBinding,
    private val vm: ReportViewModel
) : BaseViewHolder<ReportItems.Report>(binding) {
    private val reportMonthAdapter = ReportRoutineAdapter().apply {
        setViewHolderViewModel(vm)
    }

    init {
        binding.maxAchieveRoutineReportLayout.achieveRoutineRv.run {
            layoutManager = LinearLayoutManager(context)
            adapter = reportMonthAdapter
        }
    }

    override fun bind(item: ReportItems.Report) {
        super.bind(item)
        reportMonthAdapter.submitList(item.routineList)
    }
}
