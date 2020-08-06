package com.nexters.mytine.ui.write

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nexters.mytine.R
import com.nexters.mytine.base.viewmodel.BaseViewModel
import com.nexters.mytine.data.entity.Routine
import com.nexters.mytine.data.repository.RoutineRepository
import com.nexters.mytine.utils.ResourcesProvider
import com.nexters.mytine.utils.navigation.BackDirections
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.DayOfWeek

internal class WriteViewModel @ViewModelInject constructor(
    private val resourcesProvider: ResourcesProvider,
    private val routineRepository: RoutineRepository
) : BaseViewModel() {

    val emoji = MutableLiveData<String>()
    val name = MutableLiveData<String>()
    val goal = MutableLiveData<String>()
    val weekItems = MutableLiveData<List<WeekItem>>().apply {
        value = DayOfWeek.values().map { WeekItem(it) }
    }

    private val writeClicked = BroadcastChannel<Unit>(1)

    init {
        viewModelScope.launch {
            navArgs<WriteFragmentArgs>()
                .map { it.routineId }
                .filter { it.isNotEmpty() }
                .flatMapLatest { routineRepository.flowRoutinesById(it) }
                .filter { it.isNotEmpty() }
                .collect { routines ->
                    routines.first().let {
                        emoji.value = it.emoji
                        name.value = it.name
                        goal.value = it.goal
                    }

                    weekItems.value = routines.map { WeekItem(it.date.dayOfWeek, it.status != Routine.Status.DISABLE) }
                }
        }

        viewModelScope.launch {
            writeClicked.asFlow()
                .flatMapLatest { navArgsChannel.asFlow() }
                .collect { navArgs ->
                    val emoji = emoji.value
                    val name = name.value
                    val goal = goal.value
                    val selectedDayOfWeeks = weekItems.value?.filter { it.selected }?.map { it.dayOfWeek }

                    if (emoji.isNullOrBlank() || name.isNullOrBlank() || selectedDayOfWeeks.isNullOrEmpty()) {
                        toast.value = resourcesProvider.getString(R.string.write_empty_toast_message)
                        return@collect
                    }

                    viewModelScope.launch {
                        routineRepository.updateRoutine(
                            emoji = emoji,
                            name = name,
                            goal = goal,
                            selectedDayOfWeeks = selectedDayOfWeeks,
                            id = (navArgs as? WriteFragmentArgs)?.routineId ?: ""
                        )
                    }

                    navDirections.value = BackDirections()
                }
        }
    }

    fun onClickWrite() {
        viewModelScope.launch { writeClicked.send(Unit) }
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
