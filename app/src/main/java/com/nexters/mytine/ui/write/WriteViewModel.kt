package com.nexters.mytine.ui.write

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
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.akarnokd.kotlin.flow.BehaviorSubject
import hu.akarnokd.kotlin.flow.PublishSubject
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@HiltViewModel
internal class WriteViewModel @Inject constructor(
    private val routineRepository: RoutineRepository
) : BaseViewModel() {

    val isEditMode = MutableLiveData<Boolean>().apply { value = false }
    val emoji = MutableLiveData<String>()
    val name = MutableLiveData<String>()
    val goal = MutableLiveData<String>()
    val weekItems = MutableLiveData<List<WeekItem>>().apply {
        value = DayOfWeek.values().map { WeekItem(it) }
    }

    val isChangedEdit = combineLatest(
        emoji,
        name,
        goal,
        weekItems
    ) { _, _, _, _ ->
        if (isEditMode.value == true) {
            routineChanged(routinesChannel.value)
        } else {
            true
        }
    }

    val showErrorEmoji = createErrorCheckLiveData(emoji) { !it.isNullOrBlank() }
    val showErrorName = createErrorCheckLiveData(name) { !it.isNullOrBlank() }

    val showErrorWeek = createErrorCheckLiveData(weekItems) { !it.isNullOrEmpty() }

    val enableWrite = combineLatest(
        showErrorEmoji,
        showErrorName,
        showErrorWeek,
        isChangedEdit
    ) { errorEmoji, errorName, errorWeek, isChangedEdit -> !errorEmoji && !errorName && !errorWeek && isChangedEdit }
    val showBackDialog = LiveEvent<Unit>()

    val showDeleteDialog = LiveEvent<Unit>()

    private val backPressedChannel = PublishSubject<Unit>()
    private val saveClickChannel = PublishSubject<Unit>()
    private val deleteClickChannel = PublishSubject<Unit>()
    private val deleteDialogPositiveClickChannel = PublishSubject<Unit>()
    private val routinesChannel = BehaviorSubject<List<Routine>>()

    init {
        viewModelScope.launch {
            navArgs<WriteFragmentArgs>()
                .map { it.routineId }
                .filter { it.isNotEmpty() }
                .flatMapLatest { routineRepository.flowRoutinesById(it, LocalDate.now().with(DayOfWeek.MONDAY)) }
                .filter { it.isNotEmpty() }
                .collect { routines ->
                    isEditMode.value = true
                    routinesChannel.emit(routines)

                    routines.first().let {
                        emoji.value = it.emoji
                        name.value = it.name
                        goal.value = it.goal
                    }

                    weekItems.value = routines.map { WeekItem(it.date.dayOfWeek, it.status != Routine.Status.DISABLE) }
                }
        }

        viewModelScope.launch {
            saveClickChannel
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
            deleteClickChannel
                .collect {
                    showDeleteDialog.value = Unit
                }
        }

        viewModelScope.launch {
            backPressedChannel
                .flatMapLatest { navArgs<WriteFragmentArgs>() }
                .map { it.routineId }
                .flatMapLatest {
                    if (it.isBlank()) {
                        flow { emit(true) }
                    } else {
                        routinesChannel
                            .map { routines -> routineChanged(routines) }
                    }
                }
                .collect {
                    if (it) {
                        showBackDialog.value = Unit
                    } else {
                        navDirections.value = BackDirections()
                    }
                }
        }

        viewModelScope.launch {
            deleteDialogPositiveClickChannel
                .flatMapLatest { navArgs<WriteFragmentArgs>() }
                .map { it.routineId }
                .collect {
                    routineRepository.deleteRoutinesById(it)

                    navDirections.value = BackDirections()
                }
        }
    }

    private fun routineChanged(routines: List<Routine>): Boolean {
        val contentChanged = routines.first().let { emoji.value != it.emoji || name.value != it.name || goal.value != it.goal }
        val weekItemChanged = weekItems.value != routines.map { WeekItem(it.date.dayOfWeek, it.status != Routine.Status.DISABLE) }

        return contentChanged || weekItemChanged
    }

    fun onBackPressed() {
        viewModelScope.launch { backPressedChannel.emit(Unit) }
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
        viewModelScope.launch { saveClickChannel.emit(Unit) }
    }

    fun onClickDelete() {
        viewModelScope.launch { deleteClickChannel.emit(Unit) }
    }

    fun onClickDeleteDialogPositiveButton() {
        viewModelScope.launch { deleteDialogPositiveClickChannel.emit(Unit) }
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
