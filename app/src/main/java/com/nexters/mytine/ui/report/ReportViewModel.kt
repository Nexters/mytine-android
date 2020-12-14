package com.nexters.mytine.ui.report

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nexters.mytine.base.viewmodel.BaseViewModel
import com.nexters.mytine.data.entity.Routine
import com.nexters.mytine.data.repository.RoutineRepository
import com.nexters.mytine.ui.report.routine.ReportRoutineItem
import com.nexters.mytine.utils.navigation.BackDirections
import kotlinx.coroutines.launch
import java.time.LocalDate

internal class ReportViewModel @ViewModelInject constructor(
    private val routineRepository: RoutineRepository
) : BaseViewModel() {
    val reportItems = MutableLiveData<List<ReportItems>>()

    init {
        viewModelScope.launch {
            val preMonthValue = 1L
            routineRepository.getsStartDate()?.let { startDate ->
                val preMonthDate = LocalDate.now().minusMonths(preMonthValue)
                val preMonthDateLastDayOfMonth = preMonthDate.withDayOfMonth(preMonthDate.lengthOfMonth())
                val routineList = loadRoutineByStatus(startDate, preMonthDateLastDayOfMonth, Routine.Status.SUCCESS)
                reportItems.value =
                    if (!routineList.isNullOrEmpty()) routineList
                    else listOf(ReportItems.EmptyItems())
            }
        }
    }

    fun onClickBack() {
        navDirections.value = BackDirections()
    }

    private suspend fun loadRoutineByStatus(to: LocalDate, from: LocalDate, status: Routine.Status) =
        routineRepository.getRoutinesByStatus(to, from, status)
            .groupBy {
                val startDayOfMonth = 1
                it.date.withDayOfMonth(startDayOfMonth)
            }
            .map { map ->
                val value = map.value
                    .groupBy { it.id }
                    .values.toList()
                    .sortedByDescending { it.size }
                    .filterIndexed { idx, _ -> idx < MAX_RANK }
                    .mapIndexed { idx, list ->
                        val idxGap = 1
                        ReportRoutineItem(idx + idxGap, list.first())
                    }
                ReportItems.Report(map.key, value)
            }
            .sortedByDescending { it.date }

    companion object {
        private const val MAX_RANK = 3
    }
}
