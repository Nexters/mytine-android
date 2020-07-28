package com.nexters.mytine.ui.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nexters.mytine.base.viewmodel.BaseViewModel
import com.nexters.mytine.data.entity.Retrospect
import com.nexters.mytine.data.entity.Routine
import com.nexters.mytine.data.repository.RetrospectRepository
import com.nexters.mytine.data.repository.RoutineRepository
import com.nexters.mytine.ui.home.icongroup.IconGroupItem
import com.nexters.mytine.ui.home.icongroup.icon.IconItem
import com.nexters.mytine.ui.home.week.WeekItem
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate

internal class HomeViewModel @ViewModelInject constructor(
    private val routineRepository: RoutineRepository,
    private val retrospectRepository: RetrospectRepository
) : BaseViewModel() {
    val homeItems = MutableLiveData<List<HomeItems>>()
    val content = MutableLiveData<String>()

    var date = LocalDate.now()

    init {
        viewModelScope.launch {
            routineRepository.getRoutines()
                .collect { homeItems.value = createHomeItems(it) }
        }
    }

    fun onClickWrite() {
        navDirections.value = HomeFragmentDirections.actionHomeFragmentToWriteFragment()
    }

    fun onClickRoutine() {
        homeItems.value = mutableListOf<HomeItems>().apply {
            add(HomeItems.RoutineGroupItem(weekItems(), iconGroupItems()))
            add(HomeItems.TabBarItem())
        }
    }

    fun onClickRetrospect() {
        homeItems.value = mutableListOf<HomeItems>().apply {
            add(HomeItems.RoutineGroupItem(weekItems(), iconGroupItems()))
            add(HomeItems.TabBarItem())
            add(HomeItems.Retrospect())
        }

        getRetrospect()
    }

    private fun createHomeItems(routines: List<Routine>): List<HomeItems> {
        return mutableListOf<HomeItems>().apply {
            add(HomeItems.RoutineGroupItem(weekItems(), iconGroupItems()))
            add(HomeItems.TabBarItem())
            addAll(routines.map { HomeItems.RoutineItem(it) })
        }
    }

    private fun weekItems(): List<WeekItem> {
        return DayOfWeek.values().map { WeekItem(it) }
    }

    private fun iconGroupItems(): List<IconGroupItem> {
        return listOf(
            IconGroupItem(iconItems()),
            IconGroupItem(iconItems()),
            IconGroupItem(iconItems()),
            IconGroupItem(iconItems()),
            IconGroupItem(iconItems()),
            IconGroupItem(iconItems()),
            IconGroupItem(iconItems()),
            IconGroupItem(iconItems()),
            IconGroupItem(iconItems()),
            IconGroupItem(iconItems()),
            IconGroupItem(iconItems())
        )
    }

    private fun iconItems(): List<IconItem> {
        return listOf("a", "b", "c", "d", "e", "f", "g").map { IconItem(it) }
    }

    private fun getRetrospect() {
        setDay()

        viewModelScope.launch {
            content.value = retrospectRepository.getRetrospect(date).first().contents
        }
    }

    fun onClickWriteRetrospect() {
        // TODO("변경된 내용이 있을 때만 버튼 활성화")
        viewModelScope.launch {
            retrospectRepository.updateRetrospect(Retrospect(date, content.value!!))
        }
    }

    private fun setDay() {
        // TODO("탐색 날자 설정. 유진이 코드 연결하기")
        date = LocalDate.now()
    }

/*    fun checkDataSaved() {
        lateinit var savedData: String
        viewModelScope.launch {
            savedData = retrospectRepository.getRetrospect(date).first().contents
        }

        if (savedData != content.value) {
        }
    }*/
}
