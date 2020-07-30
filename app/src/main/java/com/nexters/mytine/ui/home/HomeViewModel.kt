package com.nexters.mytine.ui.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nexters.mytine.R
import com.nexters.mytine.base.viewmodel.BaseViewModel
import com.nexters.mytine.data.entity.Retrospect
import com.nexters.mytine.data.entity.Routine
import com.nexters.mytine.data.repository.RetrospectRepository
import com.nexters.mytine.data.repository.RoutineRepository
import com.nexters.mytine.ui.home.icongroup.IconGroupItem
import com.nexters.mytine.ui.home.icongroup.icon.IconItem
import com.nexters.mytine.ui.home.week.WeekItem
import com.nexters.mytine.utils.ResourcesProvider
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.DayOfWeek
import java.time.LocalDate

internal class HomeViewModel @ViewModelInject constructor(
    private val resourcesProvider: ResourcesProvider,
    private val routineRepository: RoutineRepository,
    private val retrospectRepository: RetrospectRepository
) : BaseViewModel() {
    val homeItems = MutableLiveData<List<HomeItems>>()
    val content = MutableLiveData<String>()

    var date: LocalDate = LocalDate.now()
    private var isInRetrospect = false
    private var storedContent: String

    init {
        storedContent = ""
        content.value = ""

        viewModelScope.launch {
            routineRepository.getRoutines()
                .collect { homeItems.value = createHomeItems(it) }

            retrospectRepository.getRetrospect(date).firstOrNull()?.let {
                storedContent = it.contents
            }
        }
    }

    fun onClickWrite() {
        navDirections.value = HomeFragmentDirections.actionHomeFragmentToWriteFragment()
    }

    fun onClickRoutine() {

        if (checkDataSaved()) {
            homeItems.value = mutableListOf<HomeItems>().apply {
                add(HomeItems.RoutineGroupItem(weekItems(), iconGroupItems()))
                add(HomeItems.TabBarItem())
            }
            isInRetrospect = false
        }
    }

    fun onClickRetrospect() {

        if (isInRetrospect)
            return

        homeItems.value = mutableListOf<HomeItems>().apply {
            add(HomeItems.RoutineGroupItem(weekItems(), iconGroupItems()))
            add(HomeItems.TabBarItem())
            add(HomeItems.Retrospect())
        }
        isInRetrospect = true

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

    private fun getRetrospect() = runBlocking {
        setDay()

        retrospectRepository.getRetrospect(date).firstOrNull()?.let {
            content.value = it.contents
            storedContent = it.contents
        }
    }

    fun onClickWriteRetrospect() {

        if (content.value.isNullOrBlank()) {
            toast.value = resourcesProvider.getString(R.string.write_empty_toast_message)
            return
        }

        if (content.value.toString() == storedContent) {
            toast.value = resourcesProvider.getString(R.string.not_change_toast_message)
            return
        }

        viewModelScope.launch {
            retrospectRepository.updateRetrospect(Retrospect(date, content.value!!))
            storedContent = content.value!!
        }
    }

    private fun checkDataSaved(): Boolean {

        if (content.value != storedContent) {
            toast.value = "변경된 내용이 있습니다. 회고 저장 후 이동 해 주세요. 다이얼로그로 바꾸기ㅣ!!!"
            return false
        }

        return true
    }

    private fun setDay() {
        // TODO("탐색 날자 설정. 유진이 코드 연결하기")
        date = LocalDate.now()
    }
}
