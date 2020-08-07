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
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate

internal class HomeViewModel @ViewModelInject constructor(
    private val resourcesProvider: ResourcesProvider,
    private val routineRepository: RoutineRepository,
    private val retrospectRepository: RetrospectRepository
) : BaseViewModel() {

    val weekItems = MutableLiveData<List<WeekItem>>()
    val iconGroupItems = MutableLiveData<List<IconGroupItem>>()
    val homeItems = MutableLiveData<List<HomeItems>>()
    val retrospect = MutableLiveData<Retrospect>()
    val retrospectContent = MutableLiveData<String>().apply { value = "" }

    private val dayChannel = ConflatedBroadcastChannel<LocalDate>()
    private val tabBarStatusChannel = ConflatedBroadcastChannel<TabBarStatus>()

    init {
        val now = LocalDate.now()

        viewModelScope.launch {
            dayChannel.send(now)
            tabBarStatusChannel.send(TabBarStatus.RoutineTab)
        }

        viewModelScope.launch {
            dayChannel.asFlow()
                .flatMapLatest {
                    retrospectRepository.getRetrospect(it)
                }
                .collect {
                    retrospect.value = Retrospect(now, "")
                    retrospectContent.value = ""

                    it?.let {
                        retrospect.value = it
                        retrospectContent.value = it.contents
                    }
                    toast.value = "${retrospectContent.value}"
                }
        }

        viewModelScope.launch {
            dayChannel.asFlow()
                .map { weekItems(it) }
                .collect {
                    weekItems.value = it
                }
        }

        viewModelScope.launch {
            dayChannel.asFlow()
                .flatMapLatest {
                    routineRepository.flowRoutinesByDate(it.with(DayOfWeek.MONDAY), it.with(DayOfWeek.SUNDAY))
                }
                .map { convertRoutineItems(it) }
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
                tabBarStatusChannel.asFlow()
            ) { dateRoutines, tabBarStatus ->
                mutableListOf<HomeItems>().apply {
                    add(HomeItems.TabBarItem())

                    when (tabBarStatus) {
                        TabBarStatus.RoutineTab -> {

                            val enableList = mutableListOf<Routine>()
                            val completedList = mutableListOf<Routine>()

                            dateRoutines.forEach {
                                if (it.status == Routine.Status.SUCCESS) {
                                    completedList.add(it)
                                } else if (it.status == Routine.Status.ENABLE) {
                                    enableList.add(it)
                                }
                            }

                            addAll(
                                enableList.map {
                                    HomeItems.RoutineItem.EnabledRoutineItem(it)
                                }
                            )
                            addAll(
                                completedList.map {
                                    HomeItems.RoutineItem.CompletedRoutineItem(it)
                                }
                            )
                        }
                        TabBarStatus.RetrospectTab -> add(HomeItems.Retrospect())
                    }
                }
            }.collect {
                homeItems.value = it
            }
        }
    }

    fun onClickWrite() {
        navDirections.value = HomeFragmentDirections.actionHomeFragmentToWriteFragment()
    }

    fun onClickRoutine() {
        if (checkDataSaved()) {
            viewModelScope.launch { tabBarStatusChannel.send(TabBarStatus.RoutineTab) }
        }
    }

    fun onClickRetrospect() {
        viewModelScope.launch { tabBarStatusChannel.send(TabBarStatus.RetrospectTab) }
    }

    fun sendWeekRoutines(selectedDay: LocalDate) {
        viewModelScope.launch {
            dayChannel.send(selectedDay)
        }
    }

    private fun weekItems(date: LocalDate): List<WeekItem> {
        return DayOfWeek.values().map { day -> WeekItem(date.with(day)) }
    }

    private fun convertRoutineItems(list: List<Routine>): List<IconGroupItem> {
        return list.groupBy { it.date }
            .map { routineMap ->
                IconGroupItem(routineMap.value.map { routine -> IconItem(routine) })
            }
    }

    enum class TabBarStatus {
        RoutineTab,
        RetrospectTab
    }

    fun onClickWriteRetrospect() {
        val contentValue = retrospectContent.value

        if (contentValue.isNullOrBlank()) {
            toast.value = resourcesProvider.getString(R.string.write_empty_toast_message)
            return
        }

        if (contentValue == retrospect.value?.contents) {
            toast.value = resourcesProvider.getString(R.string.not_change_toast_message)
            return
        }

        viewModelScope.launch {
            retrospectRepository.updateRetrospect(Retrospect(dayChannel.value, contentValue))
        }
    }

    private fun checkDataSaved(): Boolean {
        if (retrospectContent.value != retrospect.value?.contents) {
            toast.value = "변경된 내용이 있습니다. 회고 저장 후 이동 해 주세요. 다이얼로그로 바꾸기ㅣ!!!"
            return false
        }

        return true
    }

    fun onClickRoutineItem(item: HomeItems.RoutineItem) {
        navDirections.value = HomeFragmentDirections.actionHomeFragmentToWriteFragment(item.routine.id)
    }

    fun setStatus(id: String, status: Routine.Status) {
        viewModelScope.launch {
            if (status == Routine.Status.ENABLE)
                routineRepository.updateStatus(id, Routine.Status.SUCCESS)
            else
                routineRepository.updateStatus(id, Routine.Status.ENABLE)
        }
    }

    fun successRoutine(position: Int) {
        toast.value = "성공! position : $position"
    }
}
