package com.nexters.mytine.ui.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nexters.mytine.base.viewmodel.BaseViewModel
import com.nexters.mytine.data.entity.Routine
import com.nexters.mytine.data.repository.RoutineRepository
import com.nexters.mytine.ui.home.icongroup.IconGroupItem
import com.nexters.mytine.ui.home.icongroup.icon.IconItem
import com.nexters.mytine.ui.home.week.WeekItem
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate

internal class HomeViewModel @ViewModelInject constructor(
        private val routineRepository: RoutineRepository
) : BaseViewModel() {
    val homeItems = MutableLiveData<List<HomeItems>>()
    private val weekRoutinesBroadcastChannel = ConflatedBroadcastChannel<List<Routine>>()
    private val tabBarStatusBroadcastChannel = ConflatedBroadcastChannel<TabBarStatus>()

    init {
        viewModelScope.launch {
            sendWeekRoutines(LocalDate.now())
            onClickRoutine()
            initBroadcastChannelEvent()
        }
    }

    fun onClickWrite() {
        navDirections.value = HomeFragmentDirections.actionHomeFragmentToWriteFragment()
    }

    fun onClickRoutine() {
        viewModelScope.launch { tabBarStatusBroadcastChannel.send(TabBarStatus.RoutineTab) }
    }

    fun onClickRetrospect() {
        viewModelScope.launch { tabBarStatusBroadcastChannel.send(TabBarStatus.RetrospectTab) }
    }

    suspend fun loadWeekRoutines(selectedDay: LocalDate): List<Routine> {
        val from = selectedDay.with(DayOfWeek.MONDAY)
        val to = selectedDay.with(DayOfWeek.SUNDAY)
        return routineRepository.getsByDate(from, to)
    }

    fun sendWeekRoutines(selectedDay: LocalDate) {
        viewModelScope.launch {
            weekRoutinesBroadcastChannel.send(loadWeekRoutines(selectedDay))
        }
    }

    private suspend fun initBroadcastChannelEvent() {
        combine(weekRoutinesBroadcastChannel.asFlow(), tabBarStatusBroadcastChannel.asFlow()) { routineList, tabBarStatus ->
            mutableListOf<HomeItems>().apply {
                add(HomeItems.RoutineGroupItem(weekItems(), convertRoutineItems(routineList)))
                add(HomeItems.TabBarItem())
                when (tabBarStatus) {
                    TabBarStatus.RoutineTab -> addAll(routineList.map { HomeItems.RoutineItem(it) })
                    TabBarStatus.RetrospectTab -> add(HomeItems.Retrospect())
                }
            }
        }.collect {
            homeItems.value = it
        }
    }

    private fun weekItems(): List<WeekItem> {
        val now = LocalDate.now()
        return DayOfWeek.values().map { day -> WeekItem(now.with(day)) }
    }

    private fun convertRoutineItems(list: List<Routine>): List<IconGroupItem> {
        return list.groupBy { it.id }
                .map { routineMap ->
                    IconGroupItem(routineMap.value.map { r -> IconItem(r) })
                }
    }

    enum class TabBarStatus {
        RoutineTab,
        RetrospectTab
    }
}
