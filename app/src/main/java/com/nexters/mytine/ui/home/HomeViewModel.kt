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
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
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

    private val weekRoutinesBroadcastChannel = ConflatedBroadcastChannel<List<Routine>>()
    private val tabBarStatusBroadcastChannel = ConflatedBroadcastChannel<TabBarStatus>()

    init {
        storedContent = ""
        content.value = ""

        viewModelScope.launch {
            routineRepository.flowRoutines(LocalDate.now())
                .collect { homeItems.value = createHomeItems(it) }

            // 앱 진입시 처리 => 유진이에게 state 넘겨받기
            retrospectRepository.getRetrospect(date).firstOrNull()?.let {
                storedContent = it.contents
            }
            sendWeekRoutines(LocalDate.now())
            onClickRoutine()
            initBroadcastChannelEvent()
        }
    }

    fun onClickWrite() {
        navDirections.value = HomeFragmentDirections.actionHomeFragmentToWriteFragment()
    }

    fun onClickRoutine() {
        if (checkDataSaved()) {
            viewModelScope.launch { tabBarStatusBroadcastChannel.send(TabBarStatus.RoutineTab) }
            isInRetrospect = false
        }
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
