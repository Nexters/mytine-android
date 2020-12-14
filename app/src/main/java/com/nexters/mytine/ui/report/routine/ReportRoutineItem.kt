package com.nexters.mytine.ui.report.routine

import com.nexters.mytine.base.recyclerview.BaseItem
import com.nexters.mytine.data.entity.Routine

internal class ReportRoutineItem(
    val rank: Int,
    val routine: Routine,
    override val itemId: String = "$rank ${routine.id}"
) : BaseItem
