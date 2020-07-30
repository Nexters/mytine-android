package com.nexters.mytine.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.nexters.mytine.MainCoroutinesRule
import com.nexters.mytine.anyObj
import com.nexters.mytine.data.entity.Routine
import com.nexters.mytine.data.repository.RoutineRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import java.time.DayOfWeek
import java.time.LocalDate

@RunWith(MockitoJUnitRunner::class)
internal class HomeViewModelTest {
    @ExperimentalCoroutinesApi
    @get:Rule
    val coroutinesRule = MainCoroutinesRule()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var homeItems: Observer<List<HomeItems>>

    @Mock
    private lateinit var navDirections: Observer<NavDirections>

    @Mock
    private lateinit var mockRoutineRepository: RoutineRepository

    @Mock
    private lateinit var mockRoutine: Routine

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        `when`(mockRoutineRepository.flowRoutines(anyObj())).thenReturn(flow { emit(listOf(mockRoutine)) })

        viewModel = HomeViewModel(mockRoutineRepository)
        viewModel.navDirections.observeForever(navDirections)
        viewModel.homeItems.observeForever(homeItems)
    }

    @Test
    fun `루틴추가 버튼 클릭 시 루틴입력 화면으로 이동`() {
        viewModel.onClickWrite()

        verify(navDirections).onChanged(HomeFragmentDirections.actionHomeFragmentToWriteFragment())
    }

    @Test
    fun `루틴 탭 터치 시 루틴 탭으로 이동`() {
        viewModel.viewModelScope.launch {
            viewModel.onClickRoutine()
            assert(viewModel.homeItems.value?.contains(HomeItems.RoutineItem(any())) == true)
        }
    }

    @Test
    fun `회고 탭 터치 시 회고 탭으로 이동`() {
        viewModel.viewModelScope.launch {
            viewModel.onClickRetrospect()
            assert(viewModel.homeItems.value?.contains(HomeItems.Retrospect()) == true)
        }
    }

    @Test
    fun `주간 루틴 리스트 변경 시, 홈 변경`() {
        viewModel.viewModelScope.launch {
            val now = LocalDate.now()
            viewModel.sendWeekRoutines(now)
            verify(viewModel).loadWeekRoutines(now)
        }
    }

    @Test
    fun `날짜에 해당하는 주간날짜 리스트 로드`() {
        viewModel.viewModelScope.launch {
            val now = LocalDate.now()
            val from = now.with(DayOfWeek.MONDAY)
            val to = now.with(DayOfWeek.FRIDAY)
            `when`(mockRoutineRepository.getsByDate(from, to))
                .thenReturn(viewModel.loadWeekRoutines(LocalDate.now()))
        }
    }
}
