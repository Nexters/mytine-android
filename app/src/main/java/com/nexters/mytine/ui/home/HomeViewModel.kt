package com.nexters.mytine.ui.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.ItemTouchHelper
import com.nexters.mytine.R
import com.nexters.mytine.base.viewmodel.BaseViewModel
import com.nexters.mytine.data.entity.Retrospect
import com.nexters.mytine.data.entity.Routine
import com.nexters.mytine.data.repository.RetrospectRepository
import com.nexters.mytine.data.repository.RoutineRepository
import com.nexters.mytine.ui.home.icongroup.IconGroupItem
import com.nexters.mytine.ui.home.icongroup.icon.IconItem
import com.nexters.mytine.ui.home.week.DayItem
import com.nexters.mytine.ui.home.week.WeekItem
import com.nexters.mytine.ui.home.weekrate.DayRateItem
import com.nexters.mytine.ui.home.weekrate.WeekRateItem
import com.nexters.mytine.utils.ResourcesProvider
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.DayOfWeek
import java.time.LocalDate

internal class HomeViewModel @ViewModelInject constructor(
    private val resourcesProvider: ResourcesProvider,
    private val routineRepository: RoutineRepository,
    private val retrospectRepository: RetrospectRepository
) : BaseViewModel() {

    enum class TabBarStatus {
        RoutineTab,
        RetrospectTab
    }

    val weekItems = MutableLiveData<List<WeekItem>>()
    val weekRateItems = MutableLiveData<List<WeekRateItem>>()
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
                .flatMapLatest { date ->
                    retrospectRepository
                        .getRetrospectDatesByDate(date.with(DayOfWeek.MONDAY), date)
                        .map { weekItems(date, it) }
                }
                .collect { weekItems.value = it }
        }

        viewModelScope.launch {
            dayChannel.asFlow()
                .flatMapLatest { date ->
                    routineRepository
                        .flowRoutinesByDate(date.with(DayOfWeek.MONDAY), date)
                        .map { weekRateItems(date, it) }
                }
                .collect { weekRateItems.value = it }
        }

        viewModelScope.launch {
            dayChannel.asFlow()
                .flatMapLatest { routineRepository.flowRoutinesByDate(it.with(DayOfWeek.MONDAY), it.with(DayOfWeek.SUNDAY)) }
                .map { list ->
                    list.groupBy { it.date }.map { IconGroupItem(it.value.map { routine -> IconItem(routine) }) }
                }
                .collect { iconGroupItems.value = it }
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

    fun getStartDate(): List<WeekOfMonth> = runBlocking {
        val dateArray = arrayListOf<WeekOfMonth>()
        var startDate = routineRepository.getsStartDate() ?: LocalDate.now()
        val now = LocalDate.now()
        while (startDate <= now) {
            dateArray.add(WeekOfMonth(startDate.with(DayOfWeek.MONDAY), startDate.with(DayOfWeek.SUNDAY)))
            startDate = startDate.plusWeeks(1)
        }
        return@runBlocking dateArray
    }

    fun sendWeekRoutines(selectedDay: LocalDate) {
        viewModelScope.launch {
            dayChannel.send(selectedDay)
        }
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

    fun onClickRoutineItem(item: HomeItems.RoutineItem) {
        navDirections.value = HomeFragmentDirections.actionHomeFragmentToWriteFragment(item.routine.id)
    }

    fun swipeRoutine(item: HomeItems, direction: Int) {
        if (item !is HomeItems.RoutineItem) {
            return
        }

        when {
            item is HomeItems.RoutineItem.EnabledRoutineItem && direction == ItemTouchHelper.START -> setStatus(item.routine.realId, Routine.Status.SUCCESS)
            item is HomeItems.RoutineItem.CompletedRoutineItem && direction == ItemTouchHelper.END -> setStatus(item.routine.realId, Routine.Status.ENABLE)
        }
    }

    private fun weekRateItems(date: LocalDate, routineList: List<Routine>): List<WeekRateItem> {
        val routineMap = routineList.groupBy { it.date }
        return DayOfWeek.values()
            .map { dayOfWeek ->
                val day = date.with(dayOfWeek)
                var rate = 0f
                routineMap[day]?.let { list ->
                    val successCnt = list.filter { it.status == Routine.Status.SUCCESS }.count().toFloat()
                    val totalCnt = list.filter { it.status == Routine.Status.ENABLE }.count() + successCnt
                    rate = successCnt.div(totalCnt)
                }
                WeekRateItem(DayRateItem(day, rate))
            }
    }

    private fun weekItems(date: LocalDate, retrospectSet: List<LocalDate>): List<WeekItem> {
        return DayOfWeek.values()
            .map {
                val day = date.with(it)
                WeekItem(DayItem(day, retrospectSet.contains(day)))
            }
    }

    private fun checkDataSaved(): Boolean {
        if (retrospectContent.value != retrospect.value?.contents) {
            toast.value = "변경된 내용이 있습니다. 회고 저장 후 이동 해 주세요. 다이얼로그로 바꾸기ㅣ!!!"
            return false
        }

        return true
    }

    private fun setStatus(id: String, status: Routine.Status) {
        viewModelScope.launch {
            routineRepository.updateStatus(id, status)
        }
    }
}
