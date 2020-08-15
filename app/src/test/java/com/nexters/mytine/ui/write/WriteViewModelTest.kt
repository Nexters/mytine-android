package com.nexters.mytine.ui.write

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.navigation.NavDirections
import com.google.common.truth.Truth.assertThat
import com.nexters.mytine.MainCoroutinesRule
import com.nexters.mytine.data.entity.Routine
import com.nexters.mytine.data.repository.RoutineRepository
import com.nexters.mytine.getValue
import com.nexters.mytine.utils.navigation.BackDirections
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyString
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import java.time.DayOfWeek
import java.time.LocalDate

@RunWith(MockitoJUnitRunner::class)
internal class WriteViewModelTest {
    @ExperimentalCoroutinesApi
    @get:Rule
    val coroutinesRule = MainCoroutinesRule()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var isEditMode: Observer<Boolean>

    @Mock
    private lateinit var emoji: Observer<String>

    @Mock
    private lateinit var name: Observer<String>

    @Mock
    private lateinit var goal: Observer<String>

    @Mock
    private lateinit var weekItems: Observer<List<WeekItem>>

    @Mock
    private lateinit var showErrorEmoji: Observer<Boolean>

    @Mock
    private lateinit var showErrorName: Observer<Boolean>

    @Mock
    private lateinit var showErrorWeek: Observer<Boolean>

    @Mock
    private lateinit var enableWrite: Observer<Boolean>

    @Mock
    private lateinit var showBackDialog: Observer<Unit>

    @Mock
    private lateinit var navDirections: Observer<NavDirections>

    @Mock
    private lateinit var mockRoutineRepository: RoutineRepository
    private lateinit var viewModel: WriteViewModel

    @Before
    fun setup() {
        viewModel = WriteViewModel(mockRoutineRepository)
        viewModel.isEditMode.observeForever(isEditMode)
        viewModel.emoji.observeForever(emoji)
        viewModel.name.observeForever(name)
        viewModel.goal.observeForever(goal)
        viewModel.weekItems.observeForever(weekItems)
        viewModel.showErrorEmoji.observeForever(showErrorEmoji)
        viewModel.showErrorName.observeForever(showErrorName)
        viewModel.showErrorWeek.observeForever(showErrorWeek)
        viewModel.enableWrite.observeForever(enableWrite)
        viewModel.showBackDialog.observeForever(showBackDialog)
        viewModel.navDirections.observeForever(navDirections)
        viewModel.navArgs(WriteFragmentArgs())
    }

    @Test
    fun `쓰기 상태로 진입 시 요일들을 선택안된채로 불러오기`() {
        val defaultWeekItems = DayOfWeek.values().map { WeekItem(it) }
        verify(weekItems).onChanged(defaultWeekItems)
        assertThat(getValue(viewModel.isEditMode)).isFalse()
    }

    @Test
    fun `쓰기화면에서 뒤로가기 시 다이얼로그 띄우기 및 나가기 클릭 시 나가기`() {
        viewModel.onBackPressed()
        verify(showBackDialog).onChanged(Unit)
        viewModel.onClickLeave()
        verify(navDirections).onChanged(BackDirections())
    }

