package com.nexters.mytine.ui.report.month

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nexters.mytine.base.viewmodel.BaseViewModel
import com.nexters.mytine.data.entity.Routine
import com.nexters.mytine.data.repository.RoutineRepository
import com.nexters.mytine.ui.report.routine.ReportRoutineItem
import com.nexters.mytine.utils.navigation.BackDirections
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
internal class ReportMonthViewModel @Inject constructor(
    private val routineRepository: RoutineRepository
) : BaseViewModel() {
    val date = MutableLiveData<LocalDate>()
    val maxAchieveRoutineList = MutableLiveData<List<ReportRoutineItem>>()
    val minAchieveRoutineList = MutableLiveData<List<ReportRoutineItem>>()

    init {
        viewModelScope.launch {
            val startDayOfMonth = 1
            navArgs<ReportMonthFragmentArgs>()
                .map { LocalDate.of(it.year, it.month, startDayOfMonth) }
                .collect { localDate ->
                    date.value = localDate

                    val from = localDate.withDayOfMonth(localDate.lengthOfMonth())
                    maxAchieveRoutineList.value = loadRoutineByStatus(localDate, from, Routine.Status.SUCCESS)
                    minAchieveRoutineList.value = loadRoutineByStatus(localDate, from, Routine.Status.ENABLE)
                }
        }
    }

    fun onClickBack() {
        navDirections.value = BackDirections()
    }

    private suspend fun loadRoutineByStatus(to: LocalDate, from: LocalDate, status: Routine.Status) =
        routineRepository.getRoutinesByStatus(to, from, status)
            .groupBy { it.id }
            .values.toList()
            .sortedByDescending { it.size }
            .filterIndexed { idx, _ -> idx < MAX_RANK }
            .mapIndexed { idx, list ->
                val idxGap = 1
                ReportRoutineItem(idx + idxGap, list.first())
            }

    companion object {
        private const val MAX_RANK = 3
    }
}
