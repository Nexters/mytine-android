package com.nexters.mytine.ui.report

import com.nexters.mytine.base.recyclerview.BaseItem
import com.nexters.mytine.ui.report.routine.ReportRoutineItem
import java.time.LocalDate

sealed class ReportItems(override val itemId: String) : BaseItem {
    class EmptyItems : ReportItems("empty")
    internal data class Report(
        val date: LocalDate,
        val routineList: List<ReportRoutineItem>
    ) : ReportItems(date.toString())
}
