package com.nexters.mytine.ui.write

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.navigation.NavDirections
import com.nexters.mytine.MainCoroutinesRule
import com.nexters.mytine.R
import com.nexters.mytine.data.repository.RoutineRepository
import com.nexters.mytine.utils.ResourcesProvider
import com.nexters.mytine.utils.navigation.BackDirections
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import java.time.DayOfWeek

@RunWith(MockitoJUnitRunner::class)
internal class WriteViewModelTest {
    @ExperimentalCoroutinesApi
    @get:Rule
    val coroutinesRule = MainCoroutinesRule()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var weekItems: Observer<List<WeekItem>>

    @Mock
    private lateinit var navDirections: Observer<NavDirections>

    @Mock
    private lateinit var toast: Observer<String>

    @Mock
    private lateinit var resourcesProvider: ResourcesProvider

    @Mock
    private lateinit var mockRoutineRepository: RoutineRepository

    private lateinit var viewModel: WriteViewModel

    private val emptyToastMessage = "emptyToastMessage"

    @Before
    fun setup() {
        `when`(resourcesProvider.getString(R.string.write_empty_toast_message)).thenReturn(emptyToastMessage)
        viewModel = WriteViewModel(resourcesProvider, mockRoutineRepository)
        viewModel.weekItems.observeForever(weekItems)
        viewModel.navDirections.observeForever(navDirections)
        viewModel.toast.observeForever(toast)
    }

    @Test
    fun `진입 시 요일들을 선택안된채로 불러오기`() {
        val defaultWeekItems = DayOfWeek.values().map { WeekItem(it) }
        verify(weekItems).onChanged(defaultWeekItems)
    }

    @Test
    fun `요일을 클릭 할 시 해당 요일 선택 처리`() {
        val defaultWeekItems = DayOfWeek.values().map { WeekItem(it) }
        val mondayItem = WeekItem(DayOfWeek.MONDAY, true)
        val thursdayItem = WeekItem(DayOfWeek.THURSDAY, true)
        val mondaySelectedWeekItems = defaultWeekItems.map {
            if (it.dayOfWeek == mondayItem.dayOfWeek) {
                mondayItem
            } else {
                it
            }
        }
        val mondayAndThursdaySelectedWeekItems = mondaySelectedWeekItems.map {
            if (it.dayOfWeek == thursdayItem.dayOfWeek) {
                thursdayItem
            } else {
                it
            }
        }

        verify(weekItems).onChanged(defaultWeekItems)

        viewModel.onClickWeekItem(mondayItem)
        verify(weekItems).onChanged(mondaySelectedWeekItems)

        viewModel.onClickWeekItem(thursdayItem)
        verify(weekItems).onChanged(mondayAndThursdaySelectedWeekItems)

        viewModel.onClickWeekItem(thursdayItem)
        verify(weekItems, times(2)).onChanged(mondaySelectedWeekItems)

        viewModel.onClickWeekItem(mondayItem)
        verify(weekItems, times(2)).onChanged(defaultWeekItems)
    }

    @Test
    fun `제목과 내용이 채워진 채로 루틴 쓰기를 누를 시 루틴 저장 및 뒤로가기`() = runBlocking {
        val emoji = "emoji"
        val name = "name"
        val goal = "goal"
        val selectedDayOfWeeks = listOf(DayOfWeek.MONDAY)

        viewModel.emoji.value = emoji
        viewModel.name.value = name
        viewModel.goal.value = goal
        viewModel.weekItems.value = DayOfWeek.values().map { WeekItem(dayOfWeek = it, selected = selectedDayOfWeeks.contains(it)) }

        viewModel.onClickWrite()

        verify(mockRoutineRepository).updateRoutine(emoji, name, goal, selectedDayOfWeeks, "")
        verify(navDirections).onChanged(BackDirections())
    }

    @Test
    fun `목표가 채워지지 않은 채 루틴 쓰기를 누를 시 루틴 저장 및 뒤로가기`() = runBlocking {
        val emoji = "emoji"
        val name = "name"
        val goal = ""
        val selectedDayOfWeeks = listOf(DayOfWeek.MONDAY)

        viewModel.emoji.value = emoji
        viewModel.name.value = name
        viewModel.goal.value = goal
        viewModel.weekItems.value = DayOfWeek.values().map { WeekItem(dayOfWeek = it, selected = selectedDayOfWeeks.contains(it)) }

        viewModel.onClickWrite()

        verify(mockRoutineRepository).updateRoutine(emoji, name, goal, selectedDayOfWeeks, "")
        verify(navDirections).onChanged(BackDirections())
    }

    @Test
    fun `이모지가 채워지지 않은 채 루틴 쓰기를 누를 시 토스트`() = runBlocking {
        val emoji = ""
        val name = "name"
        val goal = "goal"
        val selectedDayOfWeeks = listOf(DayOfWeek.MONDAY)

        viewModel.emoji.value = emoji
        viewModel.name.value = name
        viewModel.goal.value = goal
        viewModel.weekItems.value = DayOfWeek.values().map { WeekItem(dayOfWeek = it, selected = selectedDayOfWeeks.contains(it)) }

        viewModel.onClickWrite()

        verify(mockRoutineRepository, never()).updateRoutine(emoji, name, goal, selectedDayOfWeeks, "")
        verify(navDirections, never()).onChanged(BackDirections())
        verify(toast).onChanged(emptyToastMessage)
    }

    @Test
    fun `루틴이름이 채워지지 않은 채 루틴 쓰기를 누를 시 토스트`() = runBlocking {
        val emoji = "emoji"
        val name = ""
        val goal = "goal"
        val selectedDayOfWeeks = listOf(DayOfWeek.MONDAY)

        viewModel.emoji.value = emoji
        viewModel.name.value = name
        viewModel.goal.value = goal
        viewModel.weekItems.value = DayOfWeek.values().map { WeekItem(dayOfWeek = it, selected = selectedDayOfWeeks.contains(it)) }

        viewModel.onClickWrite()

        verify(mockRoutineRepository, never()).updateRoutine(emoji, name, goal, selectedDayOfWeeks, "")
        verify(navDirections, never()).onChanged(BackDirections())
        verify(toast).onChanged(emptyToastMessage)
    }

    @Test
    fun `요일이 선택되어지지 않은 채 루틴 쓰기를 누를 시 토스트`() = runBlocking {
        val emoji = "emoji"
        val name = "name"
        val goal = "goal"
        val selectedDayOfWeeks = emptyList<DayOfWeek>()

        viewModel.emoji.value = emoji
        viewModel.name.value = name
        viewModel.goal.value = goal
        viewModel.weekItems.value = DayOfWeek.values().map { WeekItem(dayOfWeek = it, selected = selectedDayOfWeeks.contains(it)) }

        viewModel.onClickWrite()

        verify(mockRoutineRepository, never()).updateRoutine(emoji, name, goal, selectedDayOfWeeks, "")
        verify(navDirections, never()).onChanged(BackDirections())
        verify(toast).onChanged(emptyToastMessage)
    }
}
