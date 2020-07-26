package com.nexters.mytine.ui.write

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.navigation.NavDirections
import com.nexters.mytine.MainCoroutinesRule
import com.nexters.mytine.data.repository.RoutineRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
internal class WriteViewModelTest {
    @ExperimentalCoroutinesApi
    @get:Rule
    val coroutinesRule = MainCoroutinesRule()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var navDirections: Observer<NavDirections>

    @Mock
    private lateinit var mockRoutineRepository: RoutineRepository

    private lateinit var viewModel: WriteViewModel

    @Before
    fun setup() {
        viewModel = WriteViewModel(mockRoutineRepository)
        viewModel.navDirections.observeForever(navDirections)
    }

//    @Test
//    fun `제목과 내용이 채워진 채로 루틴 쓰기를 누를 시 루틴 저장 및 뒤로가기`() = runBlocking {
//        val title = "아이들"
//        val content = "화이팅"
//        viewModel.title.value = title
//        viewModel.content.value = content
//
//        viewModel.onClickWrite()
//
//        verify(mockRoutineRepository).updateRoutine(Routine(title = title, content = content))
//        verify(navDirections).onChanged(BackDirections())
//    }
//
//    @Test
//    fun `제목이 채워지지 않은 채 루틴 기를 누를 시 아무일도 없다`() = runBlocking {
//        val title = ""
//        val content = "화이팅"
//        viewModel.title.value = title
//        viewModel.content.value = content
//
//        viewModel.onClickWrite()
//
//        verify(mockRoutineRepository, never()).updateRoutine(Routine(title = title, content = content))
//        verify(navDirections, never()).onChanged(BackDirections())
//    }
//
//    @Test
//    fun `내용이 채워지지 않은 채 루틴 기를 누를 시 아무일도 없다`() = runBlocking {
//        val title = "아이들"
//        val content = ""
//        viewModel.title.value = title
//        viewModel.content.value = content
//
//        viewModel.onClickWrite()
//
//        verify(mockRoutineRepository, never()).updateRoutine(Routine(title = title, content = content))
//        verify(navDirections, never()).onChanged(BackDirections())
//    }
}
