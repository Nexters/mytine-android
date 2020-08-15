package com.nexters.mytine.ui.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.ItemTouchHelper
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
import com.nexters.mytine.utils.LiveEvent
import com.nexters.mytine.utils.ResourcesProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

@ExperimentalCoroutinesApi
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
    val isExpanded = MutableLiveData<Unit>()

    val isRetrospectStored = MutableLiveData<Boolean>().apply { value = false }
    val isTabClicked = MutableLiveData<Boolean>().apply { value = true }

    private val dayChannel = ConflatedBroadcastChannel<LocalDate>()
    private val tabBarStatusChannel = ConflatedBroadcastChannel<TabBarStatus>()

    val showExitDialog = LiveEvent<Unit>()

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
                }
        }

        viewModelScope.launch {
            dayChannel.asFlow()
                .flatMapLatest { date ->
                    retrospectRepository
                        .getRetrospectDatesByDate(date.with(DayOfWeek.MONDAY), date.with(DayOfWeek.SUNDAY))
                        .map { weekItems(date, it) }
                }
                .collect { weekItems.value = it }
        }

        viewModelScope.launch {
            dayChannel.asFlow()
                .flatMapLatest { date ->
                    routineRepository
                        .flowRoutinesByDate(date.with(DayOfWeek.MONDAY), date.with(DayOfWeek.SUNDAY))
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
                tabBarStatusChannel.asFlow(),
                dayChannel.asFlow().flatMapLatest { date ->
                    retrospectRepository
                        .getRetrospectDatesByDate(date.with(DayOfWeek.MONDAY), date.with(DayOfWeek.SUNDAY))
                        .map { weekItems(date, it) }
                }
            ) { dateRoutines, tabBarStatus, icons ->
                mutableListOf<HomeItems>().apply {
                    add(HomeItems.TabBarItem())

                    when (tabBarStatus) {
                        TabBarStatus.RoutineTab -> {
                            isTabClicked.value = true

                            if (icons.isNullOrEmpty())
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
                        TabBarStatus.RetrospectTab -> {
                            add(HomeItems.Retrospect())
                            isTabClicked.value = false
                        }
                    }
                }
            }.collect {
                homeItems.value = it
            }
        }
    }

    fun onClickWrite() {
        if (!isRetrospectStored.value!!) {
            navDirections.value = HomeFragmentDirections.actionHomeFragmentToWriteFragment()
        } else {
            showExitDialog.value = Unit
        }
    }

    fun onClickRoutine() {
        if (!isRetrospectStored.value!!) {
            viewModelScope.launch { tabBarStatusChannel.send(TabBarStatus.RoutineTab) }
        } else {
            showExitDialog.value = Unit
        }
    }

    fun onClickLeave() {
        // 루틴탭 눌렀을 때
        viewModelScope.launch { tabBarStatusChannel.send(TabBarStatus.RoutineTab) }

/*        //회고탭에서 날짜 변경되었을 때

        //루틴탭 눌렀을 때
        viewModelScope.launch { tabBarStatusChannel.send(TabBarStatus.RoutineTab) }
        //작성하기 버튼 눌렀을 때
        navDirections.value = HomeFragmentDirections.actionHomeFragmentToWriteFragment()*/

        retrospectContent.value = retrospect.value!!.contents
    }

    fun onClickRetrospect() {
        viewModelScope.launch { tabBarStatusChannel.send(TabBarStatus.RetrospectTab) }
    }

    fun onClickTabBar() {
        isExpanded.value = Unit
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
        if (selectedDay <= LocalDate.now()) {
            viewModelScope.launch {
                dayChannel.send(selectedDay)
            }
        }
    }

    fun onClickWriteRetrospect() {
        if (!isRetrospectStored.value!!) return

        updateRetrospect()
    }

    private fun updateRetrospect() {
        viewModelScope.launch {
            if (retrospectContent.value.isNullOrEmpty()) {
                retrospectRepository.deleteRetrospect(dayChannel.value)
                toast.value = "삭제"
            } else {
                retrospectRepository.updateRetrospect(Retrospect(dayChannel.value, retrospectContent.value!!))
                toast.value = "저장${retrospectContent.value}"
            }
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
                WeekItem(DayItem(day, retrospectSet.contains(day), day == date))
            }
    }

    fun setStatus(id: String, status: Routine.Status) {
        viewModelScope.launch {
            routineRepository.updateStatus(id, status)
        }
    }
}
