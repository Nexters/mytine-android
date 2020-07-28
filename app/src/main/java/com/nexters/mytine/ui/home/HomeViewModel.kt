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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate

internal class HomeViewModel @ViewModelInject constructor(
        private val routineRepository: RoutineRepository
) : BaseViewModel() {
    val homeItems = MutableLiveData<List<HomeItems>>()

    init {
        viewModelScope.launch {
            routineRepository.flowRoutines(LocalDate.now())
                    .collect { homeItems.value = createHomeItems(it) }
        }
    }

    fun onClickWrite() {
        navDirections.value = HomeFragmentDirections.actionHomeFragmentToWriteFragment()
    }

    fun onClickRoutine() {
        viewModelScope.launch {
            homeItems.value = mutableListOf<HomeItems>().apply {
                add(HomeItems.RoutineGroupItem(weekItems(), iconGroupItems()))
                add(HomeItems.TabBarItem())
            }
        }
    }

    fun onClickRetrospect() {
        viewModelScope.launch {
            homeItems.value = mutableListOf<HomeItems>().apply {
                add(HomeItems.RoutineGroupItem(weekItems(), iconGroupItems()))
                add(HomeItems.TabBarItem())
                add(HomeItems.Retrospect())
            }
        }
    }

    private suspend fun createHomeItems(routines: List<Routine>): List<HomeItems> {
        return mutableListOf<HomeItems>().apply {
            add(HomeItems.RoutineGroupItem(weekItems(), iconGroupItems()))
            add(HomeItems.TabBarItem())
            addAll(routines.map { HomeItems.RoutineItem(it) })
        }
    }

    private fun weekItems(): List<WeekItem> {
        return DayOfWeek.values().map { WeekItem(it) }
    }

    private suspend fun iconGroupItems(): List<IconGroupItem> {
        return iconItems().map { IconGroupItem(it) }
    }

    private suspend fun iconItems(): List<List<IconItem>> {
        val now = LocalDate.now()
        return routineRepository.getsByDate(now.with(DayOfWeek.MONDAY), now.with(DayOfWeek.SUNDAY))
                .groupBy { it.id }
                .map { routineMap ->
                    routineMap.value.map { r -> IconItem(r) }
                }
    }
}
