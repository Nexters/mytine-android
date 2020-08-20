package com.nexters.mytine.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.navigation.NavDirections
import com.nexters.mytine.MainCoroutinesRule
import com.nexters.mytine.anyObj
import com.nexters.mytine.data.entity.Retrospect
import com.nexters.mytine.data.entity.Routine
import com.nexters.mytine.data.repository.RetrospectRepository
import com.nexters.mytine.data.repository.RoutineRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
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

    @Mock
    private lateinit var mockRetrospectRepository: RetrospectRepository

    @Mock
    private lateinit var mockRetrospect: Retrospect

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() = runBlocking {
        `when`(mockRoutineRepository.flowRoutinesByDate(anyObj(), anyObj())).thenReturn(flow { emit(listOf(mockRoutine)) })

        `when`(mockRoutine.id).thenReturn("id")
        `when`(mockRoutine.realId).thenReturn("realId")
        `when`(mockRetrospect.contents).thenReturn("")
        `when`(mockRetrospectRepository.getRetrospect(anyObj())).thenReturn(flow { emit(mockRetrospect) })
        viewModel = HomeViewModel(mockRoutineRepository, mockRetrospectRepository)
        viewModel.navDirections.observeForever(navDirections)
        viewModel.homeItems.observeForever(homeItems)
    }

    @Test
    fun `루틴추가 버튼 클릭 시 루틴입력 화면으로 이동`() {
        viewModel.onClickWrite()

        verify(navDirections).onChanged(HomeFragmentDirections.actionHomeFragmentToWriteFragment())
    }

    @Test
    fun `월간회고 버튼 클릭 시 월간회고 화면으로 이동`() {
        viewModel.onClickReport()

        verify(navDirections).onChanged(HomeFragmentDirections.actionHomeFragmentToReportFragment())
    }

    @Test
    fun `(Given) 해당 날짜에 작성한 회고가 있을 경우 (When) 회고 버튼 클릭 시 (Then) 내용 불러오기`() {
    }

    @Test
    fun `(Given) 변경된 내용이 없을 때 (When) 회고 작성 버튼 클릭 시 (Then) 토스트`() {
    }

    @Test
    fun `(Given) 회고를 작성하지 않은 채 (When) 회고 작성 버튼 클릭 시 (Then) 토스트`() {
    }

    @Test
    fun `(Given) 회고 작성 후 (When) 회고 작성 버튼 클릭 시 (Then) 회고 저장`() {
    }

    @Test
    fun `(Given) 회고 작성 중 (When) 루틴 탭으로 이동 할 경우 (Then) 토스트`() {
    }

    @Test
    fun `루틴 탭 터치 시 루틴 탭으로 이동`() {
    }

    @Test
    fun `회고 탭 터치 시 회고 탭으로 이동`() {}

    @Test
    fun `날짜에 해당하는 주간날짜 리스트 로드`() {
    }
}
