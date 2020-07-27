package com.nexters.mytine.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.navigation.NavDirections
import com.nexters.mytine.MainCoroutinesRule
import com.nexters.mytine.anyObj
import com.nexters.mytine.data.entity.Routine
import com.nexters.mytine.data.repository.RoutineRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.any
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

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
    fun `진입 시 홈 아이템 불러오기`() {
        verify(homeItems).onChanged(any())
    }

    @Test
    fun `루틴추가 버튼 클릭 시 루틴입력 화면으로 이동`() {
        viewModel.onClickWrite()

        verify(navDirections).onChanged(HomeFragmentDirections.actionHomeFragmentToWriteFragment())
    }
}