    @Test
    fun `편집 상태로 진입 시 루틴 불러오기`() {
        val fakeEmoji = "emoji"
        val fakeName = "name"
        val fakeGoal = "goal"

        val routineMon = Routine(date = LocalDate.now().with(DayOfWeek.MONDAY), status = Routine.Status.ENABLE, emoji = fakeEmoji, name = fakeName, goal = fakeGoal, order = 0)
        val routineTues = Routine(date = LocalDate.now().with(DayOfWeek.TUESDAY), status = Routine.Status.DISABLE, emoji = fakeEmoji, name = fakeName, goal = fakeGoal, order = 0)
        val routineWed = Routine(date = LocalDate.now().with(DayOfWeek.WEDNESDAY), status = Routine.Status.SUCCESS, emoji = fakeEmoji, name = fakeName, goal = fakeGoal, order = 0)
        val routineThur = Routine(date = LocalDate.now().with(DayOfWeek.THURSDAY), status = Routine.Status.FAIL, emoji = fakeEmoji, name = fakeName, goal = fakeGoal, order = 0)
        val routineFri = Routine(date = LocalDate.now().with(DayOfWeek.FRIDAY), status = Routine.Status.DISABLE, emoji = fakeEmoji, name = fakeName, goal = fakeGoal, order = 0)
        val routineSat = Routine(date = LocalDate.now().with(DayOfWeek.SATURDAY), status = Routine.Status.DISABLE, emoji = fakeEmoji, name = fakeName, goal = fakeGoal, order = 0)
        val routineSun = Routine(date = LocalDate.now().with(DayOfWeek.SUNDAY), status = Routine.Status.ENABLE, emoji = fakeEmoji, name = fakeName, goal = fakeGoal, order = 0)

        val weekRoutines = listOf(routineMon, routineTues, routineWed, routineThur, routineFri, routineSat, routineSun)
        val routineId = routineMon.id

        `when`(mockRoutineRepository.flowRoutinesById(routineId, LocalDate.now().with(DayOfWeek.MONDAY))).thenReturn(flow { emit(weekRoutines) })

        viewModel.navArgs(WriteFragmentArgs(routineId))

        assertThat(getValue(viewModel.isEditMode)).isTrue()
        verify(emoji).onChanged(fakeEmoji)
        verify(name).onChanged(fakeName)
        verify(goal).onChanged(fakeGoal)
        verify(weekItems).onChanged(weekRoutines.map { WeekItem(it.date.dayOfWeek, it.status != Routine.Status.DISABLE) })
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

        viewModel.onClickSave()

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

        viewModel.onClickSave()

        verify(mockRoutineRepository).updateRoutine(emoji, name, goal, selectedDayOfWeeks, "")
        verify(navDirections).onChanged(BackDirections())
    }

    @Test
    fun `삭제 클릭 시 루틴 삭제 및 뒤로가기`() = runBlocking {
        `편집 상태로 진입 시 루틴 불러오기`()

        viewModel.onClickDelete()

        verify(mockRoutineRepository).deleteRoutinesById(anyString())
        verify(navDirections).onChanged(BackDirections())
    }

    @Test
    fun `이모지가 채워지지 않은 채 루틴 쓰기를 누를 시 에러표시`() = runBlocking {
        val emoji = ""
        val name = "name"
        val goal = "goal"
        val selectedDayOfWeeks = listOf(DayOfWeek.MONDAY)

        viewModel.emoji.value = emoji
        viewModel.name.value = name
        viewModel.goal.value = goal
        viewModel.weekItems.value = DayOfWeek.values().map { WeekItem(dayOfWeek = it, selected = selectedDayOfWeeks.contains(it)) }

        viewModel.onClickSave()

        verify(mockRoutineRepository, never()).updateRoutine(emoji, name, goal, selectedDayOfWeeks, "")
        verify(navDirections, never()).onChanged(BackDirections())
        verify(showErrorEmoji).onChanged(true)
        assertThat(getValue(viewModel.enableWrite)).isFalse()
    }

    @Test
    fun `루틴이름이 채워지지 않은 채 루틴 쓰기를 누를 시 에러표시`() = runBlocking {
        val emoji = "emoji"
        val name = ""
        val goal = "goal"
        val selectedDayOfWeeks = listOf(DayOfWeek.MONDAY)

        viewModel.emoji.value = emoji
        viewModel.name.value = name
        viewModel.goal.value = goal
        viewModel.weekItems.value = DayOfWeek.values().map { WeekItem(dayOfWeek = it, selected = selectedDayOfWeeks.contains(it)) }

        viewModel.onClickSave()

        verify(mockRoutineRepository, never()).updateRoutine(emoji, name, goal, selectedDayOfWeeks, "")
        verify(navDirections, never()).onChanged(BackDirections())
        verify(showErrorName).onChanged(true)
        assertThat(getValue(viewModel.enableWrite)).isFalse()
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

        viewModel.onClickSave()

        verify(mockRoutineRepository, never()).updateRoutine(emoji, name, goal, selectedDayOfWeeks, "")
        verify(navDirections, never()).onChanged(BackDirections())
        verify(showErrorWeek).onChanged(true)
        assertThat(getValue(viewModel.enableWrite)).isFalse()
    }
}
