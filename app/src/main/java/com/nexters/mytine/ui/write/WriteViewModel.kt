package com.nexters.mytine.ui.write

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nexters.mytine.base.viewmodel.BaseViewModel
import com.nexters.mytine.data.entity.Routine
import com.nexters.mytine.data.repository.RoutineRepository
import com.nexters.mytine.utils.LiveEvent
import com.nexters.mytine.utils.extensions.combineLatest
import com.nexters.mytine.utils.navigation.BackDirections
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate

internal class WriteViewModel @ViewModelInject constructor(
    private val routineRepository: RoutineRepository
) : BaseViewModel() {

    val isEditMode = MutableLiveData<Boolean>().apply { value = false }
    val emoji = MutableLiveData<String>()
    val name = MutableLiveData<String>()
    val goal = MutableLiveData<String>()
    val weekItems = MutableLiveData<List<WeekItem>>().apply {
        value = DayOfWeek.values().map { WeekItem(it) }
    }

    val showErrorEmoji = createErrorCheckLiveData(emoji) { !it.isNullOrBlank() }
    val showErrorName = createErrorCheckLiveData(name) { !it.isNullOrBlank() }
    val showErrorWeek = createErrorCheckLiveData(weekItems) { !it.isNullOrEmpty() }

    val enableWrite = combineLatest(
        showErrorEmoji,
        showErrorName,
        showErrorWeek
    ) { errorEmoji, errorName, errorWeek -> !errorEmoji && !errorName && !errorWeek }

    val showBackDialog = LiveEvent<Unit>()

    private val backPressedChannel = BroadcastChannel<Unit>(1)
    private val saveClickChannel = BroadcastChannel<Unit>(1)
    private val deleteClickChannel = BroadcastChannel<Unit>(1)

    init {
        viewModelScope.launch {
            navArgs<WriteFragmentArgs>()
                .map { it.routineId }
                .filter { it.isNotEmpty() }
                .flatMapLatest { routineRepository.flowRoutinesById(it, LocalDate.now().with(DayOfWeek.MONDAY)) }
                .filter { it.isNotEmpty() }
                .collect { routines ->
                    isEditMode.value = true

                    routines.first().let {
                        emoji.value = it.emoji
                        name.value = it.name
                        goal.value = it.goal
                    }

                    weekItems.value = routines.map { WeekItem(it.date.dayOfWeek, it.status != Routine.Status.DISABLE) }
                }
        }

        viewModelScope.launch {
            saveClickChannel.asFlow()
                .flatMapLatest { navArgs<WriteFragmentArgs>() }
                .collect { navArgs ->
                    val emoji = emoji.value
                    val name = name.value
                    val goal = goal.value
                    val selectedDayOfWeeks = weekItems.value?.filter { it.selected }?.map { it.dayOfWeek }

                    if (emoji.isNullOrBlank() || name.isNullOrBlank() || selectedDayOfWeeks.isNullOrEmpty()) {
                        showErrorEmoji.value = emoji.isNullOrBlank()
                        showErrorName.value = name.isNullOrBlank()
                        showErrorWeek.value = selectedDayOfWeeks.isNullOrEmpty()
                        return@collect
                    }

                    routineRepository.updateRoutine(
                        emoji = emoji,
                        name = name,
                        goal = goal,
                        selectedDayOfWeeks = selectedDayOfWeeks,
                        id = navArgs.routineId
                    )

                    navDirections.value = BackDirections()
                }
        }

        viewModelScope.launch {
            deleteClickChannel.asFlow()
                .flatMapLatest { navArgs<WriteFragmentArgs>() }
                .map { it.routineId }
                .collect {
                    routineRepository.deleteRoutinesById(it)

                    navDirections.value = BackDirections()
                }
        }

        viewModelScope.launch {
            backPressedChannel.asFlow()
                .flatMapLatest { navArgs<WriteFragmentArgs>().map { it.routineId } }
                .collect { routineId ->
                    if (routineId.isBlank()) {
                        showBackDialog.value = Unit
                    } else {
                        navDirections.value = BackDirections()
                    }
                }
        }
    }

    fun onBackPressed() {
        viewModelScope.launch { backPressedChannel.send(Unit) }
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

    fun onClickSave() {
        viewModelScope.launch { saveClickChannel.send(Unit) }
    }

    fun onClickDelete() {
        viewModelScope.launch { deleteClickChannel.send(Unit) }
    }

    fun onClickLeave() {
        navDirections.value = BackDirections()
    }

    private fun <T> createErrorCheckLiveData(source: LiveData<T>, check: (T) -> Boolean): MediatorLiveData<Boolean> {
        return MediatorLiveData<Boolean>().apply {
            addSource(source) {
                if (check(it)) {
                    value = false
                }
            }
        }
    }
}
