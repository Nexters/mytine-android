package com.nexters.mytine.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.nexters.mytine.MainCoroutinesRule
import com.nexters.mytine.data.MyTineRoomDatabase
import com.nexters.mytine.data.dao.RoutineDao
import com.nexters.mytine.data.entity.Routine
import com.nexters.mytine.di.DatabaseModule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify
import org.robolectric.RobolectricTestRunner
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

@HiltAndroidTest
@UninstallModules(DatabaseModule::class)
@RunWith(RobolectricTestRunner::class)
internal class RoutineRepositoryTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutinesRule = MainCoroutinesRule()

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var database: MyTineRoomDatabase

    private lateinit var routineRepository: RoutineRepository
    private lateinit var routineDao: RoutineDao
    private lateinit var routine: Routine

    @Before
    fun setup() {
        hiltRule.inject()

        routineDao = spy(database.routineDao())
        routineRepository = RoutineRepositoryImpl(routineDao)

        routine = Routine(date = LocalDate.now(), emoji = "emoji", name = "name", goal = "goal", status = Routine.Status.ENABLE, order = 0)
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun `flowRoutines 테스트`() = runBlocking {
        assertThat(routineRepository.flowRoutines(routine.date).first()).isEmpty()
        routineDao.upsert(routine)
        assertThat(routineRepository.flowRoutines(routine.date).first()).isEqualTo(listOf(routine))
    }

    @Test
    fun `updateRoutine 테스트`() = runBlocking {
        // 1. 루틴을 새로 추가할 경우 테스트
        val emoji = "emoji"
        val name = "name"
        val goal = "goal"
        val now = LocalDate.now()
        val selectedDayOfWeeks = listOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY)

        routineRepository.updateRoutine(emoji, name, goal, selectedDayOfWeeks)

        val routines = DayOfWeek.values().map {
            val status = if (selectedDayOfWeeks.contains(it)) {
                Routine.Status.ENABLE
            } else {
                Routine.Status.DISABLE
            }

            Routine(
                date = now.with(it),
                emoji = emoji,
                name = name,
                goal = goal,
                status = status,
                order = 0
            )
        }

        verify(routineDao).getsByDate(now.with(DayOfWeek.MONDAY), now.with(DayOfWeek.SUNDAY))
        verify(routineDao).upserts(routines)
        assertThat(routineDao.getsByDate(now.with(DayOfWeek.MONDAY), now.with(DayOfWeek.SUNDAY))).isEqualTo(routines)

        // 2. 루틴을 업데이트 할 경우 테스트
        val id = routines.first().id
        val newEmoji = "newEmoji"
        routineRepository.updateRoutine(newEmoji, name, goal, selectedDayOfWeeks, id = id)

        val newRoutines = DayOfWeek.values().map {
            val status = if (selectedDayOfWeeks.contains(it)) {
                Routine.Status.ENABLE
            } else {
                Routine.Status.DISABLE
            }

            Routine(
                date = now.with(it),
                emoji = newEmoji,
                name = name,
                goal = goal,
                status = status,
                order = 0
            )
        }

        verify(routineDao).getsById(id)
        verify(routineDao).deleteAndUpdate(id, now.with(DayOfWeek.MONDAY), newRoutines)
        assertThat(routineDao.getsByDate(now.with(DayOfWeek.MONDAY), now.with(DayOfWeek.SUNDAY))).isEqualTo(newRoutines)
    }
}
