package com.nexters.mytine.ui.home

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.ItemTouchHelper
import com.nexters.mytine.base.viewmodel.BaseViewModel
import com.nexters.mytine.data.entity.Routine
import com.nexters.mytine.data.repository.RoutineRepository
import com.nexters.mytine.ui.home.icongroup.IconGroupItem
import com.nexters.mytine.ui.home.icongroup.icon.IconItem
import com.nexters.mytine.ui.home.week.DayItem
import com.nexters.mytine.ui.home.week.WeekItem
import com.nexters.mytine.ui.home.weekofmonth.WeekOfMonthItem
import com.nexters.mytine.ui.home.weekrate.DayRateItem
import com.nexters.mytine.ui.home.weekrate.WeekRateItem
import com.nexters.mytine.utils.LiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@HiltViewModel
internal class HomeViewModel @Inject constructor(
    private val routineRepository: RoutineRepository
) : BaseViewModel() {

    val weekItems = MutableLiveData<List<WeekItem>>()
    val weekRateItems = MutableLiveData<List<WeekRateItem>>()
    val iconGroupItems = MutableLiveData<List<IconGroupItem>>()
    val homeItems = MutableLiveData<List<HomeItems>>()
    val weekOfMonth = MutableLiveData<List<WeekOfMonthItem>>()
    val currentWeek = MutableLiveData<WeekOfMonth>()
    val expandClickEvent = MutableLiveData<Unit>()
    var itemSelectedListener: (LocalDate) -> Unit = {}

    private val dayChannel = ConflatedBroadcastChannel<LocalDate>()

    init {
        val now = LocalDate.now()

        viewModelScope.launch {
            dayChannel.send(now)
        }

        viewModelScope.launch {
            dayChannel.asFlow()
                .collect { weekItems.value = weekItems(it) }
        }

        viewModelScope.launch {
            dayChannel.asFlow()
                .flatMapLatest { date ->
                    routineRepository
                        .flowRoutinesByDate(
                            date.with(DayOfWeek.MONDAY),
                            date.with(DayOfWeek.SUNDAY)
                        )
                        .map { weekRateItems(date, it) }
                }
                .collect { weekRateItems.value = it }
        }

        viewModelScope.launch {
            dayChannel.asFlow()
                .flatMapLatest {
                    routineRepository.flowRoutinesByDate(
                        it.with(DayOfWeek.MONDAY),
                        it.with(DayOfWeek.SUNDAY)
                    )
                }
                .map { list ->
                    list.groupBy { it.date }
                        .map { IconGroupItem(it.value.map { routine -> IconItem(routine) }) }
                }
                .collect {
                    iconGroupItems.value = it
                }
        }

        viewModelScope.launch {
            combine(
                dayChannel.asFlow()
                    .flatMapLatest {
                        routineRepository.flowRoutines(it)
                    },
                dayChannel.asFlow()
                    .flatMapLatest {
                        routineRepository.flowRoutinesByDate(
                            it.with(DayOfWeek.MONDAY),
                            it.with(DayOfWeek.SUNDAY)
                        )
                    }
            ) { dateRoutines, weekRoutine ->
                mutableListOf<HomeItems>().apply {
                    add(HomeItems.TabBarItem())
                    if (weekRoutine.isNullOrEmpty())
                        add(HomeItems.EmptyRoutineItem())

                    val enableList = mutableListOf<Routine>()
                    val completedList = mutableListOf<Routine>()

                    dateRoutines.forEach {
                        if (it.status == Routine.Status.SUCCESS) {
                            completedList.add(it)
                        } else if (it.status == Routine.Status.ENABLE) {
                            enableList.add(it)
                        }
                    }

                    addAll(enableList.map { HomeItems.RoutineItem.EnabledRoutineItem(it) })
                    addAll(completedList.map { HomeItems.RoutineItem.CompletedRoutineItem(it) })
                }
            }.collect {
                homeItems.value = it
            }
        }

        viewModelScope.launch {
            dayChannel.asFlow().collect {
                currentWeek.value =
                    WeekOfMonth(it.with(DayOfWeek.MONDAY), it.with(DayOfWeek.SUNDAY))
            }
        }
    }

    fun onClickWrite() {
        navDirections.value = HomeFragmentDirections.actionHomeFragmentToWriteFragment()
    }

    fun onClickReport() {
        navDirections.value = HomeFragmentDirections.actionHomeFragmentToReportFragment()
    }

    fun onClickTabBar() {
        expandClickEvent.value = Unit
    }

    fun updateEmptyRoutines() {
        viewModelScope.launch {
            routineRepository.updateEmptyRoutines()
        }
    }

    fun getStartDate() {
        viewModelScope.launch {
            val dateArray = arrayListOf<WeekOfMonthItem>()
            var startDate = routineRepository.getsStartDate() ?: LocalDate.now()
            val now = LocalDate.now()
            while (startDate.with(DayOfWeek.SUNDAY) <= now.with(DayOfWeek.SUNDAY)) {
                val item = WeekOfMonthItem(
                    WeekOfMonth(
                        startDate.with(DayOfWeek.MONDAY),
                        startDate.with(DayOfWeek.SUNDAY)
                    )
                )
                dateArray.add(item)
                startDate = startDate.plusWeeks(1)
            }
            weekOfMonth.value = dateArray
        }
    }

    fun sendWeekRoutines(selectedDay: LocalDate) {
        if (selectedDay <= LocalDate.now()) {
            viewModelScope.launch {
                dayChannel.send(selectedDay)
            }
        }
    }

    fun onClickRoutineItem(item: HomeItems.RoutineItem) {
        if (item.routine.date >= LocalDate.now().with(DayOfWeek.MONDAY)) {
            navDirections.value =
                HomeFragmentDirections.actionHomeFragmentToWriteFragment(item.routine.id)
        }
    }

    fun onClickWeekOfMonth(date: LocalDate) = View.OnClickListener { itemSelectedListener(date) }

    fun swipeRoutine(item: HomeItems, direction: Int) {
        if (item !is HomeItems.RoutineItem) {
            return
        }

        when {
            item is HomeItems.RoutineItem.EnabledRoutineItem && direction == ItemTouchHelper.START -> setStatus(
                item.routine.realId,
                Routine.Status.SUCCESS
            )
            item is HomeItems.RoutineItem.CompletedRoutineItem && direction == ItemTouchHelper.END -> setStatus(
                item.routine.realId,
                Routine.Status.ENABLE
            )
        }
    }

    private fun weekRateItems(date: LocalDate, routineList: List<Routine>): List<WeekRateItem> {
        val routineMap = routineList.groupBy { it.date }
        return DayOfWeek.values()
            .map { dayOfWeek ->
                val day = date.with(dayOfWeek)
                var rate = 0f
                routineMap[day]?.let { list ->
                    val successCnt =
                        list.filter { it.status == Routine.Status.SUCCESS }.count().toFloat()
                    val totalCnt =
                        list.filter { it.status == Routine.Status.ENABLE }.count() + successCnt
                    rate = successCnt.div(totalCnt)
                }
                WeekRateItem(DayRateItem(day, rate))
            }
    }

    private fun weekItems(date: LocalDate): List<WeekItem> {
        return DayOfWeek.values()
            .map {
                val day = date.with(it)
                WeekItem(DayItem(day, day == date))
            }
    }

    fun setStatus(id: String, status: Routine.Status) {
        viewModelScope.launch {
            routineRepository.updateStatus(id, status)
        }
    }
}
