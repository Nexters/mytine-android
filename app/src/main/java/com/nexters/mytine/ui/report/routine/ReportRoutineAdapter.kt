package com.nexters.mytine.ui.report.routine

import com.nexters.mytine.R
import com.nexters.mytine.base.recyclerview.BaseListAdapter

internal class ReportRoutineAdapter : BaseListAdapter<ReportRoutineItem>() {
    override fun getItemViewTypeByItem(item: ReportRoutineItem): Int {
        return R.layout.item_report_routine
    }
}
