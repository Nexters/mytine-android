package com.nexters.mytine.ui.write

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nexters.mytine.base.viewmodel.BaseViewModel
import com.nexters.mytine.data.repository.RoutineRepository
import com.nexters.mytine.utils.navigation.BackDirections
import kotlinx.coroutines.launch
import java.time.DayOfWeek

internal class WriteViewModel @ViewModelInject constructor(
    private val routineRepository: RoutineRepository
) : BaseViewModel() {

    val emoji = MutableLiveData<String>()
    val name = MutableLiveData<String>()
    val goal = MutableLiveData<String>()
    val weekItems = MutableLiveData<List<WeekItem>>().apply {
        value = DayOfWeek.values().map { WeekItem(it) }
    }

    fun onClickWrite() {
        val emoji = emoji.value
        val name = name.value
        val goal = goal.value
        val selectedDayOfWeeks = weekItems.value?.filter { it.selected }?.map { it.dayOfWeek }

        if (emoji.isNullOrBlank() || name.isNullOrBlank() || goal.isNullOrBlank() || selectedDayOfWeeks.isNullOrEmpty()) {
            toast.value = "모든것을 다 채워넣어야함"
            return
        }

        viewModelScope.launch {
            routineRepository.addRoutine(
                emoji = emoji,
                name = name,
                goal = goal,
                selectedDayOfWeeks = selectedDayOfWeeks
            )
        }

        navDirections.value = BackDirections()
    }

    fun onClickWeekItem(weekItem: WeekItem) {
        val items = weekItems.value
        if (items != null) {
            weekItems.value = items.map {
                if (it.dayOfWeek == weekItem.dayOfWeek) {
                    it.copy(selected = !it.selected)
                } else {
                    it
                }
            }
        }
    }
}
